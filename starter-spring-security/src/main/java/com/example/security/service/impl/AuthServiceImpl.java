package com.example.security.service.impl;

import com.example.common.exception.AppException;
import com.example.security.config.constant.AuthErrorCode;
import com.example.security.data.mapper.UserMapper;
import com.example.security.data.request.AuthRequest;
import com.example.security.data.request.UserCreationRequest;
import com.example.security.data.response.AuthResponse;
import com.example.security.data.response.UserDetailResponse;
import com.example.security.data.response.UserResponse;
import com.example.security.model.tables.pojos.Role;
import com.example.security.model.tables.pojos.User;
import com.example.security.repository.impl.RoleRepository;
import com.example.security.repository.impl.UserRepository;
import com.example.security.service.AuthService;
import com.example.security.utils.JwtUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
//    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public Single<AuthResponse> login(AuthRequest authRequest) {
        return Single.zip(
                userRepository.findByUsername(authRequest.getUsername()),
                roleRepository.findByUsername(authRequest.getUsername()),
                ((userOptional, roles) -> {
//                    if (userOptional.isEmpty() || !passwordEncoder.matches(authRequest.getPassword(), userOptional.get().getPassword()))
//                        throw new AppException(AuthErrorCode.WRONG_USERNAME_OR_PASSWORD);
                    User user = userOptional.get();
                    UserDetailResponse userDetailResponse = userMapper.toUserDetailResponse(user);
                    userDetailResponse.setRoles(roles.stream().map(Role::getRoleName).collect(Collectors.toSet()));
                    String token = jwtUtils.generateToken(userDetailResponse, false);
                    String refreshToken = jwtUtils.generateToken(userDetailResponse, true);
                    return AuthResponse.builder()
                            .token(token)
                            .user(userDetailResponse)
                            .refreshToken(refreshToken)
                            .build();
                }));
    }

    @Override
    public Single<AuthResponse> refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public Single<UserResponse> signUp(UserCreationRequest userCreationRequest) {
        return userRepository.existByUsername(userCreationRequest.getUsername())
                .flatMap(isExist -> {
                    if (isExist) {
                        throw new AppException(AuthErrorCode.ALREADY_EXISTS, "USERNAME");
                    }
                    User user = userMapper.toUser(userCreationRequest);
//                    user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));
                    return userRepository.insertReturn(user);
                })
                .map(userMapper::toUserResponse);
    }

    @Override
    public Single<String> logout(String token) {
        return null;
    }

    @Override
    public User getCurrentUser() {
        return null;
    }
}
