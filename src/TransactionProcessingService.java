import java.sql.SQLException;

public class TransactionProcessingService {
    private final TransactionRepository transactionRepository;
    private final BillingStrategyFactory billingStrategyFactory;

    public TransactionProcessingService(TransactionRepository transactionRepository,
                                        BillingStrategyFactory billingStrategyFactory) {
        this.transactionRepository = transactionRepository;
        this.billingStrategyFactory = billingStrategyFactory;
    }

    public int openRequest(ServiceRequestInput input) throws SQLException {
        if (input.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }
        if (input.getVin() == null || input.getVin().trim().isEmpty()) {
            throw new IllegalArgumentException("VIN is required.");
        }
        if (input.getOdometer() <= 0) {
            throw new IllegalArgumentException("Odometer must be positive.");
        }
        if (!transactionRepository.customerExists(input.getCustomerId())) {
            throw new IllegalArgumentException("Customer does not exist: " + input.getCustomerId());
        }
        if (!transactionRepository.carExists(input.getVin())) {
            throw new IllegalArgumentException("Car does not exist: " + input.getVin());
        }
        if (!transactionRepository.customerOwnsCar(input.getCustomerId(), input.getVin())) {
            throw new IllegalArgumentException("Customer " + input.getCustomerId()
                    + " does not own car " + input.getVin());
        }
        return transactionRepository.openServiceRequest(input);
    }

    public int closeRequest(CloseRequestInput input) throws SQLException {
        if (input.getRequestId() <= 0) {
            throw new IllegalArgumentException("Service request ID must be positive.");
        }
        if (input.getMechanicId() <= 0) {
            throw new IllegalArgumentException("Mechanic ID must be positive.");
        }
        if (input.getBaseBill() <= 0) {
            throw new IllegalArgumentException("Base bill must be positive.");
        }
        if (!transactionRepository.requestExists(input.getRequestId())) {
            throw new IllegalArgumentException("Service request does not exist: " + input.getRequestId());
        }
        if (transactionRepository.requestAlreadyClosed(input.getRequestId())) {
            throw new IllegalArgumentException("Service request already closed: " + input.getRequestId());
        }
        if (!transactionRepository.mechanicExists(input.getMechanicId())) {
            throw new IllegalArgumentException("Mechanic does not exist: " + input.getMechanicId());
        }

        int experience = transactionRepository.getMechanicExperience(input.getMechanicId());
        BillingStrategy strategy = billingStrategyFactory.create(experience);
        int computedBill = strategy.apply(input.getBaseBill(), experience);

        return transactionRepository.closeServiceRequest(input, computedBill);
    }
}
