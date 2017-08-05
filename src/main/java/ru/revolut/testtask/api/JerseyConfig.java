package ru.revolut.testtask.api;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        packages(JerseyConfig.class.getPackage().getName());
    }
}
