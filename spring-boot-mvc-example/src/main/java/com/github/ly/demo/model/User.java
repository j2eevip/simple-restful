package com.github.ly.demo.model;

import com.github.ly.enums.PrivacyTypeEnum;
import com.github.ly.sr.privacy.PrivacyColumn;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class User {
    @PrivacyColumn(type = PrivacyTypeEnum.CHINESE_NAME)
    private String name;
}
