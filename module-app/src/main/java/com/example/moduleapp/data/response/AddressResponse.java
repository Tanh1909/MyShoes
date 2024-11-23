package com.example.moduleapp.data.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddressResponse {
    private Integer id;
    private String content;
    private String phone;
    private boolean isDefault;
    private LocalDateTime createdAt;

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
