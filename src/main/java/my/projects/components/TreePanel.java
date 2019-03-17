package my.projects.components;

import my.projects.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

@Component
public class TreePanel {
    private JTree tree;
    private TreeService treeService;

    private ConfigurationPanel configurationPanel;

    @Autowired
    public TreePanel(TreeService treeService, ConfigurationPanel configurationPanel) {
        this.treeService = treeService;
        this.configurationPanel = configurationPanel;
    }

    public JPanel getPanel(boolean isForTest) {
        JPanel treePanel = new JPanel(new GridLayout(0, 1));

        //Create a tree that allows one selection at a time.
        tree = new JTree(treeService.uploadInitialNodes(isForTest));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeService.setTree(tree);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(e -> configurationPanel.setSelectedNode((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()));

        JScrollPane treeView = new JScrollPane(tree);

        Dimension minimumSize = new Dimension(100, 50);
        treeView.setMinimumSize(minimumSize);

        treePanel.add(treeView);

        return treePanel;
    }
}
