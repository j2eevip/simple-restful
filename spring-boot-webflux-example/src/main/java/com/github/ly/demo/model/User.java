package com.github.ly.demo.model;

import com.github.ly.annotation.PrivacyColumn;
import com.github.ly.enums.PrivacyTypeEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class User {
    @PrivacyColumn(type = PrivacyTypeEnum.CHINESE_NAME)
    private String name;
}
