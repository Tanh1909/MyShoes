package com.example.moduleapp.service.impl;

import com.example.common.exception.AppException;
import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.config.constant.AppErrorCode;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.data.mapper.UserMapper;
import com.example.moduleapp.data.request.ImageRequest;
import com.example.moduleapp.data.request.UserUpdateRequest;
import com.example.moduleapp.data.response.UserResponse;
import com.example.moduleapp.model.tables.pojos.User;
import com.example.moduleapp.repository.impl.UserRepository;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.ImageService;
import com.example.moduleapp.service.UserService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.common.config.constant.CommonConstant.SUCCESS;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ImageService imageService;
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
        User user = userRepository.findByIdBlocking(userId)
                .orElseThrow(() -> new AppException(AppErrorCode.NOT_FOUND, "USER ID"));
        userMapper.toUser(user, userUpdateRequest);
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setId(userUpdateRequest.getAvatarId());
        imageRequest.setPrimary(true);
        List<ImageRequest> images = List.of(imageRequest);
        imageService.updateImagesBlocking(images, userId, ImageEnum.USER);
        userRepository.updateBlocking(userId, user);
        return Single.just(SUCCESS);
    }
}
