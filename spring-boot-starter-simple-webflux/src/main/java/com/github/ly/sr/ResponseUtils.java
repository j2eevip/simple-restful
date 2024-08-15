package com.github.ly.sr;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.github.ly.model.SrResponseBody;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

public class ResponseUtils {
	private static String dataBuffersToStringBuilder(List<? extends DataBuffer> dataBuffers) {
		StringBuilder responseStrBuilder = new StringBuilder();
		dataBuffers.forEach(dataBuffer -> {
			byte[] content = new byte[dataBuffer.readableByteCount()];
			dataBuffer.read(content);
			responseStrBuilder.append(new String(content, StandardCharsets.UTF_8));
		});
		return responseStrBuilder.toString();
	}

	private static byte[] genErrorResponseAsBytes(Exception e) {
		SrResponseBody failed = SrResponseBody.failed(e.getMessage());
		return JSON.toJSONBytes(failed);
	}

	public static Publisher<? extends DataBuffer> genFluxBody(
			ServerWebExchange exchange,
			Flux<? extends DataBuffer> fluxBody) {
		final ServerHttpResponse originalResponse = exchange.getResponse();
		final DataBufferFactory dataBufferFactory = originalResponse.bufferFactory();
		return fluxBody.buffer().map(dataBuffers -> {
			String responseStr = dataBuffersToStringBuilder(dataBuffers);
			byte[] bytes;
			try {
				Object responseData = JSON.parseObject(responseStr, Object.class);
				SrResponseBody apiResponse = SrResponseBody.success(responseData);
				bytes = JSON.toJSONBytes(apiResponse);
			}
			catch (Exception e) {
				bytes = genErrorResponseAsBytes(e);
			}
			originalResponse.getHeaders().setContentLength(bytes.length);
			originalResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			return dataBufferFactory.wrap(bytes);
		});

	}

	public static Publisher<? extends DataBuffer> genMonoBody(
			ServerWebExchange exchange,
			Mono<? extends DataBuffer> monoBody) {
		final ServerHttpResponse originalResponse = exchange.getResponse();
		final DataBufferFactory dataBufferFactory = originalResponse.bufferFactory();
		return monoBody.map(dataBuffer -> {
			String responseStr = dataBuffersToStringBuilder(List.of(dataBuffer));
			byte[] bytes = {};
			try {
				SrResponseBody apiResponse;
				Object responseData = JSON.parseObject(responseStr, Object.class);
				if (responseData instanceof SrResponseBody responseBody) {
					apiResponse = responseBody;
				}
				else {
					apiResponse = SrResponseBody.success(responseData);
				}
				bytes = JSON.toJSONBytes(apiResponse);
			}
			catch (Exception e) {
				bytes = genErrorResponseAsBytes(e);
			}


			originalResponse.getHeaders().setContentLength(bytes.length);
			originalResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			return dataBufferFactory.wrap(bytes);
		});
	}
}