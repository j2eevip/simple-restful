package com.github.ly.sr.privacy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class PrivacySerialize extends JsonSerializer<String> implements ContextualSerializer {
    private final PrivacyColumn privacyColumn;

    public PrivacySerialize() {
        this(null);
    }

    public PrivacySerialize(PrivacyColumn privacyColumn) {
        this.privacyColumn = privacyColumn;
    }

    @Override
    public void serialize(String fieldValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (Objects.isNull(fieldValue) || fieldValue.isBlank()) {
            jsonGenerator.writeObject(null);
            return;
        }

        jsonGenerator.writeString(privacyFieldValue(fieldValue));
    }

    private String privacyFieldValue(String fieldValue) {
        PrivacyTypeEnum type = Objects.nonNull(privacyColumn) ? privacyColumn.type() : null;
        if (Objects.nonNull(type)) {
            switch (type) {
                case CHINESE_NAME -> {
                    return PrivacyUtil.chineseName(fieldValue);
                }
                case ID_CARD -> {
                    return PrivacyUtil.idCardNum(fieldValue);
                }
                case FIXED_PHONE -> {
                    return PrivacyUtil.fixedPhone(fieldValue);
                }
                case MOBILE_PHONE -> {
                    return PrivacyUtil.mobilePhone(fieldValue);
                }
                case ADDRESS -> {
                    return PrivacyUtil.address(fieldValue);
                }
                case EMAIL -> {
                    return PrivacyUtil.email(fieldValue);
                }
                case BANK_CARD -> {
                    return PrivacyUtil.bankCard(fieldValue);
                }
                case PASSWORD -> {
                    return PrivacyUtil.password(fieldValue);
                }
                case KEY -> {
                    return PrivacyUtil.key(fieldValue);
                }
                case CUSTOMER -> {
                    int prefixNoMaskLen = privacyColumn.prefixNoMaskLen();
                    int suffixNoMaskLen = privacyColumn.suffixNoMaskLen();
                    String maskStr = privacyColumn.maskStr();
                    return PrivacyUtil.desValue(fieldValue, prefixNoMaskLen, suffixNoMaskLen, maskStr);
                }
            }
        }
        return fieldValue;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (Objects.nonNull(beanProperty)) {
            if (Objects.equals(String.class, beanProperty.getType().getRawClass())) {
                PrivacyColumn privacyColumn;
                if (Objects.isNull(privacyColumn = beanProperty.getAnnotation(PrivacyColumn.class))) {
                    privacyColumn = beanProperty.getContextAnnotation(PrivacyColumn.class);
                }
                if (Objects.nonNull(privacyColumn)) {
                    return new PrivacySerialize(privacyColumn);
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}
