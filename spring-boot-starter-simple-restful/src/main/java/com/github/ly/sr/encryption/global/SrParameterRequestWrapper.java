package com.github.ly.sr.encryption.global;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.github.ly.sr.SrConstant;
import com.github.ly.sr.encryption.method.EncryptMode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Map;

public class SrParameterRequestWrapper extends HttpServletRequestWrapper {
    public SrParameterRequestWrapper(final HttpServletRequest request, final EncryptMode encryptMode, final String privateKey) {
        // 将request交给父类，以便于调用对应方法的时候，将其输出，其实父亲类的实现方式和第一种new的方式类似
        super(request);

        //将参数表，赋予给当前的Map以便于持有request中的参数
        //解析
        String decryptParam = request.getParameter(SrConstant.DECRYPT_PARAM_NAME);
        String requestBody = encryptMode.getDecryptStr(decryptParam, privateKey);
        Map<String, Object> requestMap = JSONObject.parseObject(requestBody, new TypeReference<>() {
        });
        for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String getParameter(String name) {//重写getParameter，代表参数从当前类中的map获取
        return (String) getRequest().getAttribute(name);
    }

    public String[] getParameterValues(String name) {//同上
        return (String[]) getRequest().getAttribute(name);
    }
}
