/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ProcessExecutionDetailTest {

    @Test
    public void testDeserialization() throws Exception {
        final ProcessExecutionDetail executionDetail = readObjectFromResource("/dataload/processes/executionDetail.json",
                ProcessExecutionDetail.class);
        assertThat(executionDetail, notNullValue());
        assertThat(executionDetail.getStatus(), is("ERROR"));
        assertThat(executionDetail.getCreated(), is(LocalDateTime.of(2014, 2, 24, 19, 0, 35, 999000000)));
        assertThat(executionDetail.getStarted(), is(LocalDateTime.of(2014, 2, 24, 19, 0, 39, 155000000)));
        assertThat(executionDetail.getUpdated(), is(LocalDateTime.of(2014, 2, 24, 19, 26, 13, 197000000)));
        assertThat(executionDetail.getFinished(), is(LocalDateTime.of(2014, 2, 24, 19, 26, 13, 60000000)));
        assertThat(executionDetail.getError(), notNullValue());
        assertThat(executionDetail.getError().getErrorCode(), is("executor.error"));
        assertThat(executionDetail.getError().getFormattedMessage(),
                is("Error message with some placeholders for parameters - like this one."));
        assertThat(executionDetail.getUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/detail"));
        assertThat(executionDetail.getLogUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/log"));
        assertThat(executionDetail.getExecutionUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final ProcessExecutionDetail executionDetail = readObjectFromResource("/dataload/processes/executionDetail.json",
                ProcessExecutionDetail.class);

        assertThat(executionDetail.toString(), matchesPattern(ProcessExecutionDetail.class.getSimpleName() + "\\[.*\\]"));
    }

}