package ru.revolut.testtask.api;

import org.glassfish.jersey.server.ResourceConfig;
import ru.revolut.testtask.api.exceptions.EntityNotExistExceptionMapper;
import ru.revolut.testtask.api.exceptions.InvalidAccountForOperationMapper;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(new EntityNotExistExceptionMapper());
        register(new InvalidAccountForOperationMapper());
        packages(JerseyConfig.class.getPackage().getName());
    }
}
