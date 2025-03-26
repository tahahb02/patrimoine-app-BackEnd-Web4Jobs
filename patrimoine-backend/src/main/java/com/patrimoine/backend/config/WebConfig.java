    package com.patrimoine.backend.config;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.converter.ByteArrayHttpMessageConverter;
    import org.springframework.http.converter.FormHttpMessageConverter;
    import org.springframework.http.converter.HttpMessageConverter;
    import org.springframework.http.converter.ResourceHttpMessageConverter;
    import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
    import org.springframework.web.multipart.MultipartResolver;
    import org.springframework.web.multipart.support.StandardServletMultipartResolver;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    import java.util.List;

    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:3000") // Autoriser React
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:uploads/");
        }

        @Bean
        public MultipartResolver multipartResolver() {
            return new StandardServletMultipartResolver();
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new ByteArrayHttpMessageConverter());
            converters.add(new ResourceHttpMessageConverter());
            converters.add(new FormHttpMessageConverter());
            converters.add(new MappingJackson2HttpMessageConverter());
        }
    }