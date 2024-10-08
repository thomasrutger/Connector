/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

plugins {
    `java-library`
}

dependencies {
    // SPI dependencies
    api(project(":dist:bom:controlplane-base-bom"))

    // DCP dependencies, JWT and LDP
    api(project(":spi:common:jwt-spi"))

    api(project(":extensions:common:iam:oauth2:oauth2-service"))
}