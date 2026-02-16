public class CustomerAccount {
    private final int customerId;
    private final String firstName;
    private final String lastName;
    private final int openRequests;
    private final double totalBilled;

    public CustomerAccount(int customerId, String firstName, String lastName, int openRequests, double totalBilled) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.openRequests = openRequests;
        this.totalBilled = totalBilled;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getOpenRequests() {
        return openRequests;
    }

    public double getTotalBilled() {
        return totalBilled;
    }
}
