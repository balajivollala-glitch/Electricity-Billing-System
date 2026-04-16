import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static int billCounter = 1;

    public static void main(String[] args) throws IOException {
        // Create HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Define endpoints
        server.createContext("/customers", new CustomersHandler());
        server.createContext("/bills", new BillsHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("✅ Server started on http://localhost:8080");
        System.out.println("📌 Keep this terminal window open!");
    }

    // Handler for /customers endpoint
    static class CustomersHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                // Check if getting single customer
                if (path.matches("/customers/[^/]+")) {
                    String customerId = path.substring(path.lastIndexOf("/") + 1);
                    handleGetCustomer(exchange, customerId);
                } else {
                    handleGetAllCustomers(exchange);
                }
            } else if (method.equals("POST")) {
                handleAddCustomer(exchange);
            } else if (method.equals("DELETE")) {
                String customerId = path.substring(path.lastIndexOf("/") + 1);
                handleDeleteCustomer(exchange, customerId);
            }
        }

        private void handleGetAllCustomers(HttpExchange exchange) throws IOException {
            List<Customer> customers = DataStore.readCustomers();
            String response = customersToJson(customers);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handleGetCustomer(HttpExchange exchange, String customerId) throws IOException {
            List<Customer> customers = DataStore.readCustomers();
            Customer found = null;
            
            for (Customer c : customers) {
                if (c.getCustomerId().equals(customerId)) {
                    found = c;
                    break;
                }
            }

            if (found != null) {
                String response = customerToJson(found);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String response = "{\"error\":\"Customer not found\"}";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private void handleAddCustomer(HttpExchange exchange) throws IOException {
            // Read request body
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                body.append(line);
            }

            // Parse JSON manually
            Customer customer = parseCustomerJson(body.toString());
            
            // Add to storage
            List<Customer> customers = DataStore.readCustomers();
            customers.add(customer);
            DataStore.writeCustomers(customers);

            String response = "{\"message\":\"Customer added successfully\"}";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handleDeleteCustomer(HttpExchange exchange, String customerId) throws IOException {
            List<Customer> customers = DataStore.readCustomers();
            customers.removeIf(c -> c.getCustomerId().equals(customerId));
            DataStore.writeCustomers(customers);

            String response = "{\"message\":\"Customer deleted successfully\"}";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String customersToJson(List<Customer> customers) {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < customers.size(); i++) {
                json.append(customerToJson(customers.get(i)));
                if (i < customers.size() - 1) json.append(",");
            }
            json.append("]");
            return json.toString();
        }

        private String customerToJson(Customer c) {
            return "{" +
                "\"customerId\":\"" + c.getCustomerId() + "\"," +
                "\"name\":\"" + c.getName() + "\"," +
                "\"email\":\"" + c.getEmail() + "\"," +
                "\"address\":\"" + c.getAddress() + "\"," +
                "\"phone\":\"" + c.getPhone() + "\"" +
                "}";
        }

        private Customer parseCustomerJson(String json) {
            Customer customer = new Customer();
            json = json.replace("{", "").replace("}", "");
            String[] fields = json.split(",");
            
            for (String field : fields) {
                String[] kv = field.split(":", 2);
                String key = kv[0].trim().replace("\"", "");
                String value = kv[1].trim().replace("\"", "");
                
                switch (key) {
                    case "customerId": customer.setCustomerId(value); break;
                    case "name": customer.setName(value); break;
                    case "email": customer.setEmail(value); break;
                    case "address": customer.setAddress(value); break;
                    case "phone": customer.setPhone(value); break;
                }
            }
            return customer;
        }
    }

    // Handler for /bills endpoint
    static class BillsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (path.matches("/bills/customer/[^/]+")) {
                    String customerId = path.substring(path.lastIndexOf("/") + 1);
                    handleGetCustomerBills(exchange, customerId);
                } else {
                    handleGetAllBills(exchange);
                }
            } else if (method.equals("POST")) {
                handleGenerateBill(exchange);
            } else if (method.equals("PUT")) {
                String billId = path.split("/")[2];
                handlePayBill(exchange, billId);
            }
        }

        private void handleGetAllBills(HttpExchange exchange) throws IOException {
            List<Bill> bills = DataStore.readBills();
            String response = billsToJson(bills);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handleGetCustomerBills(HttpExchange exchange, String customerId) throws IOException {
            List<Bill> allBills = DataStore.readBills();
            List<Bill> customerBills = new ArrayList<>();
            
            for (Bill b : allBills) {
                if (b.getCustomerId().equals(customerId)) {
                    customerBills.add(b);
                }
            }

            String response = billsToJson(customerBills);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handleGenerateBill(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                body.append(line);
            }

            Bill bill = parseBillJson(body.toString());
            bill.setBillId("BILL" + String.format("%04d", billCounter++));
            
            List<Bill> bills = DataStore.readBills();
            bills.add(bill);
            DataStore.writeBills(bills);

            String response = "{\"message\":\"Bill generated successfully\"}";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handlePayBill(HttpExchange exchange, String billId) throws IOException {
            List<Bill> bills = DataStore.readBills();
            
            for (Bill b : bills) {
                if (b.getBillId().equals(billId)) {
                    b.setStatus("PAID");
                    break;
                }
            }
            
            DataStore.writeBills(bills);

            String response = "{\"message\":\"Bill paid successfully\"}";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String billsToJson(List<Bill> bills) {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < bills.size(); i++) {
                Bill b = bills.get(i);
                json.append("{");
                json.append("\"billId\":\"").append(b.getBillId()).append("\",");
                json.append("\"customerId\":\"").append(b.getCustomerId()).append("\",");
                json.append("\"unitsConsumed\":").append(b.getUnitsConsumed()).append(",");
                json.append("\"ratePerUnit\":").append(b.getRatePerUnit()).append(",");
                json.append("\"totalAmount\":").append(b.getTotalAmount()).append(",");
                json.append("\"billMonth\":\"").append(b.getBillMonth()).append("\",");
                json.append("\"status\":\"").append(b.getStatus()).append("\",");
                json.append("\"generatedDate\":\"").append(b.getGeneratedDate()).append("\"");
                json.append("}");
                if (i < bills.size() - 1) json.append(",");
            }
            json.append("]");
            return json.toString();
        }

        private Bill parseBillJson(String json) {
            json = json.replace("{", "").replace("}", "");
            String[] fields = json.split(",");
            
            String customerId = "";
            double unitsConsumed = 0;
            double ratePerUnit = 0;
            String billMonth = "";
            
            for (String field : fields) {
                String[] kv = field.split(":", 2);
                String key = kv[0].trim().replace("\"", "");
                String value = kv[1].trim().replace("\"", "");
                
                switch (key) {
                    case "customerId": customerId = value; break;
                    case "unitsConsumed": unitsConsumed = Double.parseDouble(value); break;
                    case "ratePerUnit": ratePerUnit = Double.parseDouble(value); break;
                    case "billMonth": billMonth = value; break;
                }
            }
            
            return new Bill("", customerId, unitsConsumed, ratePerUnit, billMonth);
        }
    }
}