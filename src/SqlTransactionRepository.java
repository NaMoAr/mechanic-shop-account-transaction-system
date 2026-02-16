import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlTransactionRepository implements TransactionRepository {
    private final DbClient db;

    public SqlTransactionRepository(DbClient db) {
        this.db = db;
    }

    @Override
    public int openServiceRequest(ServiceRequestInput input) throws SQLException {
        String sql = "INSERT INTO Service_Request (customer_id, car_vin, date, odometer, complain) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING rid";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, input.getCustomerId());
            stmt.setString(2, input.getVin());
            stmt.setDate(3, input.getDate());
            stmt.setInt(4, input.getOdometer());
            stmt.setString(5, input.getComplain());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Unable to create service request");
    }

    @Override
    public int closeServiceRequest(CloseRequestInput input, int computedBill) throws SQLException {
        String sql = "INSERT INTO Closed_Request (rid, mid, date, comment, bill) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING wid";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, input.getRequestId());
            stmt.setInt(2, input.getMechanicId());
            stmt.setDate(3, input.getDate());
            stmt.setString(4, input.getComment());
            stmt.setInt(5, computedBill);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Unable to close service request");
    }

    @Override
    public boolean customerExists(int customerId) throws SQLException {
        return existsById("SELECT 1 FROM Customer WHERE id = ?", customerId);
    }

    @Override
    public boolean carExists(String vin) throws SQLException {
        return existsByValue("SELECT 1 FROM Car WHERE vin = ?", vin);
    }

    @Override
    public boolean customerOwnsCar(int customerId, String vin) throws SQLException {
        String sql = "SELECT 1 FROM Owns WHERE customer_id = ? AND car_vin = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, vin);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean mechanicExists(int mechanicId) throws SQLException {
        return existsById("SELECT 1 FROM Mechanic WHERE id = ?", mechanicId);
    }

    @Override
    public boolean requestExists(int requestId) throws SQLException {
        return existsById("SELECT 1 FROM Service_Request WHERE rid = ?", requestId);
    }

    @Override
    public boolean requestAlreadyClosed(int requestId) throws SQLException {
        return existsById("SELECT 1 FROM Closed_Request WHERE rid = ?", requestId);
    }

    @Override
    public int getMechanicExperience(int mechanicId) throws SQLException {
        String sql = "SELECT experience FROM Mechanic WHERE id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, mechanicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Mechanic not found: " + mechanicId);
    }

    private boolean existsById(String sql, int id) throws SQLException {
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean existsByValue(String sql, String value) throws SQLException {
        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
