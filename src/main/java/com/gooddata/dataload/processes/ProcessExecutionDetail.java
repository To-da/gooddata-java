/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.gdc.ErrorStructure;
import com.gooddata.util.GoodDataToStringBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static com.gooddata.util.Validate.notNullState;

/**
 * Dataload process execution detail. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("executionDetail")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessExecutionDetail {

    private static final String LOG_LINK = "log";
    private static final String SELF_LINK = "self";
    private static final String EXECUTION_LINK = "poll";
    private static final String STATUS_OK = "OK";
    private final String status;

    private final LocalDateTime created;
    private final LocalDateTime started;
    private final LocalDateTime updated;
    private final LocalDateTime finished;

    private final ErrorStructure error;
    private final Map<String,String> links;

    @JsonCreator
    private ProcessExecutionDetail(@JsonProperty("status") String status,
                                   @JsonProperty("created") //    TODO - ISO: @JsonDeserialize(using = ISODateTimeDeserializer.class)
                                           LocalDateTime created,
                                   @JsonProperty("started") //    TODO - ISO: @JsonDeserialize(using = ISODateTimeDeserializer.class)
                                           LocalDateTime started,
                                   @JsonProperty("updated") //    TODO - ISO: @JsonDeserialize(using = ISODateTimeDeserializer.class)
                                           LocalDateTime updated,
                                   @JsonProperty("finished") //    TODO - ISO: @JsonDeserialize(using = ISODateTimeDeserializer.class)
                                           LocalDateTime finished,
                                   @JsonProperty("error") ErrorStructure error,
                                   @JsonProperty("links") Map<String, String> links) {
        this.status = notEmpty(status, "status");
        this.created = notNull(created, "created");
        this.started = started;
        this.updated = updated;
        this.finished = finished;
        this.error = error;
        this.links = links;
    }

    public String getStatus() {
        return status;
    }

    //    TODO - ISO:     @JsonSerialize(using = ISODateTimeSerializer.class)
    public LocalDateTime getCreated() {
        return created;
    }

    //    TODO - ISO:     @JsonSerialize(using = ISODateTimeSerializer.class)
    public LocalDateTime getStarted() {
        return started;
    }

    //    TODO - ISO:    @JsonSerialize(using = ISODateTimeSerializer.class)
    public LocalDateTime getUpdated() {
        return updated;
    }

    //    TODO - ISO:    @JsonSerialize(using = ISODateTimeSerializer.class)
    public LocalDateTime getFinished() {
        return finished;
    }

    public ErrorStructure getError() {
        return error;
    }

    /**
     * @return log URI string
     * @deprecated use {@link #getLogUri()} instead
     */
    @Deprecated
    @JsonIgnore
    public String getLogLink() {
        return getLogUri();
    }

    @JsonIgnore
    public String getLogUri() {
        return notNullState(links, "links").get(LOG_LINK);
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    /**
     * @return execution URI string
     * @deprecated use {@link #getExecutionUri()} instead
     */
    @Deprecated
    @JsonIgnore
    public String getExecutionLink() {
        return getExecutionUri();
    }

    @JsonIgnore
    public String getExecutionUri() {
        return notNullState(links, "links").get(EXECUTION_LINK);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return STATUS_OK.equals(status);
    }


    public static URI uriFromExecutionUri(URI executionUri) {
        return URI.create(executionUri.toString() + "/detail");
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
