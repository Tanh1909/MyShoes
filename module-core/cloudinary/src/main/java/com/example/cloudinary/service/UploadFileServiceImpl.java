package com.example.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.example.cloudinary.constant.UploadFileError;
import com.example.common.exception.AppException;
import com.example.common.template.RxTemplate;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.example.common.template.RxTemplate.rxSchedulerIo;

@Service
@RequiredArgsConstructor
public class UploadFileServiceImpl implements IUploadFileService {
    private final Cloudinary cloudinary;

    @Override
    public String blockingUpload(MultipartFile multipartFile) {
        try {
            Map<?,?> data=cloudinary.uploader().upload(multipartFile.getBytes(),Map.of());
            return (String) data.get("url");
        } catch (IOException e) {
            throw new AppException(UploadFileError.UPLOAD_FILE_FAIL);
        }
    }

    @Override
    public Single<String> rxUpload(MultipartFile multipartFile) {
        return rxSchedulerIo(() -> blockingUpload(multipartFile));
    }
}
