package com.cabbooking.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.cabbooking.model.*;
import com.cabbooking.service.BookingService;
import com.cabbooking.service.UserService;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CabBookingServer {

    private static final int PORT = 8080;
    private static final String STATIC_DIR = "CabBookingFrontend";

    public static void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Route handlers
            server.createContext("/", new StaticFileHandler());
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/signup", new SignupHandler());
            server.createContext("/api/calculate-fare", new FareHandler());
            server.createContext("/api/book", new BookingHandler());
            server.createContext("/api/history", new HistoryHandler());

            server.createContext("/api/cancelBooking", exchange -> {

                if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String body =
                        new String(
                                exchange.getRequestBody().readAllBytes()
                        );

                String bookingId =
                        body.split("\"bookingId\":\"")[1]
                                .split("\"")[0];

                BookingService bookingService =
                        new BookingService();

                boolean success =
                        bookingService.cancelBooking(bookingId);

                String response =
                        success
                                ? "Booking Cancelled Successfully"
                                : "Cancellation Failed";

                exchange.sendResponseHeaders(
                        200,
                        response.getBytes().length
                );

                exchange.getResponseBody()
                        .write(response.getBytes());

                exchange.close();
            });

            server.setExecutor(null); // default executor
            server.start();
            System.out.println("==================================================");
            System.out.println("🚖 CAB BOOKING WEB BACKEND RUNNING!");
            System.out.println("👉 Access UI at: http://localhost:" + PORT);
            System.out.println("==================================================");
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- UTILITIES FOR API ---

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return bos.toString(StandardCharsets.UTF_8.name());
    }

    private static String extractJsonValue(String json, String key) {
        // String pattern match: "key":"value"
        String pattern = "\"" + key + "\":\\s*\"([^\"]*)\"";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        // Numeric pattern match: "key": 12.34
        pattern = "\"" + key + "\":\\s*([0-9.+-]+)";
        r = Pattern.compile(pattern);
        m = r.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void handleOptionsRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
    }

    // --- HANDLERS ---

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File(STATIC_DIR + path);
            if (!file.exists() || file.isDirectory()) {
                // Return 404
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) contentType = "text/html";
            else if (path.endsWith(".css")) contentType = "text/css";
            else if (path.endsWith(".js")) contentType = "application/javascript";
            else if (path.endsWith(".png")) contentType = "image/png";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
            else if (path.endsWith(".svg")) contentType = "image/svg+xml";
            else if (path.endsWith(".ico")) contentType = "image/x-icon";

            byte[] fileBytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }
    }

    static class LoginHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleOptionsRequest(exchange);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405,
                        "{\"success\":false}");
                return;
            }

            try {

                String body = readRequestBody(exchange);

                String email =
                        extractJsonValue(body, "email");

                String password =
                        extractJsonValue(body, "password");

                if(email.isEmpty() || password.isEmpty()) {

                    sendJsonResponse(
                            exchange,
                            400,
                            "{\"success\":false,\"error\":\"Email and Password required\"}"
                    );
                    return;
                }

                boolean loginSuccess = false;
                String userName = "";

                try (java.sql.Connection con =
                             com.cabbooking.util.DBConnection.getConnection()) {

                    String sql =
                            "SELECT * FROM users WHERE email=? AND password=?";

                    java.sql.PreparedStatement pst =
                            con.prepareStatement(sql);

                    pst.setString(1, email);
                    pst.setString(2, password);

                    java.sql.ResultSet rs =
                            pst.executeQuery();

                    if(rs.next()) {

                        loginSuccess = true;

                        userName =
                                rs.getString("name");
                    }
                }

                if(loginSuccess) {

                    sendJsonResponse(
                            exchange,
                            200,
                            "{\"success\":true,\"name\":\""
                                    + userName +
                                    "\"}"
                    );
                }
                else {

                    sendJsonResponse(
                            exchange,
                            200,
                            "{\"success\":false,\"error\":\"Invalid Email or Password\"}"
                    );
                }

            }
            catch (Exception e) {

                e.printStackTrace();

                sendJsonResponse(
                        exchange,
                        500,
                        "{\"success\":false}"
                );
            }
        }
    }

    static class FareHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendJsonResponse(exchange, 405,
                        "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {

                String body =
                        readRequestBody(exchange);

                String vehicleType =
                        extractJsonValue(body, "vehicle");

                double distance =
                        Double.parseDouble(
                                extractJsonValue(body, "distance")
                        );

                Vehicle vehicle;

                switch (vehicleType.toLowerCase()) {

                    case "mini":
                        vehicle = new Mini();
                        break;

                    case "sedan":
                        vehicle = new Sedan();
                        break;

                    default:
                        vehicle = new SUV();
                }

                double fare =
                        vehicle.calculateFare(
                                (int)Math.round(distance)
                        );

                sendJsonResponse(
                        exchange,
                        200,
                        "{\"fare\":" + fare + "}"
                );

            }

            catch (Exception e) {

                e.printStackTrace();

                sendJsonResponse(
                        exchange,
                        500,
                        "{\"error\":\"Fare calculation failed\"}"
                );
            }
        }
    }

    static class BookingHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                sendJsonResponse(
                        exchange,
                        405,
                        "{\"success\":false}"
                );
                return;
            }

            try {

                String body = readRequestBody(exchange);

                System.out.println("BOOKING REQUEST:");
                System.out.println(body);

                String userName =
                        extractJsonValue(body, "userName");

                String pickup =
                        extractJsonValue(body, "pickup");

                String destination =
                        extractJsonValue(body, "destination");

                String vehicle =
                        extractJsonValue(body, "vehicle");

                double distance =
                        Double.parseDouble(
                                extractJsonValue(body, "distance")
                        );

                double fare =
                        Double.parseDouble(
                                extractJsonValue(body, "fare")
                        );

                String location =
                        pickup + " -> " + destination;

                BookingService service =
                        new BookingService();

                service.saveBooking(
                        userName,
                        location,
                        vehicle,
                        distance,
                        fare
                );

                System.out.println("Booking Saved Successfully");

                sendJsonResponse(
                        exchange,
                        200,
                        "{\"success\":true}"
                );

            }

            catch (Exception e) {

                e.printStackTrace();

                sendJsonResponse(
                        exchange,
                        500,
                        "{\"success\":false}"
                );
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleOptionsRequest(exchange);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String query = exchange.getRequestURI().getQuery();
                String name = "";
                if (query != null && query.startsWith("name=")) {
                    name = URLDecoder.decode(query.substring(5), StandardCharsets.UTF_8.name());
                }

                if (name.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Name parameter is required\"}");
                    return;
                }

                // Retrieve history JSON directly from BookingService
                String historyJson = new BookingService().getBookingsJsonByUser(name);
                sendJsonResponse(exchange, 200, historyJson);
            } catch (Exception e) {
                e.printStackTrace();
                sendJsonResponse(exchange, 500, "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class SignupHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                sendJsonResponse(
                        exchange,
                        405,
                        "{\"success\":false}"
                );

                return;
            }

            try {

                String body =
                        readRequestBody(exchange);

                String name =
                        extractJsonValue(body, "name");

                String email =
                        extractJsonValue(body, "email");

                String password =
                        extractJsonValue(body, "password");

                UserService service =
                        new UserService();

                boolean success =
                        service.registerUser(
                                name,
                                email,
                                password
                        );

                sendJsonResponse(
                        exchange,
                        200,
                        "{\"success\":" + success + "}"
                );

            }

            catch (Exception e) {

                e.printStackTrace();

                sendJsonResponse(
                        exchange,
                        500,
                        "{\"success\":false}"
                );
            }
        }
    }
}
