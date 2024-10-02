package com.example.cloudinary.constant;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public interface UploadFileError extends ErrorCodeBase {
    ErrorCode UPLOAD_FILE_FAIL = ErrorCode.builder().code(1010).message("Upload file fail").httpStatus(HttpStatus.BAD_REQUEST).build();
}
