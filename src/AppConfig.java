public class AppConfig {
    private final String dbName;
    private final String dbPort;
    private final String dbUser;
    private final String dbPassword;

    public AppConfig(String dbName, String dbPort, String dbUser, String dbPassword) {
        this.dbName = dbName;
        this.dbPort = dbPort;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public static AppConfig fromEnvironment(String[] args) {
        String dbName = getArgOrEnv(args, "--db-name", "MECHANICSHOP_DB_NAME", "carRepair");
        String dbPort = getArgOrEnv(args, "--db-port", "MECHANICSHOP_DB_PORT", "5432");
        String dbUser = getArgOrEnv(args, "--db-user", "MECHANICSHOP_DB_USER", "postgres");
        String dbPassword = getArgOrEnv(args, "--db-password", "MECHANICSHOP_DB_PASSWORD", null);

        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing database password. Set MECHANICSHOP_DB_PASSWORD or pass --db-password.");
        }

        return new AppConfig(dbName, dbPort, dbUser, dbPassword);
    }

    private static String getArgOrEnv(String[] args, String argName, String envName, String fallback) {
        for (int i = 0; i < args.length - 1; i++) {
            if (argName.equals(args[i])) {
                return args[i + 1];
            }
        }

        String env = System.getenv(envName);
        if (env != null && !env.trim().isEmpty()) {
            return env;
        }
        return fallback;
    }
}
