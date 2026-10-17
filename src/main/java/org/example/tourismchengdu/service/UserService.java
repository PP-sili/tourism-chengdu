package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.user.UpdateAvatarRequest;
import org.example.tourismchengdu.dto.user.UpdateProfileRequest;
import org.example.tourismchengdu.dto.user.UserOverviewVO;
import org.example.tourismchengdu.dto.user.UserProfileVO;

public interface UserService {

    UserProfileVO getProfile(Long userId);

    UserProfileVO updateProfile(Long userId, UpdateProfileRequest request);

    UserProfileVO updateAvatar(Long userId, UpdateAvatarRequest request);

    UserOverviewVO getOverview(Long userId);
}
