/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.report;

import com.gooddata.JsonMatchers;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class ExecuteDefinitionTest {

    @Test
    public void testSerialization() throws Exception {
        final ReportRequest request = new ExecuteDefinition("DEF-URI");
        assertThat(request, JsonMatchers.serializesToJson("/report/executeDefinition.json"));
    }
}