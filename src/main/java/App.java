import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws Exception {

        String portValue = System.getenv("PORT");
        int port = Integer.parseInt(portValue);

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(port),
                        0
                );

        server.createContext("/", exchange -> {

            String appName = System.getenv("APP_NAME");
            String environment = System.getenv("ENVIRONMENT");
            String version = System.getenv("VERSION");

            String response = """
                    {
                      "application": "%s",
                      "environment": "%s",
                      "version": "%s",
                      "status": "Running"
                    }
                    """.formatted(
                            appName,
                            environment,
                            version
                    );

            sendResponse(exchange, response);
        });

        server.createContext("/health", exchange -> {

            String response = """
                    {
                      "status": "UP"
                    }
                    """;

            sendResponse(exchange, response);
        });

        server.setExecutor(null);

        System.out.println(
                "Employee API started on port " + port
        );

        server.start();
    }

    private static void sendResponse(
            HttpExchange exchange,
            String response) throws IOException {

        exchange.getResponseHeaders()
                .add(
                        "Content-Type",
                        "application/json"
                );

        byte[] responseBytes =
                response.getBytes();

        exchange.sendResponseHeaders(
                200,
                responseBytes.length
        );

        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(responseBytes);
        }
    }
}
