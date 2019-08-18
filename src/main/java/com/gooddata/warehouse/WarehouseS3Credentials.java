/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.time.LocalDateTime;

import static com.gooddata.util.Validate.notNullState;

/**
 * Single warehouse S3 credentials record (identified uniquely by warehouse ID, region and access key)
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("s3Credentials")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseS3Credentials {
    public static final String URI = WarehouseS3CredentialsList.URI + "/{region}/{accessKey}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    @JsonView(UpdateView.class)
    private final String region;

    @JsonView(UpdateView.class)
    private final String accessKey;

    private final LocalDateTime updated;

    @JsonView(UpdateView.class)
    private String secretKey;

    private final Links links;

    /**
     * Used to add new S3 credentials
     *
     * @param region    S3 region
     * @param accessKey S3 access key
     * @param secretKey S3 secret key
     */
    public WarehouseS3Credentials(final String region,
                                  final String accessKey,
                                  final String secretKey) {
        this(region, accessKey, secretKey, null, null);
    }

    @JsonCreator
    protected WarehouseS3Credentials(@JsonProperty("region") final String region,
                                     @JsonProperty("accessKey") final String accessKey,
                                     @JsonProperty("secretKey") final String secretKey,
                                     @JsonProperty("updated") // TODO - ISO: @JsonDeserialize(using = ISODateTimeDeserializer.class)
                                         final LocalDateTime updated,
                                     @JsonProperty("links") final Links links) {
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.updated = updated;
        this.links = links;
    }

    /**
     * @return the date and time of last modification
     */
//    TODO - ISO: @JsonSerialize(using = ISODateTimeSerializer.class)
    public LocalDateTime getUpdated() {
        return updated;
    }

    /**
     * not returned when listing
     * @return S3 secret key
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Sets new secret key
     *
     * @param secretKey secret key to set
     */
    public void setSecretKey(final String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getRegion() {
        return region;
    }

    public Links getLinks() {
        return links;
    }

    /**
     * @return URI of this resource
     */
    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").getSelf();
    }

    /**
     * @return URI of the parent of warehouse S3 credentials
     */
    @JsonIgnore
    public String getListUri() {
        return notNullState(links, "links").getParent();
    }

    /**
     * @return URI of the warehouse
     */
    @JsonIgnore
    public String getInstanceUri() {
        return notNullState(links, "links").getInstance();
    }

    /**
     * @return URI of the user profile, who was the last to modify this resource
     */
    @JsonIgnore
    public String getUpdatedByUri() {
        return notNullState(links, "links").getUpdatedBy();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        private final String self;
        private final String parent;
        private final String instance;
        private final String updatedBy;

        @JsonCreator
        public Links(@JsonProperty("self") String self,
                     @JsonProperty("parent") String parent,
                     @JsonProperty("instance") String instance,
                     @JsonProperty("updatedBy") String updatedBy) {
            this.self = self;
            this.parent = parent;
            this.instance = instance;
            this.updatedBy = updatedBy;
        }

        public String getSelf() {
            return self;
        }

        public String getParent() {
            return parent;
        }

        public String getInstance() {
            return instance;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this, "secretKey");
    }

    /**
     * Class representing update view of warehouse S3 credentials
     */
    static class UpdateView {
    }
}
