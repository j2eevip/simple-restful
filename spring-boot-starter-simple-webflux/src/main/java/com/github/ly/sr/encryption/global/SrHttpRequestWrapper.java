package com.github.ly.sr.encryption.global;

import com.github.ly.sr.encryption.method.EncryptMode;
import io.micrometer.common.util.StringUtils;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SrHttpRequestWrapper extends ServerHttpRequestDecorator {
    private final String requestBody;

    public SrHttpRequestWrapper(final ServerHttpRequest delegate, final EncryptMode encryptMode, final String privateKey) throws IOException {
        super(delegate);
        byte[] rawBody = StreamUtils.copyToByteArray(delegate.getBody().blockFirst().asInputStream());
        requestBody = readBody(rawBody, encryptMode, privateKey);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(this.requestBody.length());
        buffer.write(this.requestBody.getBytes(StandardCharsets.UTF_8));
        return Flux.just(buffer);
    }

    @Override
    public HttpHeaders getHeaders() {
        // 必须 new，不能直接操作 super.getHeaders()（readonly）
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(super.getHeaders());
        headers.setContentLength(this.requestBody.length());
        return headers;
    }

    /**
     * @return body json string
     */
    public String bodyString() {
        return this.requestBody;
    }

    private static String readBody(final byte[] rawBody, final EncryptMode encryptMode, final String privateKey) throws IOException {
        String param = new String(rawBody, StandardCharsets.UTF_8);
        if (StringUtils.isBlank(param)) {
            return param;
        }
        return encryptMode.getDecryptStr(param, privateKey);
    }
}
