import java.sql.Date;

public class ServiceRequestInput {
    private final int customerId;
    private final String vin;
    private final Date date;
    private final int odometer;
    private final String complain;

    public ServiceRequestInput(int customerId, String vin, Date date, int odometer, String complain) {
        this.customerId = customerId;
        this.vin = vin;
        this.date = date;
        this.odometer = odometer;
        this.complain = complain;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getVin() {
        return vin;
    }

    public Date getDate() {
        return date;
    }

    public int getOdometer() {
        return odometer;
    }

    public String getComplain() {
        return complain;
    }
}
