package com.example.moduleapp.service.impl;

import com.example.cloudinary.service.IUploadFileService;
import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.data.mapper.UserMapper;
import com.example.moduleapp.data.request.UserUpdateRequest;
import com.example.moduleapp.data.response.UserResponse;
import com.example.moduleapp.model.tables.pojos.User;
import com.example.moduleapp.repository.impl.UserRepository;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.UserService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final IUploadFileService uploadFileService;
    private final AuthService authService;

    @Override
    public Single<UserResponse> getProfile() {
        Integer userId = authService.getCurrentUser().getUserInfo().getId();
        return userRepository.findById(userId)
                .map(userOptional -> {
                    User user = ValidateUtils.getOptionalValue(userOptional, User.class);
                    return userMapper.toUserResponse(user);
                });
    }

    @Override
    public Single<String> updateProfile(UserUpdateRequest userUpdateRequest) {
        Integer userId = authService.getCurrentUser().getUserInfo().getId();
        return userRepository.findById(userId)
                .flatMap(userOptional -> {
                    User user = ValidateUtils.getOptionalValue(userOptional, User.class);
                    userMapper.toUser(user, userUpdateRequest);
                    if (userUpdateRequest.getAvatar() != null) {
                        String avatarUrl = uploadFileService.blockingUpload(userUpdateRequest.getAvatar());
                        user.setAvatar(avatarUrl);
                    }
                    return userRepository.update(userId, user);
                }).map(integer -> "SUCCESS");
    }
}
