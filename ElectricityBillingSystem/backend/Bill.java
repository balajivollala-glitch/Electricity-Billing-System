import java.time.LocalDate;

public class Bill {
    private String billId;
    private String customerId;
    private double unitsConsumed;
    private double ratePerUnit;
    private double totalAmount;
    private String billMonth;
    private String status; // PAID or UNPAID
    private String generatedDate;

    // Constructor
    public Bill(String billId, String customerId, double unitsConsumed, 
                double ratePerUnit, String billMonth) {
        this.billId = billId;
        this.customerId = customerId;
        this.unitsConsumed = unitsConsumed;
        this.ratePerUnit = ratePerUnit;
        this.totalAmount = unitsConsumed * ratePerUnit;
        this.billMonth = billMonth;
        this.status = "UNPAID";
        this.generatedDate = LocalDate.now().toString();
    }

    // Empty constructor for JSON parsing
    public Bill() {
    }

    // Getters
    public String getBillId() {
        return billId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getUnitsConsumed() {
        return unitsConsumed;
    }

    public double getRatePerUnit() {
        return ratePerUnit;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getBillMonth() {
        return billMonth;
    }

    public String getStatus() {
        return status;
    }

    public String getGeneratedDate() {
        return generatedDate;
    }

    // Setters
    public void setBillId(String billId) {
        this.billId = billId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setUnitsConsumed(double unitsConsumed) {
        this.unitsConsumed = unitsConsumed;
    }

    public void setRatePerUnit(double ratePerUnit) {
        this.ratePerUnit = ratePerUnit;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }
}