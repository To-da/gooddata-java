/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.util.GoodDataToStringBuilder;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents {@link DateFilter} specifying exact from and to dates.
 */
@JsonRootName(AbsoluteDateFilter.NAME)
public class AbsoluteDateFilter extends DateFilter {

    private static final long serialVersionUID = -1857726227400504182L;
    static final String NAME = "absoluteDateFilter";
    
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates new filter instance
     * @param dataSet qualifier of date dimension dataset
     * @param from date from
     * @param to date to
     */
    @JsonCreator
    public AbsoluteDateFilter(@JsonProperty("dataSet") final ObjQualifier dataSet,
                              @JsonProperty("from") @JsonFormat(pattern = "yyyy-MM-dd") final LocalDate from,
                              @JsonProperty("to") @JsonFormat(pattern = "yyyy-MM-dd") final LocalDate to) {
        super(dataSet);
        this.from = from;
        this.to = to;
    }

    /**
     * @return date from
     */
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate getFrom() {
        return from;
    }

    /**
     * @return date to
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate getTo() {
        return to;
    }

    @Override
    public FilterItem withObjUriQualifier(final UriObjQualifier qualifier) {
        return new AbsoluteDateFilter(qualifier, from, to);
    }

    @Override
    @JsonIgnore
    public boolean isAllTimeSelected() {
        return getFrom() == null || getTo() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbsoluteDateFilter that = (AbsoluteDateFilter) o;
        return super.equals(that) && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, super.hashCode());
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
