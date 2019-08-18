/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static com.gooddata.util.Validate.notNullState;

/**
 * Connector process (i.e. single ETL run) status used in integration object. Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationProcessStatus {

    public static final String URI = "/gdc/projects/{project}/connectors/{connector}/integration/processes/{process}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);
    private static final String SELF_LINK = "self";

    private final Status status;
    private final LocalDateTime started;
    private final LocalDateTime finished;
    private final Map<String, String> links;

    @JsonCreator
    protected IntegrationProcessStatus(@JsonProperty("status") Status status,
                                       @JsonProperty("started")  //    TODO - ISO: @JsonDeserialize(using = ISODateTimeDeserializer.class)
                                               LocalDateTime started,
                                       @JsonProperty("finished")  //    TODO - ISO: @JsonDeserialize(using = ISODateTimeDeserializer.class)
                                               LocalDateTime finished,
                                       @JsonProperty("links") Map<String, String> links) {
        this.status = status;
        this.started = started;
        this.finished = finished;
        this.links = links;
    }

    public Status getStatus() {
        return status;
    }

    //    TODO - ISO:   @JsonSerialize(using = ISODateTimeSerializer.class)
    public LocalDateTime getStarted() {
        return started;
    }

    //    TODO - ISO:  @JsonSerialize(using = ISODateTimeSerializer.class)
    public LocalDateTime getFinished() {
        return finished;
    }

    /**
     * Returns true when the connector process has already finished (no matter if it was successful).
     * NOTE: It also returns false in case of inability to resolve the code (e.g. API change)
     *
     * @return true when the connector process has already finished, false otherwise
     */
    @JsonIgnore
    public boolean isFinished() {
        return status != null && status.isFinished();
    }

    /**
     * Returns true when the connector process failed.
     * NOTE: It also returns false in case of inability to resolve the code (e.g. API change)
     *
     * @return true when the connector process failed, false otherwise
     */
    @JsonIgnore
    public boolean isFailed() {
        return status != null && status.isFailed();
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getUri()).get("process");
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
