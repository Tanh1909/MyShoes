package com.example.security.config.service;

import com.example.common.exception.AppException;
import com.example.security.config.constant.AuthErrorCode;
import com.example.security.data.mapper.UserMapper;
import com.example.security.data.response.UserDetailResponse;
import com.example.security.repository.impl.RoleRepository;
import com.example.security.repository.impl.UserRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailResponse userDetailResponse = Single.zip(
                userRepository.findByUsername(username),
                roleRepository.findByUsername(username),
                (userOptional, roles) -> {
                    if (userOptional.isPresent()) throw new AppException(AuthErrorCode.WRONG_USERNAME_OR_PASSWORD);
                    UserDetailResponse result = userMapper.toUserDetailResponse(userOptional.get());
                    result.setRoles(roles.stream()
                            .map(role -> role.getRoleName())
                            .collect(Collectors.toSet()));
                    return result;
                }
        ).blockingGet();
        return UserDetailImpl.builder(userDetailResponse);
    }
}
