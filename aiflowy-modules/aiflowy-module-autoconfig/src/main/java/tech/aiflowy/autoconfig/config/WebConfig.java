package tech.aiflowy.autoconfig.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.common.web.error.GlobalErrorResolver;
import tech.aiflowy.core.dict.DictManager;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(httpMessageConverter -> {
            List<MediaType> supportedMediaTypes = httpMessageConverter.getSupportedMediaTypes();
            return supportedMediaTypes.contains(MediaType.APPLICATION_JSON);
        });
        //使用 fastjson 进行序列化
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();

        //设置兼容 web，把 BigInteger 转换为 String，防止精度丢失
        fastJsonHttpMessageConverter.getFastJsonConfig().setSerializerFeatures(
                SerializerFeature.BrowserCompatible, //bigInteger 等自适应浏览器
                SerializerFeature.DisableCircularReferenceDetect //取消循环引用的，否则当有应用一个对象时，使用 $ref 替代
        );
        converters.add(fastJsonHttpMessageConverter);
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new GlobalErrorResolver());
    }

    @Bean
    public org.springframework.web.filter.CorsFilter getCorsFilter(){
        CorsConfiguration cors = new CorsConfiguration();
        // 1,允许任何来源
        // springboot2.4后，当allowCredentials为true时，
        // allowingOrigins不能包含特殊值"*"，因为无法在“ Access-Control-Allow-Origin”响应标头上设置。
        // 要允许凭据具有一组来源，请明确列出它们或考虑改用"allowedOriginPatterns"。
        cors.setAllowedOriginPatterns(Collections.singletonList("*"));
        //2,允许任何请求头
        cors.addAllowedHeader(CorsConfiguration.ALL);
        //3,允许任何方法
        cors.addAllowedMethod(CorsConfiguration.ALL);
        //4,允许凭证
        cors.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",cors);
        return new CorsFilter(source);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartup() {
        DictManager dictManager = SpringContextUtil.getBean(DictManager.class);
        System.out.println("onApplicationStartup >>>>>>" + dictManager);
    }

}
