package com.gooddata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.function.Consumer;

/**
 * Helper class exposing base for configuring {@link ObjectMapper} and exposing access to
 * immutable {@link ObjectReader} & {@link ObjectWriter}.
 * It's thread safe.
 */
public class ObjectMapperProvider {

    /**
     * Mutable {@link ObjectMapper} is private and used only as base for immutable {@link ObjectReader} & {@link ObjectWriter}.
     */
    private final ObjectMapper mapper;

    protected Consumer<ObjectMapper> mapperConfig = mapper -> {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

//        DateTimeFormatter GDC_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
//                .parseCaseInsensitive()
//                .append(ISO_LOCAL_DATE)
//                .appendLiteral(' ')
//                .append(ISO_LOCAL_TIME)
//                .toFormatter();
//
//        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(GDC_LOCAL_DATE_TIME));
        mapper.registerModule(javaTimeModule);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    };

    private ObjectMapperProvider() {
        this.mapper = initMapper();
        configureMapper(mapperConfig);
    }

    /**
     * can be configured before use
     *
     * @return generic {@link ObjectReader} instance
     */
    public ObjectReader getReader() {
        return this.mapper.reader();
    }

    /**
     * can be configured before use
     *
     * @return generic {@link ObjectMapper} instance
     */
    public ObjectWriter getWriter() {
        return this.mapper.writer();
    }

    public static ObjectMapperProvider instance() {
        return new ObjectMapperProvider();
    }

    protected ObjectMapper initMapper() {
        return new ObjectMapper();
    }

    private void configureMapper(final Consumer<ObjectMapper> action) {
        action.accept(mapper);
    }
}

