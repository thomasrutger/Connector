/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.edc.junit.testfixtures;

import dev.failsafe.RetryPolicy;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.http.client.EdcHttpClientImpl;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

public class TestUtils {
    public static final String GRADLE_WRAPPER;
    private static final String GRADLE_WRAPPER_UNIX = "gradlew";
    private static final String GRADLE_WRAPPER_WINDOWS = "gradlew.bat";
    private static File buildRoot = null;

    static {
        GRADLE_WRAPPER = (System.getProperty("os.name").toLowerCase().contains("win")) ? GRADLE_WRAPPER_WINDOWS : GRADLE_WRAPPER_UNIX;
    }

    public static URI getResource(String name) {
        var resource = Thread.currentThread().getContextClassLoader().getResource(name);
        if (resource == null) {
            throw new AssertionFailedError("Cannot find resource " + name);
        }
        try {
            return resource.toURI();
        } catch (URISyntaxException e) {
            throw new AssertionFailedError("Cannot find resource " + name, e);
        }
    }

    public static File getFileFromResourceName(String resourceName) {
        return new File(getResource(resourceName));
    }

    public static String getResourceFileContentAsString(String resourceName) {
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            var scanner = new Scanner(Objects.requireNonNull(stream, "Not found: " + resourceName)).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to create a temporary directory.
     *
     * @return a newly create temporary directory.
     */
    public static String tempDirectory() {
        try {
            return Files.createTempDirectory(TestUtils.class.getSimpleName()).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create an {@link OkHttpClient} suitable for using in unit tests. The client configured with long timeouts
     * suitable for high-contention scenarios in CI.
     *
     * @return an {@link OkHttpClient.Builder}.
     */
    public static OkHttpClient testOkHttpClient(Interceptor... interceptors) {
        var builder = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES);

        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }

        return builder.build();
    }

    /**
     * Create an {@link org.eclipse.edc.spi.http.EdcHttpClient} suitable for using in unit tests. The client configured with long timeouts
     * suitable for high-contention scenarios in CI.
     *
     * @return an {@link OkHttpClient.Builder}.
     */
    public static EdcHttpClient testHttpClient(Interceptor... interceptors) {
        return new EdcHttpClientImpl(testOkHttpClient(interceptors), RetryPolicy.ofDefaults(), mock(Monitor.class));
    }

    /**
     * Utility method to locate the Gradle project root.
     * Search for build root will be done only once and cached for subsequent calls.
     *
     * @return The Gradle project root directory.
     */
    public static File findBuildRoot() {
        // Use cached value if already existing.
        if (buildRoot != null) {
            return buildRoot;
        }

        File canonicalFile;
        try {
            canonicalFile = new File(".").getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalStateException("Could not resolve current directory.", e);
        }
        buildRoot = findBuildRoot(canonicalFile);
        if (buildRoot == null) {
            throw new IllegalStateException("Could not find " + GRADLE_WRAPPER + " in parent directories.");
        }
        return buildRoot;
    }

    private static File findBuildRoot(File path) {
        File gradlew = new File(path, GRADLE_WRAPPER);
        if (gradlew.exists()) {
            return path;
        }
        var parent = path.getParentFile();
        if (parent != null) {
            return findBuildRoot(parent);
        }
        return null;
    }

}
