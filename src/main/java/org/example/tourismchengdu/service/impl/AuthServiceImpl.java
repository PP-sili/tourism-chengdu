package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.dto.auth.LoginRequest;
import org.example.tourismchengdu.dto.auth.LoginResponse;
import org.example.tourismchengdu.dto.auth.RegisterRequest;
import org.example.tourismchengdu.dto.user.UserProfileVO;
import org.example.tourismchengdu.entity.AppUser;
import org.example.tourismchengdu.mapper.AppUserMapper;
import org.example.tourismchengdu.security.JwtTokenProvider;
import org.example.tourismchengdu.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_NICKNAME = "蓉城游客";

    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AppUserMapper appUserMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        AppUser existing = appUserMapper.findByPhone(request.getPhone());
        if (existing != null) {
            throw new BusinessException(ResultCode.CONFLICT, "手机号已注册");
        }

        AppUser user = new AppUser();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : DEFAULT_NICKNAME);
        user.setStatus(1);
        appUserMapper.insert(user);

        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        AppUser user = appUserMapper.findByPhone(request.getPhone());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "手机号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        appUserMapper.updateLastLoginTime(user.getId());
        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(AppUser user) {
        String token = jwtTokenProvider.generateToken(user.getId(), user.getPhone());
        return new LoginResponse(token, UserProfileVO.from(user));
    }
}
