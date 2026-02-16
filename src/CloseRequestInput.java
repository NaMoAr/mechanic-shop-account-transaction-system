import java.sql.Date;

public class CloseRequestInput {
    private final int requestId;
    private final int mechanicId;
    private final Date date;
    private final String comment;
    private final int baseBill;

    public CloseRequestInput(int requestId, int mechanicId, Date date, String comment, int baseBill) {
        this.requestId = requestId;
        this.mechanicId = mechanicId;
        this.date = date;
        this.comment = comment;
        this.baseBill = baseBill;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getMechanicId() {
        return mechanicId;
    }

    public Date getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public int getBaseBill() {
        return baseBill;
    }
}
