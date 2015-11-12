/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Metric
 */
@JsonTypeName("metric")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Metric extends AbstractObj implements Queryable, Updatable {

    @JsonProperty("content")
    private final Content content;

    @JsonCreator
    private Metric(@JsonProperty("meta") Meta meta, @JsonProperty("content") Content content) {
        super(meta);
        this.content = content;
    }

    public Metric(String title, String expression, String format) {
        this(new Meta(title), new Metric.Content(expression, format));
    }

    @JsonIgnore
    public String getExpression() {
        return content.getExpression();
    }

    @JsonIgnore
    public String getFormat() {
        return content.getFormat();
    }

    @JsonIgnore
    public MaqlAst getMaqlAst() {
        return content.getMaqlAst();
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private static class Content {

        private final String expression;

        @JsonProperty("format")
        private String format;

        @JsonProperty("tree")
        private MaqlAst maqlAst;


        @JsonCreator
        public Content(@JsonProperty("expression") String expression) {
            this.expression = expression;
        }

        public Content(final String expression, final String format) {
            this.expression = expression;
            this.format = format;
        }

        public String getExpression() {
            return expression;
        }

        public void setFormat(final String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        public void setMaqlAst(final MaqlAst maqlAst) {
            this.maqlAst = maqlAst;
        }

        public MaqlAst getMaqlAst() {
            return maqlAst;
        }

    }
}
