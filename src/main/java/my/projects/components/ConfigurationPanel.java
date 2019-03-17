package my.projects.components;

import my.projects.io.IOService;
import my.projects.io.IOServiceFactory;
import my.projects.io.IOServiceFactory.ServiceType;
import my.projects.localization.Localization;
import my.projects.localization.LocalizationFactory;
import my.projects.localization.LocalizationFactory.Language;
import my.projects.model.DigitNode;
import my.projects.repository.DigitNodeRepository;
import my.projects.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
public class ConfigurationPanel {
    private TreeService treeService;

    private IOService database;
    private Localization localization;

    private DefaultMutableTreeNode selectedNode;

    private JButton createChildButton;
    private JButton updateValueButton;
    private JButton removeNodeButton;

    private JComboBox<Language> languageComboBox;

    private JLabel sumTextLabel;
    private JLabel sumValueLabel;

    @Autowired
    public ConfigurationPanel(TreeService treeService) {
        this.treeService = treeService;
        this.database = IOServiceFactory.getIOService(ServiceType.Database);
        this.localization = LocalizationFactory.getLocalization(Language.EN);
    }

    public JPanel getPanel() {
        JPanel configurationPanel = new JPanel();
        configurationPanel.setLayout(new BoxLayout(configurationPanel, BoxLayout.X_AXIS));
        configurationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));

        initializeComponents();
        buildPanel(configurationPanel);

        return configurationPanel;
    }

    private void initializeComponents() {
        createChildButton = getCreateChildButton();
        updateValueButton = getUpdateValueButton();
        removeNodeButton = getRemoveNodeButton();

        languageComboBox = getLanguageComboBox();

        sumTextLabel = new JLabel();
        sumValueLabel = new JLabel();
        sumValueLabel.setForeground(new Color(0, 153, 0));

        refreshButtonsActivity();
        refreshLocalization();
    }

    private void buildPanel(JPanel configurationPanel) {
        // Create left side of configuration buttonsPanel
        // Make buttonsPanel and button's part unresizable
        // Make every button the same size
        // **************************************************** //
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        Dimension sizeB = new Dimension(400, 40);
        buttonsPanel.setMinimumSize(sizeB);
        buttonsPanel.setPreferredSize(sizeB);
        buttonsPanel.setMaximumSize(sizeB);

        GridBagConstraints bc = new GridBagConstraints();
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.gridy = 0;
        bc.gridx = 0;
        bc.insets = new Insets(5, 5, 5, 5);
        bc.weightx = 0.33;
        buttonsPanel.add(createChildButton, bc);
        bc.gridx = 1;
        buttonsPanel.add(updateValueButton, bc);
        bc.gridx = 2;
        buttonsPanel.add(removeNodeButton, bc);
        // **************************************************** //

        // Make combobox unresizable
        JPanel comboBoxPanel = new JPanel(new GridLayout(0, 1));
        Dimension sizeC = new Dimension(50, 25);
        comboBoxPanel.setMinimumSize(sizeC);
        comboBoxPanel.setPreferredSize(sizeC);
        comboBoxPanel.setMaximumSize(sizeC);
        comboBoxPanel.add(languageComboBox);

        configurationPanel.add(buttonsPanel);
        configurationPanel.add(Box.createHorizontalStrut(10));
        configurationPanel.add(comboBoxPanel);
        configurationPanel.add(Box.createHorizontalStrut(10));
        configurationPanel.add(sumTextLabel);
        configurationPanel.add(sumValueLabel);
        configurationPanel.add(Box.createHorizontalGlue());
    }

    private JButton getCreateChildButton() {
        JButton createButton = new JButton();
        createButton.addActionListener(e ->
        {
            // Just it case
            if (selectedNode != null) {
                // Initialize spinner with:
                // a) zero as a start value
                // b) "infinite" min and max values
                // c) and step equals one
                SpinnerModel spinnerConfiguration = new SpinnerNumberModel(0, null, null, 1);
                JSpinner spinner = new JSpinner(spinnerConfiguration);

                // Show dialog window to set value of new node
                int dialogResult = JOptionPane.showOptionDialog(null, spinner, localization.getCreateChildDialogTitleText(), JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (JOptionPane.OK_OPTION == dialogResult) {
                    DigitNode parentNode = (DigitNode) selectedNode.getUserObject();
                    DigitNode childNode = database.insertNode(new DigitNode((int) spinner.getValue(), parentNode));

                    database.insertRelation(parentNode.getID(), childNode.getID());
                    treeService.insertNode(selectedNode, childNode);
                    DigitNodeRepository.getInstance().addNode(childNode);
                }
            }
        });
        return createButton;
    }

    private JButton getUpdateValueButton() {
        JButton updateButton = new JButton();
        updateButton.addActionListener(e ->
        {
            // Just it case
            if (selectedNode != null) {
                // Initialize spinner with selected node value as start value
                DigitNode digitNode = (DigitNode) selectedNode.getUserObject();
                SpinnerModel spinnerConfiguration = new SpinnerNumberModel(digitNode.getValue(), null, null, 1);
                JSpinner spinner = new JSpinner(spinnerConfiguration);

                // Show dialog window for changing select node value
                int dialogResult = JOptionPane.showOptionDialog(null, spinner, localization.getUpdateValueDialogTitleText(), JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                int newValue = (int) spinner.getValue();
                // Do nothing if value does not changed
                if (JOptionPane.OK_OPTION == dialogResult && newValue != digitNode.getValue()) {
                    // Get only children IDs
                    List<Long> childrenIDs = treeService.getParentAndChildrenIDs(digitNode, new LinkedList<>());
                    childrenIDs.remove(digitNode.getID());

                    // Get difference between old and new values,
                    // depending on does the new value greater or lesser
                    int difference;
                    if (newValue > digitNode.getValue()) difference = newValue - digitNode.getValue();
                    else difference = (digitNode.getValue() - newValue) * -1;

                    // Update nodes on tree view
                    treeService.updateNodesOnView(digitNode, difference);

                    // Update nodes in database
                    database.updateNodeValues(digitNode);
                    if (childrenIDs.size() > 0) database.updateChildrenSums(childrenIDs, difference);

                    refreshSumValueLabel();
                }
            }
        });
        return updateButton;
    }

    private JButton getRemoveNodeButton() {
        JButton removeButton = new JButton();
        removeButton.addActionListener(e ->
        {
            // Just it case
            if (selectedNode != null) {
                DigitNode digitNode = (DigitNode) selectedNode.getUserObject();
                if (selectedNode != null && digitNode.getParent() != null) {
                    List<Long> IDs = treeService.getParentAndChildrenIDs(digitNode, new LinkedList<>());

                    // Remove from database selected node, its children and relations
                    database.removeNodeAndChildren(IDs);

                    // Remove from view selected node and children
                    treeService.removeNode(selectedNode);

                    DigitNodeRepository.getInstance().removeNodes(IDs);

                    // Refreshing buttons and label value
                    setSelectedNode(null);
                }
            }
        });
        return removeButton;
    }

    private JComboBox<Language> getLanguageComboBox() {
        JComboBox<Language> comboBox = new JComboBox<>(Language.values());
        comboBox.addActionListener(e -> {
            Language selectedLanguage = (Language) Objects.requireNonNull(comboBox.getSelectedItem());
            localization = LocalizationFactory.getLocalization(selectedLanguage);
            refreshLocalization();
        });
        return comboBox;
    }

    /**
     * Updates current selected node for configuration panel.</br>
     * Refreshes button activity and sum value label.
     *
     * @param selectedNode by user
     */
    public void setSelectedNode(DefaultMutableTreeNode selectedNode) {
        this.selectedNode = selectedNode;
        refreshButtonsActivity();
        refreshSumValueLabel();
    }

    private void refreshButtonsActivity() {
        createChildButton.setEnabled(selectedNode != null);
        updateValueButton.setEnabled(selectedNode != null);
        removeNodeButton.setEnabled(selectedNode != null && selectedNode.getParent() != null);
    }

    private void refreshSumValueLabel() {
        String value = String.valueOf(selectedNode != null ? ((DigitNode) selectedNode.getUserObject()).getSum() : "");
        sumValueLabel.setText(value);
    }

    private void refreshLocalization() {
        createChildButton.setText(localization.getCreateButtonText());
        updateValueButton.setText(localization.getUpdateButtonText());
        removeNodeButton.setText(localization.getDeleteButtonText());
        sumTextLabel.setText(localization.getSumLabelText());
    }
}
