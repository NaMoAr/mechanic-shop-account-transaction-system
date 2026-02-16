import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class AccountTrackingService {
    private final AccountRepository accountRepository;

    public AccountTrackingService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public CustomerAccount getCustomerAccount(int customerId) throws SQLException {
        return accountRepository.getAccount(customerId);
    }

    public List<TransactionRecord> getCustomerLedger(int customerId) throws SQLException {
        List<TransactionRecord> ledger = accountRepository.getTransactions(customerId);
        ledger.sort(Comparator.comparing(TransactionRecord::getDate).thenComparing(TransactionRecord::getRequestId));
        return ledger;
    }
}
