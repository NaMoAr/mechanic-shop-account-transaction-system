import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlAccountRepository implements AccountRepository {
    private final DbClient db;

    public SqlAccountRepository(DbClient db) {
        this.db = db;
    }

    @Override
    public CustomerAccount getAccount(int customerId) throws SQLException {
        String sql = "SELECT c.id, c.fname, c.lname, "
                + "COALESCE(open_sr.cnt, 0) AS open_requests, "
                + "COALESCE(total_bill.sum_bill, 0) AS total_billed "
                + "FROM Customer c "
                + "LEFT JOIN ("
                + "   SELECT sr.customer_id, COUNT(*) AS cnt "
                + "   FROM Service_Request sr "
                + "   LEFT JOIN Closed_Request cr ON cr.rid = sr.rid "
                + "   WHERE cr.rid IS NULL "
                + "   GROUP BY sr.customer_id"
                + ") open_sr ON open_sr.customer_id = c.id "
                + "LEFT JOIN ("
                + "   SELECT sr.customer_id, SUM(cr.bill) AS sum_bill "
                + "   FROM Service_Request sr "
                + "   JOIN Closed_Request cr ON cr.rid = sr.rid "
                + "   GROUP BY sr.customer_id"
                + ") total_bill ON total_bill.customer_id = c.id "
                + "WHERE c.id = ?";

        List<List<String>> rows = db.executeQuery(sql, stmt -> stmt.setInt(1, customerId));
        if (rows.isEmpty()) {
            return null;
        }

        List<String> row = rows.get(0);
        return new CustomerAccount(
                Integer.parseInt(row.get(0).trim()),
                row.get(1).trim(),
                row.get(2).trim(),
                Integer.parseInt(row.get(3).trim()),
                Double.parseDouble(row.get(4).trim())
        );
    }

    @Override
    public List<TransactionRecord> getTransactions(int customerId) throws SQLException {
        List<TransactionRecord> ledger = new ArrayList<TransactionRecord>();

        String openedSql = "SELECT sr.rid, sr.date::text, sr.complain "
                + "FROM Service_Request sr "
                + "WHERE sr.customer_id = ? "
                + "ORDER BY sr.date ASC";

        List<List<String>> opened = db.executeQuery(openedSql, stmt -> stmt.setInt(1, customerId));
        for (List<String> row : opened) {
            ledger.add(new TransactionRecord(
                    TransactionType.REQUEST_OPENED,
                    Integer.parseInt(row.get(0).trim()),
                    row.get(1),
                    row.get(2),
                    0.0
            ));
        }

        String closedSql = "SELECT sr.rid, cr.date::text, cr.comment, cr.bill "
                + "FROM Service_Request sr "
                + "JOIN Closed_Request cr ON cr.rid = sr.rid "
                + "WHERE sr.customer_id = ? "
                + "ORDER BY cr.date ASC";

        List<List<String>> closed = db.executeQuery(closedSql, stmt -> stmt.setInt(1, customerId));
        for (List<String> row : closed) {
            ledger.add(new TransactionRecord(
                    TransactionType.REQUEST_CLOSED,
                    Integer.parseInt(row.get(0).trim()),
                    row.get(1),
                    row.get(2),
                    Double.parseDouble(row.get(3).trim())
            ));
        }

        return ledger;
    }
}
