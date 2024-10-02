package com.example.cloudinary.service;

import io.reactivex.rxjava3.core.Single;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {
    String blockingUpload(MultipartFile multipartFile);
    Single<String> rxUpload(MultipartFile multipartFile);
}
