/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.admin.model;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
public class TestReport {

    private Date executionDate = new Date();
    private String projectName;
    private String suiteName;
    private long passed;
    private long skipped;
    private long failed;
    private long total;

    private long duration;

    private String groups;

    private List<TestResult> results = new ArrayList<>();

    /**
     * Sets the project name.
     * @param projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Gets the project name.
     * @return
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the execution date.
     * @return
     */
    public Date getExecutionDate() {
        return executionDate;
    }

    /**
     * Sets the execution date.
     * @param executionDate
     */
    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    /**
     * Gets the suite name.
     * @return
     */
    public String getSuiteName() {
        return suiteName;
    }

    /**
     * Sets the suite name
     * @param suiteName
     */
    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    /**
     * Gets the number of passed tests.
     * @return
     */
    public long getPassed() {
        return passed;
    }

    /**
     * Sets the number of passed tests.
     * @param passed
     */
    public void setPassed(long passed) {
        this.passed = passed;
    }

    /**
     * Gets the number of skipped tests.
     * @return
     */
    public long getSkipped() {
        return skipped;
    }

    /**
     * Sets the number of skipped tests.
     * @param skipped
     */
    public void setSkipped(long skipped) {
        this.skipped = skipped;
    }

    /**
     * Gets the number of failed tests.
     * @return
     */
    public long getFailed() {
        return failed;
    }

    /**
     * Sets the number of failed tests.
     * @param failed
     */
    public void setFailed(long failed) {
        this.failed = failed;
    }

    /**
     * Gets the total number of tests.
     * @return
     */
    public long getTotal() {
        return total;
    }

    /**
     * Sets the total number of tests.
     * @param total
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * Gets the test run duration in ms.
     * @return
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Sets the test run duration in ms.
     * @param duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Gets the test groups.
     * @return
     */
    public String getGroups() {
        return groups;
    }

    /**
     * Sets the test groups.
     * @param groups
     */
    public void setGroups(String groups) {
        this.groups = groups;
    }

    /**
     * Sets the test results.
     * @param results
     */
    public void setResults(List<TestResult> results) {
        this.results = results;
    }

    /**
     * Gets the test results.
     * @return
     */
    public List<TestResult> getResults() {
        return results;
    }
}
