package my.projects.localization;

class EN implements Localization {
    public String getCreateButtonText() {
        return "Create Node";
    }

    public String getUpdateButtonText() {
        return "Update Value";
    }

    public String getDeleteButtonText() {
        return "Delete Node";
    }

    public String getSumLabelText() {
        return "Sum of parents: ";
    }

    public String getCreateChildDialogTitleText() {
        return "Enter an integer";
    }

    public String getUpdateValueDialogTitleText() {
        return "Enter new Value";
    }
}
