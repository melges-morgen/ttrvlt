package ru.revolut.testtask;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.revolut.testtask.api.JerseyConfig;

/**
 * TODO: Write class description
 *
 * @author morgmat
 */
public class EmbeddedServer {
    private static Logger logger = LoggerFactory.getLogger(EmbeddedServer.class);
    private static final int SERVER_PORT = 8090;

    private EmbeddedServer() {}

    public static void main(String[] args) throws Exception {
        QuasiBeanManager.init("revolut-test-task");
        runWebServer();
    }

    private static void runWebServer() throws Exception {
        logger.info("Starting internal jetty server");

        ResourceConfig config = new JerseyConfig();
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        Server server = new Server(SERVER_PORT);
        ServletContextHandler context = new ServletContextHandler(server, "/api/");
        context.addServlet(servlet, "/*");

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
