/*
 *  Copyright (c) 2025 Cofinity-X
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Cofinity-X - initial API and implementation
 *
 */

plugins {
    `java-library`
}

dependencies {
    api(project(":data-protocols:dsp:dsp-core:dsp-http-api-base-configuration"))
    api(project(":data-protocols:dsp:dsp-core:dsp-http-core"))
    api(project(":data-protocols:dsp:dsp-core:dsp-catalog-http-dispatcher"))
    api(project(":data-protocols:dsp:dsp-core:dsp-negotiation-http-dispatcher"))
    api(project(":data-protocols:dsp:dsp-core:dsp-transfer-process-http-dispatcher"))
}
