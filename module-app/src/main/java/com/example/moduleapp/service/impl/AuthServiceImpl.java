package com.example.moduleapp.service.impl;

import com.example.authconfig.config.constant.AuthErrorCode;
import com.example.authconfig.utils.JwtUtils;
import com.example.common.context.SecurityContext;
import com.example.common.context.SimpleSecurityUser;
import com.example.common.context.UserPrincipal;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.ERole;
import com.example.moduleapp.data.mapper.UserMapper;
import com.example.moduleapp.data.request.AuthRequest;
import com.example.moduleapp.data.request.UserCreationRequest;
import com.example.moduleapp.data.response.AuthResponse;
import com.example.moduleapp.data.response.UserDetailResponse;
import com.example.moduleapp.data.response.UserResponse;
import com.example.moduleapp.model.tables.pojos.Role;
import com.example.moduleapp.model.tables.pojos.User;
import com.example.moduleapp.model.tables.pojos.UserRole;
import com.example.moduleapp.repository.impl.RoleRepository;
import com.example.moduleapp.repository.impl.UserRepository;
import com.example.moduleapp.repository.impl.UserRoleRepository;
import com.example.moduleapp.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;


    @Override
    public Single<AuthResponse> login(AuthRequest authRequest) {
        return Single.zip(
                userRepository.findByUsername(authRequest.getUsername()),
                roleRepository.findByUsername(authRequest.getUsername()),
                ((userOptional, roles) -> {
                    if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(authRequest.getPassword())) {
                        throw new AppException(AuthErrorCode.WRONG_USERNAME_OR_PASSWORD);
                    }
                    User user = userOptional.get();
                    List<String> listRoles = roles.stream().map(Role::getRoleName).toList();
                    UserDetailResponse userDetailResponse = userMapper.toUserDetailResponse(user);
                    userDetailResponse.setRoles(listRoles);
                    SimpleSecurityUser simpleSecurityUser = userMapper.toSimpleSecurityUser(userDetailResponse);
                    String token = jwtUtils.generateToken(simpleSecurityUser, false);
                    String refreshToken = jwtUtils.generateToken(simpleSecurityUser, true);
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
        return Single.zip(
                userRepository.existByUsername(userCreationRequest.getUsername()),
                roleRepository.findByRoleName(ERole.ROLE_USER.name()),
                (isExist, roleOptional) -> {
                    if (isExist) {
                        throw new AppException(AuthErrorCode.ALREADY_EXISTS, "USERNAME");
                    }
                    if (roleOptional.isEmpty()) {
                        throw new AppException(AuthErrorCode.NOT_FOUND, "ROLE");
                    }
                    User user = userMapper.toUser(userCreationRequest);
                    user.setPassword(userCreationRequest.getPassword());
                    return userRepository.insertReturn(user)
                            .flatMap(userInsert -> {
                                UserRole userRole = new UserRole();
                                userRole.setUserId(userInsert.getId());
                                userRole.setRoleId(roleOptional.get().getId());
                                return userRoleRepository.insertReturn(userRole)
                                        .map(integer -> userMapper.toUserResponse(userInsert));
                            });
                }
        ).flatMap(userResponseSingle -> userResponseSingle);
    }

    @Override
    public UserPrincipal getCurrentUser() {
        UserPrincipal userPrincipal = SecurityContext.getUserPrincipal();
        if (userPrincipal == null) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
        return userPrincipal;
    }

    @Override
    public void validateOwner(Long userId) {
        UserPrincipal currentUser = getCurrentUser();
        if (!currentUser.getUserInfo().getId().equals(userId.intValue())) {
            throw new AppException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}
