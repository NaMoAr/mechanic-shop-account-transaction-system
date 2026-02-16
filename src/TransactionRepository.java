import java.sql.SQLException;

public interface TransactionRepository {
    int openServiceRequest(ServiceRequestInput input) throws SQLException;
    int closeServiceRequest(CloseRequestInput input, int computedBill) throws SQLException;
    boolean customerExists(int customerId) throws SQLException;
    boolean carExists(String vin) throws SQLException;
    boolean customerOwnsCar(int customerId, String vin) throws SQLException;
    boolean mechanicExists(int mechanicId) throws SQLException;
    boolean requestExists(int requestId) throws SQLException;
    boolean requestAlreadyClosed(int requestId) throws SQLException;
    int getMechanicExperience(int mechanicId) throws SQLException;
}
