package com.example.common.config.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PageConstant {
    DEFAULT_PAGE(0),
    DEFAULT_SIZE(7);
    int value;
}