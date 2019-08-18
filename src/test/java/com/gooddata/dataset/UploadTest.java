/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gooddata.executeafm.IdentifierObjQualifier;
import com.gooddata.executeafm.afm.AbsoluteDateFilter;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class UploadTest {

    @Test
    public void testAbsoluteDateFilter() throws JsonProcessingException {
        final AbsoluteDateFilter absoluteDateFilter = new AbsoluteDateFilter(new IdentifierObjQualifier("date.attr"), LocalDate.of(2017, 9, 25), LocalDate.of(2017, 9, 26));
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        System.out.println(mapper.writer().writeValueAsString(absoluteDateFilter));

    }

    //TODO erase
    @Test
    public void testName() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofLocal(LocalDateTime.of(1999, 1, 3, 23, 59, 59), ZoneOffset.UTC, ZoneOffset.UTC);

        System.out.println(zonedDateTime.toLocalDateTime());
        System.out.println(ZoneOffset.systemDefault());
    }

    //TODO erase
    @Test
    public void testDateFormat() throws IOException {
         final ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule=new JavaTimeModule();
        // Hack time module to allow 'Z' at the end of string (i.e. javascript json's)
//        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        mapper.registerModule(javaTimeModule);
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ZonedDateTime localDateTime = mapper.reader().readValue("2012-03-20T14:31:05.003Z");
        System.out.println(localDateTime);
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final Upload upload = readObjectFromResource("/dataset/uploads/upload.json", Upload.class);

        assertThat(upload, notNullValue());
        assertThat(upload.getMessage(), is("some upload message"));
        assertThat(upload.getProgress(), closeTo(1, 0.0001));
        assertThat(upload.getStatus(), is("OK"));
        assertThat(upload.getUploadMode(), is(UploadMode.INCREMENTAL));
        assertThat(upload.getUri(), is("/gdc/md/project/data/upload/123"));
        assertThat(upload.getCreatedAt(), is(LocalDateTime.of(2016, 4, 8, 12, 55, 21)));
        assertThat(upload.getSize(), is(130501));
        assertThat(upload.getProcessedAt(), is(LocalDateTime.of(2016, 4, 8, 12, 55, 25)));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Upload upload = readObjectFromResource("/dataset/uploads/upload.json", Upload.class);

        assertThat(upload.toString(), matchesPattern(Upload.class.getSimpleName() + "\\[.*\\]"));
    }
}