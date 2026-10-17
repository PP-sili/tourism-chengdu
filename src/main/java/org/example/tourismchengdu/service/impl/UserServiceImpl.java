package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.constant.OrderConstants;
import org.example.tourismchengdu.dto.user.OrderStatusCountVO;
import org.example.tourismchengdu.dto.user.UpdateAvatarRequest;
import org.example.tourismchengdu.dto.user.UpdateProfileRequest;
import org.example.tourismchengdu.dto.user.UserOverviewVO;
import org.example.tourismchengdu.dto.user.UserProfileVO;
import org.example.tourismchengdu.entity.AppUser;
import org.example.tourismchengdu.entity.OrderStatusCount;
import org.example.tourismchengdu.mapper.AppUserMapper;
import org.example.tourismchengdu.mapper.CartMapper;
import org.example.tourismchengdu.mapper.OrderMapper;
import org.example.tourismchengdu.mapper.PostMapper;
import org.example.tourismchengdu.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserMapper appUserMapper;
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final PostMapper postMapper;

    public UserServiceImpl(AppUserMapper appUserMapper,
                           OrderMapper orderMapper,
                           CartMapper cartMapper,
                           PostMapper postMapper) {
        this.appUserMapper = appUserMapper;
        this.orderMapper = orderMapper;
        this.cartMapper = cartMapper;
        this.postMapper = postMapper;
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        AppUser user = requireUser(userId);
        return UserProfileVO.from(user);
    }

    @Override
    public UserProfileVO updateProfile(Long userId, UpdateProfileRequest request) {
        AppUser user = requireUser(userId);

        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        appUserMapper.updateProfile(user);
        return UserProfileVO.from(requireUser(userId));
    }

    @Override
    public UserProfileVO updateAvatar(Long userId, UpdateAvatarRequest request) {
        AppUser user = requireUser(userId);
        user.setAvatar(request.getAvatar());
        appUserMapper.updateProfile(user);
        return UserProfileVO.from(requireUser(userId));
    }

    @Override
    public UserOverviewVO getOverview(Long userId) {
        AppUser user = requireUser(userId);

        UserOverviewVO overview = new UserOverviewVO();
        overview.setProfile(UserProfileVO.from(user));
        overview.setTotalLikes(postMapper.sumLikeCountByUserId(userId));
        overview.setPostCount(postMapper.countByUserId(userId));
        overview.setCartItemCount(cartMapper.countByUserId(userId));
        overview.setTicketOrders(buildOrderStats(orderMapper.countByUserIdGroupByStatus(userId, OrderConstants.ORDER_TYPE_TICKET)));
        overview.setMallOrders(buildOrderStats(orderMapper.countByUserIdGroupByStatus(userId, OrderConstants.ORDER_TYPE_MALL)));
        return overview;
    }

    private OrderStatusCountVO buildOrderStats(List<OrderStatusCount> counts) {
        Map<String, Long> statusCounts = new HashMap<>();
        long total = 0;
        for (OrderStatusCount item : counts) {
            statusCounts.put(item.getStatus(), item.getCount());
            total += item.getCount();
        }
        OrderStatusCountVO vo = new OrderStatusCountVO();
        vo.setStatusCounts(statusCounts);
        vo.setTotal(total);
        return vo;
    }

    private AppUser requireUser(Long userId) {
        AppUser user = appUserMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }
}
