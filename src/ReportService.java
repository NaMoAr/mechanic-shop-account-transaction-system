import java.sql.SQLException;

public class ReportService {
    private final DbClient db;

    public ReportService(DbClient db) {
        this.db = db;
    }

    public void listCustomersWithBillLessThan100() throws SQLException {
        String query = "SELECT c.id, c.fname, c.lname, c.phone, c.address "
                + "FROM Closed_Request cr "
                + "JOIN Service_Request sr ON cr.rid = sr.rid "
                + "JOIN Customer c ON sr.customer_id = c.id "
                + "WHERE cr.bill < 100";
        int rows = db.printQuery(query, stmt -> {
        });
        System.out.println("total row(s): " + rows);
    }

    public void listCustomersWithMoreThan20Cars() throws SQLException {
        String query = "SELECT c.id, c.fname, c.lname, c.phone, c.address "
                + "FROM Customer c "
                + "JOIN Owns o ON c.id = o.customer_id "
                + "GROUP BY c.id "
                + "HAVING COUNT(*) > 20";
        int rows = db.printQuery(query, stmt -> {
        });
        System.out.println("total row(s): " + rows);
    }

    public void listCarsBefore1995With50000Miles() throws SQLException {
        String query = "SELECT c.vin, c.make, c.model, c.year, sr.odometer "
                + "FROM Car c "
                + "JOIN Service_Request sr ON c.vin = sr.car_vin "
                + "WHERE c.year < 1995 AND sr.odometer >= 50000";
        int rows = db.printQuery(query, stmt -> {
        });
        System.out.println("total row(s): " + rows);
    }

    public void listTopKCarsByServices(int k) throws SQLException {
        String query = "SELECT * FROM ("
                + "SELECT c.vin, c.make, c.model, c.year, COUNT(*) AS service_count "
                + "FROM Service_Request sr "
                + "JOIN Car c ON sr.car_vin = c.vin "
                + "GROUP BY c.vin"
                + ") AS temp "
                + "ORDER BY temp.service_count DESC "
                + "FETCH FIRST ? ROWS ONLY";
        int rows = db.printQuery(query, stmt -> stmt.setInt(1, k));
        System.out.println("total row(s): " + rows);
    }

    public void listCustomersByTotalBillDescending() throws SQLException {
        String query = "SELECT * FROM ("
                + "SELECT c.id, c.fname, c.lname, c.phone, c.address, SUM(cr.bill) AS total_bill "
                + "FROM Closed_Request cr "
                + "JOIN Service_Request sr ON cr.rid = sr.rid "
                + "JOIN Customer c ON sr.customer_id = c.id "
                + "GROUP BY c.id"
                + ") AS temp "
                + "ORDER BY temp.total_bill DESC";
        int rows = db.printQuery(query, stmt -> {
        });
        System.out.println("total row(s): " + rows);
    }
}
