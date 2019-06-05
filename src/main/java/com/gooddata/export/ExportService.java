/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataEndpoint;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.GoodDataSettings;
import com.gooddata.PollResult;
import com.gooddata.SimplePollHandler;
import com.gooddata.gdc.AsyncTask;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.AbstractObj;
import com.gooddata.md.ProjectDashboard;
import com.gooddata.md.ProjectDashboard.Tab;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.project.Project;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;

import static com.gooddata.md.Obj.OBJ_TEMPLATE;
import static com.gooddata.util.Validate.notNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Export project data
 *
 * @see com.gooddata.report.ReportService
 */
public class ExportService extends AbstractService {

    public static final String EXPORTING_URI = "/gdc/exporter/executor";

    private static final String CLIENT_EXPORT_URI = "/gdc/projects/{projectId}/clientexport";

    private static final String RAW_EXPORT_URI = "/gdc/projects/{projectId}/execute/raw";

    private final GoodDataEndpoint endpoint;

    /**
     * Service for data export
     * @param restTemplate REST template
     * @param endpoint GoodData Endpoint
     * @param settings settings
     */
    public ExportService(final RestTemplate restTemplate, final GoodDataEndpoint endpoint, final GoodDataSettings settings) {
        super(restTemplate, settings);
        this.endpoint = notNull(endpoint, "endpoint");
    }

    /**
     * Service for data export
     * @param restTemplate REST template
     * @param endpoint GoodData Endpoint
     * @deprecated use ExportService(RestTemplate, GoodDataEndpoint, GoodDataSettings) constructor instead
     */
    @Deprecated
    public ExportService(final RestTemplate restTemplate, final GoodDataEndpoint endpoint) {
        super(restTemplate);
        this.endpoint = notNull(endpoint, "endpoint");
    }

    /**
     * Export the given report definition in the given format to the given output stream
     *
     * @param reportDefinition report definition
     * @param format           export format
     * @param output           target
     * @return polling result
     * @throws NoDataExportException in case report contains no data
     * @throws ExportException       on error
     */
    public FutureResult<Void> export(final ReportDefinition reportDefinition, final ExportFormat format,
                                     final OutputStream output) {
        notNull(reportDefinition, "reportDefinition");
        final ReportRequest request = new ExecuteReportDefinition(reportDefinition);
        return exportReport(request, format, output);
    }

    /**
     * Export the given report in the given format to the given output stream
     *
     * @param report report
     * @param format export format
     * @param output target
     * @return polling result
     * @throws NoDataExportException in case report contains no data
     * @throws ExportException       on error
     */
    public FutureResult<Void> export(final Report report, final ExportFormat format,
                                     final OutputStream output) {
        notNull(report, "report");
        final ReportRequest request = new ExecuteReport(report);
        return exportReport(request, format, output);
    }

    private FutureResult<Void> exportReport(final ReportRequest request, final ExportFormat format, final OutputStream output) {
        notNull(output, "output");
        notNull(format, "format");
        final JsonNode execResult = executeReport(ReportRequest.URI, request);
        final String uri = exportReport(execResult, format);
        return new PollResult<>(this, new SimplePollHandler<Void>(uri, Void.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                switch (response.getStatusCode()) {
                    case OK:
                        return true;
                    case ACCEPTED:
                        return false;
                    case NO_CONTENT:
                        throw new NoDataExportException();
                    default:
                        throw new ExportException("Unable to export report, unknown HTTP response code: " + response.getStatusCode());
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ExportException("Unable to export report", e);
            }

            @Override
            protected void onFinish() {
                try {
                    restTemplate.execute(uri, GET, null, new OutputStreamResponseExtractor(output));
                } catch (GoodDataException | RestClientException e) {
                    throw new ExportException("Unable to export report", e);
                }
            }
        });
    }

    protected JsonNode executeReport(final String executionUri, final ReportRequest request) {
        try {
            final ResponseEntity<String> entity = restTemplate
                    .exchange(executionUri, POST, new HttpEntity<>(request), String.class);
            return MAPPER_PROVIDER.getReader().readTree(entity.getBody());
        } catch (GoodDataException | RestClientException e) {
            throw new ExportException("Unable to execute report", e);
        } catch (IOException e) {
            throw new ExportException("Unable to read execution result", e);
        }
    }

    private String exportReport(final JsonNode execResult, final ExportFormat format) {
        notNull(execResult, "execResult");
        notNull(format, "format");
        final ObjectNode root = JsonNodeFactory.instance.objectNode();
        final ObjectNode child = JsonNodeFactory.instance.objectNode();

        child.set("result", execResult);
        child.put("format", format.getValue());
        root.set("result_req", child);

        try {
            return restTemplate.postForObject(EXPORTING_URI, root, UriResponse.class).getUri();
        } catch (GoodDataException | RestClientException e) {
            throw new ExportException("Unable to export report", e);
        }
    }

    /**
     * Export the given dashboard tab in PDF format to the given output stream
     *
     * @param dashboard dashboard
     * @param tab       tab
     * @param output    output
     * @return polling result
     * @throws ExportException if export fails
     */
    public FutureResult<Void> exportPdf(final ProjectDashboard dashboard, final Tab tab, final OutputStream output) {
        notNull(dashboard, "dashboard");
        notNull(tab, "tab");
        notNull(output, "output");

        final String projectId = extractProjectId(dashboard);
        final String projectUri = Project.TEMPLATE.expand(projectId).toString();
        final String dashboardUri = dashboard.getUri();

        final ClientExport export = new ClientExport(endpoint, projectUri, dashboardUri, tab.getIdentifier());
        final AsyncTask task;
        try {
            task = restTemplate.postForObject(CLIENT_EXPORT_URI, export, AsyncTask.class, projectId);
        } catch (RestClientException | GoodDataRestException e) {
            throw new ExportException("Unable to export dashboard: " + dashboardUri, e);
        }

        return new PollResult<>(this, new SimplePollHandler<Void>(task.getUri(), Void.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                switch (response.getStatusCode()) {
                    case OK:
                        return true;
                    case ACCEPTED:
                        return false;
                    default:
                        throw new ExportException("Unable to export dashboard: " + dashboardUri +
                                ", unknown HTTP response code: " + response.getStatusCode());
                }
            }

            @Override
            protected void onFinish() {
                try {
                    restTemplate.execute(task.getUri(), GET, null, new OutputStreamResponseExtractor(output));
                } catch (GoodDataException | RestClientException e) {
                    throw new ExportException("Unable to export dashboard: " + dashboardUri, e);
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ExportException("Unable to export dashboard: " + dashboardUri, e);
            }
        });
    }

    /**
     * Export the given Report using the raw export (without columns/rows limitations)
     * @param report report
     * @param output output
     * @return polling result
     * @throws ExportException in case export fails
     */
    public FutureResult<Void> exportCsv(final Report report, final OutputStream output) {
        notNull(report, "report");
        return exportCsv(report, new ExecuteReport(report), output);
    }

    /**
     * Export the given Report Definition using the raw export (without columns/rows limitations)
     * @param definition report definition
     * @param output output
     * @return polling result
     * @throws ExportException in case export fails
     */
    public FutureResult<Void> exportCsv(final ReportDefinition definition, final OutputStream output) {
        final ReportRequest request = new ExecuteReportDefinition(definition);
        return exportCsv(definition, request, output);
    }

    private FutureResult<Void> exportCsv(final AbstractObj obj, final ReportRequest request, final OutputStream output) {
        notNull(obj, "obj");
        notNull(request, "request");
        notNull(output, "output");

        final String projectId = extractProjectId(obj);
        final String uri = obj.getUri();

        final UriResponse response;
        try {
            response = restTemplate.postForObject(RAW_EXPORT_URI, request, UriResponse.class, projectId);
        } catch (RestClientException | GoodDataRestException e) {
            throw new ExportException("Unable to export: " + uri);
        }
        if (response == null || response.getUri() == null) {
            throw new ExportException("Empty response, unable to export: " + uri);
        }

        return new PollResult<>(this, new SimplePollHandler<Void>(response.getUri(), Void.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                switch (response.getStatusCode()) {
                    case OK:
                        return true;
                    case ACCEPTED:
                        return false;
                    case NO_CONTENT:
                        throw new NoDataExportException();
                    default:
                        throw new ExportException("Unable to export: " + uri +
                                ", unknown HTTP response code: " + response.getStatusCode());
                }
            }

            @Override
            protected void onFinish() {
                try {
                    restTemplate.execute(getPolling(), GET, null, new OutputStreamResponseExtractor(output));
                } catch (GoodDataException | RestClientException e) {
                    throw new ExportException("Unable to export: " + uri, e);
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ExportException("Unable to export: " + uri, e);
            }
        });
    }

    static String extractProjectId(final AbstractObj obj) {
        notNull(obj, "obj");
        notNull(obj.getUri(), "obj.uri");

        final String projectId = OBJ_TEMPLATE.match(obj.getUri()).get("projectId");
        notNull(projectId, "obj uri - project id");
        return projectId;
    }
}
