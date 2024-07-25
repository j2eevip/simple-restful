package com.github.ly.demo.model;

import com.github.ly.sr.privacy.PrivacyColumn;
import com.github.ly.sr.privacy.PrivacyTypeEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class User {
    @PrivacyColumn(type = PrivacyTypeEnum.CHINESE_NAME)
    private String name;
}
