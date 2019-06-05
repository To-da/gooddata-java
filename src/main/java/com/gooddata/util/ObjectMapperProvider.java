package com.gooddata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.function.Consumer;

public class ObjectMapperProvider {

    /**
     * Mutable {@link ObjectMapper} is private and used only as base for immutable {@link ObjectReader} & {@link ObjectMapper}.
     */
    private final ObjectMapper mapper;

    protected Consumer<ObjectMapper> mapperConfig = mapper -> {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.registerModule(new JavaTimeModule());
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

