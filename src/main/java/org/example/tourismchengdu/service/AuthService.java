package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.auth.LoginRequest;
import org.example.tourismchengdu.dto.auth.LoginResponse;
import org.example.tourismchengdu.dto.auth.RegisterRequest;

public interface AuthService {

    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
