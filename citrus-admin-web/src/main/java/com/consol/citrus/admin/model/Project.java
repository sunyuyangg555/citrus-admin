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

import com.consol.citrus.Citrus;
import com.consol.citrus.admin.configuration.ConfigurationProvider;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.service.command.maven.MavenBuildContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.util.FileUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
public class Project {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(Project.class);

    private final String projectHome;
    private String name;
    private String description;
    private String version = "1.0.0";

    private ProjectSettings settings = ConfigurationProvider.load(ProjectSettings.class);

    @JsonIgnore
    private Class<?> springJavaConfig;

    @JsonIgnore
    private ClassLoader classLoader;

    /** Citrus project information as Json file */
    public static final String PROJECT_INFO_FILENAME = "citrus-project.json";

    /**
     * Default constructor.
     */
    public Project() {
        super();
        this.projectHome = "";
    }

    /**
     * Default constructor using project home directory.
     * @param projectHome
     */
    public Project(String projectHome) {
        try {
            this.projectHome = new File(projectHome).getCanonicalPath();
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Unable to access project home directory", e);
        }
    }

    /**
     * Gets the ANT build file.
     * @return
     */
    @JsonIgnore
    public File getAntBuildFile() {
        if (isAntProject()) {
            return new File(getAntBuildFilePath());
        } else {
            throw new ApplicationRuntimeException("Failed to get ANT build file - project is not a ANT project");
        }
    }

    /**
     * Checks ANT project nature by finding the basic build.xml ANT file in project home directory.
     * @return
     */
    @JsonIgnore
    public boolean isAntProject() {
        return new File(getAntBuildFilePath()).exists();
    }

    /**
     * Gets the ANT build xml file path.
     * @return
     */
    @JsonIgnore
    private String getAntBuildFilePath() {
        return projectHome + System.getProperty("file.separator") + "build.xml";
    }

    /**
     * Gets the Maven POM file.
     * @return
     */
    @JsonIgnore
    public File getMavenPomFile() {
        if (isMavenProject()) {
            return new File(getMavenPomFilePath());
        } else {
            throw new ApplicationRuntimeException("Failed to get Maven POM file - project is not a Maven project");
        }
    }

    /**
     * Checks Maven project nature by finding the basic pom.xml Maven file in project home directory.
     * @return
     */
    @JsonIgnore
    public boolean isMavenProject() {
        return new File(getMavenPomFilePath()).exists();
    }

    /**
     * Gets the Maven POM xml file path.
     * @return
     */
    @JsonIgnore
    private String getMavenPomFilePath() {
        return projectHome + System.getProperty("file.separator") + "pom.xml";
    }

    /**
     * Gets file pointer to project info file in project home directory.
     * @return
     */
    @JsonIgnore
    public File getProjectInfoFile() {
        return new File(projectHome + System.getProperty("file.separator") + PROJECT_INFO_FILENAME);
    }

    /**
     * Gets the current test directory based on project home and default test directory.
     * @return
     */
    @JsonIgnore
    public String getJavaDirectory() {
        return new File(projectHome).getAbsolutePath() + System.getProperty("file.separator") + settings.getJavaSrcDirectory();
    }

    /**
     * Gets the current test directory based on project home and default test directory.
     * @return
     */
    @JsonIgnore
    public String getXmlDirectory() {
        return new File(projectHome).getAbsolutePath() + System.getProperty("file.separator") + settings.getXmlSrcDirectory();
    }

    /**
     * Gets the value of the projectHome property.
     *
     * @return the projectHome
     */
    public String getProjectHome() {
        return projectHome;
    }

    /**
     * Gets the absolute pathname string
     * @param filePath
     * @return
     */
    public String getAbsolutePath(String filePath) {
        if (filePath.endsWith(".java")) {
            return getJavaDirectory() + filePath;
        } else {
            return getXmlDirectory() + filePath;
        }
    }

    /**
     * Gets the value of the name property.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name property.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the description property.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description property.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the value of the version property.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version property.
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Sets the project settings.
     * @param settings
     */
    public void setSettings(ProjectSettings settings) {
        this.settings = settings;
    }

    /**
     * Gets the project settings.
     * @return
     */
    public ProjectSettings getSettings() {
        return settings;
    }

    /**
     * Load settings from project info file.
     */
    public void loadSettings() {
        try (FileInputStream fileInput = new FileInputStream(getProjectInfoFile())) {
            String projectInfo = FileUtils.readToString(fileInput);

            // support legacy build configuration
            projectInfo = projectInfo.replaceAll("com\\.consol\\.citrus\\.admin\\.model\\.build\\.maven\\.MavenBuildConfiguration", MavenBuildContext.class.getName());

            Project project = Jackson2ObjectMapperBuilder.json().build().readerFor(Project.class).readValue(projectInfo);

            setName(project.getName());
            setDescription(project.getDescription());
            setSettings(project.getSettings());
            setVersion(project.getVersion());
        } catch (IOException e) {
            throw new CitrusRuntimeException("Failed to read project settings file", e);
        }
    }

    /**
     * Returns the project's Spring Java config file.
     * @return the config file or null if no config file exists within the selected project.
     */
    @JsonIgnore
    public Class<?> getSpringJavaConfig() {
        if (springJavaConfig == null) {
            try {
                ClassLoader classLoader = getClassLoader();
                springJavaConfig = classLoader.loadClass(getSettings().getSpringJavaConfig());
            } catch (IOException | ClassNotFoundException | NoClassDefFoundError e) {
                throw new ApplicationRuntimeException("Failed to access Spring Java config class", e);
            }
        }

        return springJavaConfig;
    }

    /**
     * Provide project class loader
     * @return
     * @throws IOException
     */
    @JsonIgnore
    public ClassLoader getClassLoader() throws IOException {
        if (classLoader == null) {
            List<URL> classpathUrls = new ArrayList<>();

            classpathUrls.add(new FileSystemResource(projectHome + File.separator + "target" + File.separator + "classes").getURL());
            classpathUrls.add(new FileSystemResource(projectHome + File.separator + "target" + File.separator + "test-classes").getURL());

            if (isMavenProject()) {
                List<String> citrusDependencies = Stream.of(Maven.configureResolver()
                            .workOffline()
                            .resolve(getCitrusArtifacts())
                            .withTransitivity()
                            .asFile())
                        .parallel()
                        .map(File::getPath)
                        .collect(Collectors.toList());

                Stream.of(Maven.configureResolver()
                            .workOffline()
                            .loadPomFromFile(getMavenPomFile())
                            .importRuntimeAndTestDependencies()
                            .resolve()
                            .withTransitivity()
                            .asFile())
                        .parallel()
                        .map(File::getPath)
                        .filter(dependency -> !dependency.contains("com/consol/citrus/"))
                        .filter(dependency -> citrusDependencies.parallelStream().noneMatch(citrusDependency -> citrusDependency.equals(dependency)))
                        .forEach(dependency -> {
                            try {
                                classpathUrls.add(new FileSystemResource(dependency).getURL());
                            } catch (IOException e) {
                                log.warn(String.format("Failed to access project dependency: %s", dependency));
                            }
                        });
            }

            if (log.isDebugEnabled()) {
                log.debug("Loading test project classes ...");
                classpathUrls.forEach(url -> log.debug(url.getPath()));
            }

            classLoader = URLClassLoader.newInstance(classpathUrls.toArray(new URL[classpathUrls.size()]), Optional.ofNullable(ClassUtils.getDefaultClassLoader())
                                                                                                                    .orElse(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader())));
        }

        return classLoader;
    }

    /**
     * Provides all Citrus artifact coordinates in Maven canonical format.
     * @return
     */
    private List<String> getCitrusArtifacts() {
        return Stream.of("citrus-core",
                "citrus-jms",
                "citrus-jdbc",
                "citrus-http",
                "citrus-websocket",
                "citrus-ws",
                "citrus-ftp",
                "citrus-ssh",
                "citrus-camel",
                "citrus-docker",
                "citrus-kubernetes",
                "citrus-selenium",
                "citrus-zookeeper",
                "citrus-cucumber",
                "citrus-rmi",
                "citrus-jmx",
                "citrus-restdocs",
                "citrus-mail",
                "citrus-vertx",
                "citrus-java-dsl").map(name -> String.format("com.consol.citrus:%s:%s", name, Citrus.getVersion())).collect(Collectors.toList());
    }
}
