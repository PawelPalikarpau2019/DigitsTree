package my.projects.io;

public class IOServiceFactory {
    public enum ServiceType {
        Database
    }

    public static IOService getIOService(ServiceType type) {
        switch (type) {
            case Database:
                return new IODatabaseService();
            default:
                throw new RuntimeException("Unknown type of IO service: " + type);
        }
    }
}
