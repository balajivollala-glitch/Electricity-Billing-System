import java.io.*;
import java.util.*;

public class DataStore {
    private static final String CUSTOMERS_FILE = "../data/customers.json";
    private static final String BILLS_FILE = "../data/bills.json";

    // Read customers from JSON file
    public static List<Customer> readCustomers() {
        List<Customer> customers = new ArrayList<>();
        try {
            File file = new File(CUSTOMERS_FILE);
            if (!file.exists()) {
                return customers;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            // Parse JSON manually
            String jsonStr = json.toString().trim();
            if (jsonStr.equals("[]") || jsonStr.isEmpty()) {
                return customers;
            }

            // Remove outer brackets
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
            
            // Split by },{ to get individual objects
            String[] objects = jsonStr.split("\\},\\{");
            
            for (String obj : objects) {
                obj = obj.replace("{", "").replace("}", "");
                String[] fields = obj.split(",");
                
                Customer customer = new Customer();
                for (String field : fields) {
                    String[] keyValue = field.split(":", 2);
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    
                    switch (key) {
                        case "customerId": customer.setCustomerId(value); break;
                        case "name": customer.setName(value); break;
                        case "email": customer.setEmail(value); break;
                        case "address": customer.setAddress(value); break;
                        case "phone": customer.setPhone(value); break;
                    }
                }
                customers.add(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Write customers to JSON file
    public static void writeCustomers(List<Customer> customers) {
        try {
            File file = new File(CUSTOMERS_FILE);
            file.getParentFile().mkdirs();
            
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println("[");
            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                writer.print("  {");
                writer.print("\"customerId\":\"" + c.getCustomerId() + "\",");
                writer.print("\"name\":\"" + c.getName() + "\",");
                writer.print("\"email\":\"" + c.getEmail() + "\",");
                writer.print("\"address\":\"" + c.getAddress() + "\",");
                writer.print("\"phone\":\"" + c.getPhone() + "\"");
                writer.print("}");
                if (i < customers.size() - 1) writer.println(",");
                else writer.println();
            }
            writer.println("]");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read bills from JSON file
    public static List<Bill> readBills() {
        List<Bill> bills = new ArrayList<>();
        try {
            File file = new File(BILLS_FILE);
            if (!file.exists()) {
                return bills;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            String jsonStr = json.toString().trim();
            if (jsonStr.equals("[]") || jsonStr.isEmpty()) {
                return bills;
            }

            jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
            String[] objects = jsonStr.split("\\},\\{");
            
            for (String obj : objects) {
                obj = obj.replace("{", "").replace("}", "");
                String[] fields = obj.split(",");
                
                Bill bill = new Bill();
                for (String field : fields) {
                    String[] keyValue = field.split(":", 2);
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    
                    switch (key) {
                        case "billId": bill.setBillId(value); break;
                        case "customerId": bill.setCustomerId(value); break;
                        case "unitsConsumed": bill.setUnitsConsumed(Double.parseDouble(value)); break;
                        case "ratePerUnit": bill.setRatePerUnit(Double.parseDouble(value)); break;
                        case "totalAmount": bill.setTotalAmount(Double.parseDouble(value)); break;
                        case "billMonth": bill.setBillMonth(value); break;
                        case "status": bill.setStatus(value); break;
                        case "generatedDate": bill.setGeneratedDate(value); break;
                    }
                }
                bills.add(bill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bills;
    }

    // Write bills to JSON file
    public static void writeBills(List<Bill> bills) {
        try {
            File file = new File(BILLS_FILE);
            file.getParentFile().mkdirs();
            
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println("[");
            for (int i = 0; i < bills.size(); i++) {
                Bill b = bills.get(i);
                writer.print("  {");
                writer.print("\"billId\":\"" + b.getBillId() + "\",");
                writer.print("\"customerId\":\"" + b.getCustomerId() + "\",");
                writer.print("\"unitsConsumed\":" + b.getUnitsConsumed() + ",");
                writer.print("\"ratePerUnit\":" + b.getRatePerUnit() + ",");
                writer.print("\"totalAmount\":" + b.getTotalAmount() + ",");
                writer.print("\"billMonth\":\"" + b.getBillMonth() + "\",");
                writer.print("\"status\":\"" + b.getStatus() + "\",");
                writer.print("\"generatedDate\":\"" + b.getGeneratedDate() + "\"");
                writer.print("}");
                if (i < bills.size() - 1) writer.println(",");
                else writer.println();
            }
            writer.println("]");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}