import java.sql.SQLException;
import java.util.List;

public interface AccountRepository {
    CustomerAccount getAccount(int customerId) throws SQLException;
    List<TransactionRecord> getTransactions(int customerId) throws SQLException;
}
