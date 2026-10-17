package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.dto.scene.SceneDetailVO;
import org.example.tourismchengdu.dto.scene.SceneListVO;
import org.example.tourismchengdu.dto.scene.TicketStatItemVO;
import org.example.tourismchengdu.dto.scene.VisitorStatItemVO;
import org.example.tourismchengdu.entity.Scene;
import org.example.tourismchengdu.entity.TicketStock;
import org.example.tourismchengdu.entity.TicketStockSlot;
import org.example.tourismchengdu.mapper.SceneMapper;
import org.example.tourismchengdu.mapper.StatDailyMapper;
import org.example.tourismchengdu.mapper.TicketStockMapper;
import org.example.tourismchengdu.mapper.TicketStockSlotMapper;
import org.example.tourismchengdu.service.SceneService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SceneServiceImpl implements SceneService {

    private static final int STAT_DAYS = 7;

    private final SceneMapper sceneMapper;
    private final StatDailyMapper statDailyMapper;
    private final TicketStockMapper ticketStockMapper;
    private final TicketStockSlotMapper ticketStockSlotMapper;

    public SceneServiceImpl(SceneMapper sceneMapper,
                            StatDailyMapper statDailyMapper,
                            TicketStockMapper ticketStockMapper,
                            TicketStockSlotMapper ticketStockSlotMapper) {
        this.sceneMapper = sceneMapper;
        this.statDailyMapper = statDailyMapper;
        this.ticketStockMapper = ticketStockMapper;
        this.ticketStockSlotMapper = ticketStockSlotMapper;
    }

    @Override
    public List<SceneListVO> listScenes() {
        return sceneMapper.findAllVisible().stream()
                .map(SceneListVO::from)
                .toList();
    }

    @Override
    public SceneDetailVO getSceneDetail(Long id) {
        Scene scene = requireScene(id);
        return SceneDetailVO.from(scene);
    }

    @Override
    public List<VisitorStatItemVO> getVisitorStats(Long id) {
        requireScene(id);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(STAT_DAYS - 1L);
        return statDailyMapper.findBySceneIdAndDateRange(id, startDate, endDate).stream()
                .map(VisitorStatItemVO::from)
                .toList();
    }

    @Override
    public List<TicketStatItemVO> getTicketStats(Long id) {
        Scene scene = requireScene(id);
        if (!SceneListVO.isBookable(scene.getTicketPrice())) {
            return List.of();
        }
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(STAT_DAYS - 1L);
        List<TicketStock> stocks = ticketStockMapper.findBySceneIdAndDateRange(id, startDate, endDate);
        List<TicketStockSlot> slots = ticketStockSlotMapper.findBySceneIdAndDateRange(id, startDate, endDate);
        return TicketStatItemVO.buildList(stocks, slots);
    }

    private Scene requireScene(Long id) {
        Scene scene = sceneMapper.findById(id);
        if (scene == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "景点不存在");
        }
        return scene;
    }
}
