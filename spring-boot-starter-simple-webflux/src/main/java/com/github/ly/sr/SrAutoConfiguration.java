package com.github.ly.sr;

import java.util.Collections;
import java.util.List;

import com.github.ly.sr.encryption.global.SrEncryptionFilter;
import com.github.ly.sr.encryption.method.DecryptRequestBody;
import com.github.ly.sr.exception.SrExceptionWrapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

@Slf4j
@Lazy(false)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SrProperties.class)
public class SrAutoConfiguration implements InitializingBean {
	private final ServerProperties serverProperties;

	private final ApplicationContext applicationContext;

	private final WebProperties webProperties;

	private final List<ViewResolver> viewResolvers;

	private final ServerCodecConfigurer serverCodecConfigurer;

	public SrAutoConfiguration(ServerProperties serverProperties,
			WebProperties webProperties,
			ObjectProvider<List<ViewResolver>> viewResolversProvider,
			ServerCodecConfigurer serverCodecConfigurer,
			ApplicationContext applicationContext) {
		this.serverProperties = serverProperties;
		this.applicationContext = applicationContext;
		this.webProperties = webProperties;
		this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
		this.serverCodecConfigurer = serverCodecConfigurer;
	}
    @Bean
	public SrExceptionWrapper exceptionAdvice(ErrorAttributes errorAttributes) {
		SrExceptionWrapper srExceptionAdvice = new SrExceptionWrapper(errorAttributes,
				this.webProperties.getResources(),
				this.serverProperties.getError(),
				this.applicationContext);
		srExceptionAdvice.setViewResolvers(this.viewResolvers);
		srExceptionAdvice.setMessageWriters(this.serverCodecConfigurer.getWriters());
		srExceptionAdvice.setMessageReaders(this.serverCodecConfigurer.getReaders());
		return srExceptionAdvice;
    }

    @Bean
	public SrGlobalWebFilter srGlobalWebFilter() {
		return new SrGlobalWebFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "simple-restful", name = "request-body-encryption", havingValue = "true")
    public DecryptRequestBody decryptRequestBody(SrProperties properties) {
        return new DecryptRequestBody(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "simple-restful", name = "global-encryption", havingValue = "true")
    public SrEncryptionFilter encryptionFilter(SrProperties properties) {
        return new SrEncryptionFilter(properties.getSkipPaths(), properties.getEncryptMode(), properties.getPrivateKey());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("simple restful loaded..");
    }
}
