public class TransactionRecord {
    private final TransactionType type;
    private final int requestId;
    private final String date;
    private final String note;
    private final double amount;

    public TransactionRecord(TransactionType type, int requestId, String date, String note, double amount) {
        this.type = type;
        this.requestId = requestId;
        this.date = date;
        this.note = note;
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public double getAmount() {
        return amount;
    }
}
