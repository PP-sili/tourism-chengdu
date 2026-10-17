package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.dto.itinerary.ItineraryDayVO;
import org.example.tourismchengdu.dto.itinerary.ItinerarySceneItemVO;
import org.example.tourismchengdu.dto.itinerary.UpdateItineraryRequest;
import org.example.tourismchengdu.entity.Itinerary;
import org.example.tourismchengdu.entity.Order;
import org.example.tourismchengdu.entity.Scene;
import org.example.tourismchengdu.mapper.ItineraryMapper;
import org.example.tourismchengdu.mapper.OrderMapper;
import org.example.tourismchengdu.mapper.SceneMapper;
import org.example.tourismchengdu.service.ItineraryService;
import org.example.tourismchengdu.util.JsonUtils;
import org.example.tourismchengdu.util.TimeSlotUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    private final OrderMapper orderMapper;
    private final ItineraryMapper itineraryMapper;
    private final SceneMapper sceneMapper;

    public ItineraryServiceImpl(OrderMapper orderMapper,
                                ItineraryMapper itineraryMapper,
                                SceneMapper sceneMapper) {
        this.orderMapper = orderMapper;
        this.itineraryMapper = itineraryMapper;
        this.sceneMapper = sceneMapper;
    }

    @Override
    public List<ItineraryDayVO> listItinerary(Long userId) {
        LocalDate today = LocalDate.now();
        List<Order> orders = orderMapper.findPendingUseTicketOrdersFromDate(userId, today);
        if (orders.isEmpty()) {
            return List.of();
        }

        Map<LocalDate, List<Order>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(Order::getVisitDate));

        Map<LocalDate, Itinerary> savedMap = itineraryMapper.findByUserIdFromDate(userId, today).stream()
                .collect(Collectors.toMap(Itinerary::getVisitDate, item -> item, (a, b) -> b));

        return ordersByDate.keySet().stream()
                .sorted()
                .map(date -> buildDayVO(date, ordersByDate.get(date), savedMap.get(date)))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItineraryDayVO updateItinerary(Long userId, LocalDate visitDate, UpdateItineraryRequest request) {
        if (visitDate.isBefore(LocalDate.now())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不能修改已过期行程");
        }

        List<Order> dayOrders = orderMapper.findPendingUseTicketOrdersByUserAndDate(userId, visitDate);
        if (dayOrders.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该日期没有待使用的门票订单");
        }

        validateSceneOrder(request.getSceneOrder(), dayOrders);

        Itinerary existing = itineraryMapper.findByUserIdAndDate(userId, visitDate);
        if (existing == null) {
            Itinerary itinerary = new Itinerary();
            itinerary.setUserId(userId);
            itinerary.setVisitDate(visitDate);
            itinerary.setSceneOrder(JsonUtils.toJson(request.getSceneOrder()));
            itinerary.setNote(request.getNote());
            itineraryMapper.insert(itinerary);
        } else {
            existing.setSceneOrder(JsonUtils.toJson(request.getSceneOrder()));
            existing.setNote(request.getNote());
            itineraryMapper.update(existing);
        }

        Itinerary saved = itineraryMapper.findByUserIdAndDate(userId, visitDate);
        return buildDayVO(visitDate, dayOrders, saved);
    }

    private ItineraryDayVO buildDayVO(LocalDate visitDate, List<Order> dayOrders, Itinerary saved) {
        List<Order> autoSorted = dayOrders.stream()
                .sorted(Comparator.comparingInt(order -> TimeSlotUtils.sortWeight(order.getVisitTimeSlot())))
                .toList();

        List<ItinerarySceneItemVO> scenes;
        if (saved != null && saved.getSceneOrder() != null && !saved.getSceneOrder().isBlank()) {
            scenes = applyCustomOrder(JsonUtils.parseLongList(saved.getSceneOrder()), autoSorted);
        } else {
            scenes = autoSorted.stream().map(this::toSceneItem).toList();
        }

        ItineraryDayVO vo = new ItineraryDayVO();
        vo.setVisitDate(visitDate);
        vo.setNote(saved != null ? saved.getNote() : null);
        vo.setScenes(scenes);
        return vo;
    }

    private List<ItinerarySceneItemVO> applyCustomOrder(List<Long> customSceneIds, List<Order> orders) {
        List<Order> remaining = new ArrayList<>(orders);
        List<ItinerarySceneItemVO> result = new ArrayList<>();

        for (Long sceneId : customSceneIds) {
            Iterator<Order> iterator = remaining.iterator();
            while (iterator.hasNext()) {
                Order order = iterator.next();
                if (sceneId.equals(order.getSceneId())) {
                    result.add(toSceneItem(order));
                    iterator.remove();
                    break;
                }
            }
        }

        remaining.stream()
                .sorted(Comparator.comparingInt(order -> TimeSlotUtils.sortWeight(order.getVisitTimeSlot())))
                .map(this::toSceneItem)
                .forEach(result::add);
        return result;
    }

    private ItinerarySceneItemVO toSceneItem(Order order) {
        ItinerarySceneItemVO item = new ItinerarySceneItemVO();
        item.setSceneId(order.getSceneId());
        item.setSceneName(order.getSceneName());
        item.setOrderNo(order.getOrderNo());
        item.setVisitTimeSlot(order.getVisitTimeSlot());
        item.setVisitTimeSlotText(TimeSlotUtils.toText(order.getVisitTimeSlot()));

        Scene scene = sceneMapper.findById(order.getSceneId());
        if (scene != null) {
            item.setSuggestedDuration(scene.getSuggestedDuration());
        }
        return item;
    }

    private void validateSceneOrder(List<Long> sceneOrder, List<Order> orders) {
        List<Long> expected = orders.stream().map(Order::getSceneId).sorted().toList();
        List<Long> actual = new ArrayList<>(sceneOrder);
        actual.sort(Long::compareTo);
        if (!expected.equals(actual)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "景点排序必须与当日门票订单一致");
        }
    }
}
