package my.projects.localization;

class PL implements Localization {
    public String getCreateButtonText() {
        return "Dodaj komórkę";
    }

    public String getUpdateButtonText() {
        return "Zmień wartość";
    }

    public String getDeleteButtonText() {
        return "Usuń komurkę";
    }

    public String getSumLabelText() {
        return "Suma rodziców: ";
    }

    public String getCreateChildDialogTitleText() {
        return "Wprowadź liczbę całkowitą";
    }

    public String getUpdateValueDialogTitleText() {
        return "Wprowadż nową wartość";
    }
}
