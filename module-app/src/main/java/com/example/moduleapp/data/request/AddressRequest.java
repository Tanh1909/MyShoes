package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {
    private String content;
    private String phone;
    private boolean isDefault;

    public boolean getIsDefault() {
        return isDefault;
    }
}
