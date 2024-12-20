/*
 *  Copyright (c) 2022 Microsoft Corporation
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

package org.eclipse.edc.boot.system.injection;

import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public final class InjectorImpl implements Injector {

    private final DefaultServiceSupplier defaultServiceSupplier;

    /**
     * Constructs a new Injector instance, which can either resolve services from the {@link ServiceExtensionContext}, or -
     * if the required service is not present - use the default implementations provided in the map.
     *
     * @param defaultServiceSupplier A function that maps a type to its default service instance, that could be null.
     */
    public InjectorImpl(DefaultServiceSupplier defaultServiceSupplier) {
        this.defaultServiceSupplier = defaultServiceSupplier;
    }

    @Override
    public <T> T inject(InjectionContainer<T> container, ServiceExtensionContext context) {
        var monitor = context.getMonitor();

        container.getInjectionPoints().forEach(ip -> {
            try {
                var service = resolveService(context, ip);
                if (service != null) { //can only be if not required
                    ip.setTargetValue(service);
                }
            } catch (EdcInjectionException ex) {
                throw ex; //simply rethrow, do not wrap in another one
            } catch (EdcException ex) { //thrown e.g. if the service is not present and is not optional
                monitor.warning("Error during injection", ex);
                throw new EdcInjectionException(ex);
            } catch (IllegalAccessException e) { //e.g. when the field is marked "final"
                monitor.warning("Could not set injection target", e);
                throw new EdcInjectionException(e);
            }
        });

        return container.getInjectionTarget();
    }

    private Object resolveService(ServiceExtensionContext context, InjectionPoint<?> injectionPoint) {
        var serviceClass = injectionPoint.getType();
        if (context.hasService(serviceClass)) {
            return context.getService(serviceClass, !injectionPoint.isRequired());
        } else {
            return defaultServiceSupplier.provideFor(injectionPoint, context);
        }
    }

}
