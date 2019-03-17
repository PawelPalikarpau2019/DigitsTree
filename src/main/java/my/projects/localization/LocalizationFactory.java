package my.projects.localization;

public class LocalizationFactory {
    public enum Language {
        EN, PL
    }

    public static Localization getLocalization(Language language) {
        switch (language) {
            case EN:
                return new EN();
            case PL:
                return new PL();
            default:
                throw new RuntimeException("Unknown language: " + language);
        }
    }
}
