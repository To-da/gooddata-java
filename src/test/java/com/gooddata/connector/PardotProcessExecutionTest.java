/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import org.testng.annotations.Test;

import java.time.LocalDate;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;

public class PardotProcessExecutionTest {

    @Test
    public void shouldSerialize() throws Exception {
        final PardotProcessExecution execution = new PardotProcessExecution();

        assertThat(execution, jsonEquals(resource("connector/process-execution-empty.json")));
    }

    @Test
    public void shouldSerializeIncremental() throws Exception {
        final PardotProcessExecution execution = new PardotProcessExecution();
        execution.setIncremental(true);

        assertThat(execution, jsonEquals(resource("connector/process-execution-incremental.json")));
    }

    @Test
    public void shouldSerializeDownloadDataFrom() throws Exception {
        final PardotProcessExecution execution = new PardotProcessExecution();
        execution.setChangesFrom(LocalDate.of(2018, 1, 25));

        assertThat(execution, jsonEquals(resource("connector/process-execution-pardot-changesFrom.json")));
    }

}