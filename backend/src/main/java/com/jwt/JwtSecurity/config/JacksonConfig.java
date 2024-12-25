package com.jwt.JwtSecurity.config;//package com.jwt.JwtSecurity.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.hibernate.type.descriptor.WrapperOptions;
//import org.hibernate.type.descriptor.java.JavaType;
//import org.hibernate.type.format.FormatMapper;
//import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class JacksonConfig implements FormatMapper {
//
//    private final FormatMapper delegate;
//
//    public JacksonConfig() {
//        ObjectMapper objectMapper = createObjectMapper();
//        delegate = new JacksonJsonFormatMapper(objectMapper);
//    }
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        return objectMapper;
//    }
//
//    private static ObjectMapper createObjectMapper() {
//        return new ObjectMapper()
//                .registerModule(new JavaTimeModule())
//                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//    }
//
//    @Override
//    public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions wrapperOptions) {
//        return delegate.fromString(charSequence, javaType, wrapperOptions);
//    }
//
//    @Override
//    public <T> String toString(T value, JavaType<T> javaType, WrapperOptions wrapperOptions) {
//        return delegate.toString(value, javaType, wrapperOptions);
//
//    }
//}
