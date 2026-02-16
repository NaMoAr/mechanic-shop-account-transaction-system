import java.sql.SQLException;
import java.util.List;

public class MechanicShop {
    public static void main(String[] args) {
        DbClient db = null;
        try {
            Class.forName("org.postgresql.Driver");
            AppConfig config = AppConfig.fromEnvironment(args);

            db = new DbClient(config.getDbName(), config.getDbPort(), config.getDbUser(), config.getDbPassword());
            InputReader inputReader = new InputReader();

            AccountRepository accountRepository = new SqlAccountRepository(db);
            TransactionRepository transactionRepository = new SqlTransactionRepository(db);

            AccountTrackingService accountTrackingService = new AccountTrackingService(accountRepository);
            TransactionProcessingService transactionService =
                    new TransactionProcessingService(transactionRepository, new BillingStrategyFactory());
            ReportService reportService = new ReportService(db);

            runMenu(inputReader, db, transactionService, accountTrackingService, reportService);
        } catch (Exception e) {
            System.err.println("Startup error: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private static void runMenu(InputReader input,
                                DbClient db,
                                TransactionProcessingService transactionService,
                                AccountTrackingService accountTrackingService,
                                ReportService reportService) throws Exception {
        boolean keepRunning = true;

        while (keepRunning) {
            printMenu();
            int choice = input.readInt("Please make your choice:");

            try {
                switch (choice) {
                    case 1:
                        addCustomer(input, db);
                        break;
                    case 2:
                        addMechanic(input, db);
                        break;
                    case 3:
                        addCar(input, db);
                        break;
                    case 4:
                        addOwnership(input, db);
                        break;
                    case 5:
                        openServiceRequest(input, transactionService);
                        break;
                    case 6:
                        closeServiceRequest(input, transactionService);
                        break;
                    case 7:
                        reportService.listCustomersWithBillLessThan100();
                        break;
                    case 8:
                        reportService.listCustomersWithMoreThan20Cars();
                        break;
                    case 9:
                        reportService.listCarsBefore1995With50000Miles();
                        break;
                    case 10:
                        reportService.listTopKCarsByServices(input.readInt("Enter k:"));
                        break;
                    case 11:
                        reportService.listCustomersByTotalBillDescending();
                        break;
                    case 12:
                        showAccountSummary(input, accountTrackingService);
                        break;
                    case 13:
                        showTransactionLedger(input, accountTrackingService);
                        break;
                    case 14:
                        keepRunning = false;
                        break;
                    default:
                        System.out.println("Unknown option.");
                }
            } catch (Exception ex) {
                System.err.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("MAIN MENU");
        System.out.println("---------");
        System.out.println("1. AddCustomer");
        System.out.println("2. AddMechanic");
        System.out.println("3. AddCar");
        System.out.println("4. AddOwnership");
        System.out.println("5. InsertServiceRequest");
        System.out.println("6. CloseServiceRequest");
        System.out.println("7. ListCustomersWithBillLessThan100");
        System.out.println("8. ListCustomersWithMoreThan20Cars");
        System.out.println("9. ListCarsBefore1995With50000Miles");
        System.out.println("10. ListCarsWithTheMostServices");
        System.out.println("11. ListCustomersInDescendingOrderOfTheirTotalBill");
        System.out.println("12. ViewCustomerAccountSummary");
        System.out.println("13. ViewCustomerTransactionLedger");
        System.out.println("14. EXIT");
    }

    private static void addCustomer(InputReader input, DbClient db) throws Exception {
        String firstName = input.readLine("Enter First Name:");
        String lastName = input.readLine("Enter Last Name:");
        String phone = input.readLine("Enter Phone Number:");
        String address = input.readLine("Enter Address:");

        String sql = "INSERT INTO Customer (fname, lname, phone, address) VALUES (?, ?, ?, ?)";
        db.executeUpdate(sql, stmt -> {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phone);
            stmt.setString(4, address);
        });
        System.out.println("A new customer has been inserted.");
    }

    private static void addMechanic(InputReader input, DbClient db) throws Exception {
        String firstName = input.readLine("Enter First Name:");
        String lastName = input.readLine("Enter Last Name:");
        int experience = input.readInt("Enter Years of Experience:");

        String sql = "INSERT INTO Mechanic (fname, lname, experience) VALUES (?, ?, ?)";
        db.executeUpdate(sql, stmt -> {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setInt(3, experience);
        });
        System.out.println("A new mechanic has been inserted.");
    }

    private static void addCar(InputReader input, DbClient db) throws Exception {
        String vin = input.readLine("Enter the VIN number:");
        String make = input.readLine("Enter the make:");
        String model = input.readLine("Enter the model:");
        int year = input.readInt("Enter the year:");

        String sql = "INSERT INTO Car (vin, make, model, year) VALUES (?, ?, ?, ?)";
        db.executeUpdate(sql, stmt -> {
            stmt.setString(1, vin);
            stmt.setString(2, make);
            stmt.setString(3, model);
            stmt.setInt(4, year);
        });
        System.out.println("A new car has been inserted.");
    }

    private static void addOwnership(InputReader input, DbClient db) throws Exception {
        int customerId = input.readInt("Enter customer id:");
        String vin = input.readLine("Enter car VIN:");

        String sql = "INSERT INTO Owns (customer_id, car_vin) VALUES (?, ?)";
        db.executeUpdate(sql, stmt -> {
            stmt.setInt(1, customerId);
            stmt.setString(2, vin);
        });
        System.out.println("Ownership has been created.");
    }

    private static void openServiceRequest(InputReader input, TransactionProcessingService transactionService) throws Exception {
        int customerId = input.readInt("Enter customer id:");
        String vin = input.readLine("Enter car VIN:");
        java.sql.Date date = input.readDate("Enter request date");
        int odometer = input.readInt("Enter odometer:");
        String complain = input.readLine("Enter complain:");

        ServiceRequestInput request = new ServiceRequestInput(customerId, vin, date, odometer, complain);
        int requestId = transactionService.openRequest(request);

        System.out.println("Service request created. RID: " + requestId);
    }

    private static void closeServiceRequest(InputReader input, TransactionProcessingService transactionService) throws Exception {
        int requestId = input.readInt("Enter the Service Request ID:");
        int mechanicId = input.readInt("Enter the Mechanic ID:");
        java.sql.Date date = input.readDate("Enter close date");
        String comment = input.readLine("Enter comment:");
        int baseBill = input.readInt("Enter base bill:");

        CloseRequestInput close = new CloseRequestInput(requestId, mechanicId, date, comment, baseBill);
        int workOrderId = transactionService.closeRequest(close);
        System.out.println("Service request closed. WID: " + workOrderId);
    }

    private static void showAccountSummary(InputReader input, AccountTrackingService accountTrackingService)
            throws SQLException, Exception {
        int customerId = input.readInt("Enter customer id:");
        CustomerAccount account = accountTrackingService.getCustomerAccount(customerId);
        if (account == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.println("Customer: " + account.getCustomerId() + " - "
                + account.getFirstName() + " " + account.getLastName());
        System.out.println("Open requests: " + account.getOpenRequests());
        System.out.println("Total billed: " + account.getTotalBilled());
    }

    private static void showTransactionLedger(InputReader input, AccountTrackingService accountTrackingService)
            throws SQLException, Exception {
        int customerId = input.readInt("Enter customer id:");
        List<TransactionRecord> ledger = accountTrackingService.getCustomerLedger(customerId);

        if (ledger.isEmpty()) {
            System.out.println("No transactions for this customer.");
            return;
        }

        System.out.println("Type\tRID\tDate\tAmount\tNote");
        for (TransactionRecord record : ledger) {
            System.out.println(record.getType() + "\t" + record.getRequestId() + "\t"
                    + record.getDate() + "\t" + record.getAmount() + "\t" + record.getNote());
        }
    }
}
