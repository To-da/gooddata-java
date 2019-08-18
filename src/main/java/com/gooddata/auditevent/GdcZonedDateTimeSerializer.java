package com.gooddata.auditevent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

public class GdcZonedDateTimeSerializer extends ZonedDateTimeSerializer {

    public static final DateTimeFormatter GDC_ISO_LOCAL_TIME;
    static {
        GDC_ISO_LOCAL_TIME = new DateTimeFormatterBuilder()
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(SECOND_OF_MINUTE, 2)
                .optionalStart()
                .appendFraction(NANO_OF_SECOND, 3, 9, true)
                .toFormatter();
    }

    public static final DateTimeFormatter GDC_ISO_LOCAL_DATE_TIME;
    static {
        GDC_ISO_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(ISO_LOCAL_DATE)
                .appendLiteral('T')
                .append(GDC_ISO_LOCAL_TIME)
                .toFormatter();
    }

    public static final DateTimeFormatter GDC_ISO_OFFSET_DATE_TIME;
    static {
        GDC_ISO_OFFSET_DATE_TIME = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(GDC_ISO_LOCAL_DATE_TIME)
                .appendOffsetId()
                .toFormatter();
    }

    public GdcZonedDateTimeSerializer() {
        super(GDC_ISO_OFFSET_DATE_TIME);
    }

    @Override
    public void serialize(final ZonedDateTime value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
//        super.serialize(value, g, provider);
    }
}
