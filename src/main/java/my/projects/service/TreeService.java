package my.projects.service;

import my.projects.io.IOServiceFactory;
import my.projects.io.IOServiceFactory.ServiceType;
import my.projects.model.DigitNode;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

@Service
public class TreeService {
    private JTree tree;

    public void setTree(JTree tree) {
        this.tree = tree;
        setTreeExpandedState();
        makeTreeUnCollapsible();
    }

    /**
     * Insert child node to parent node on the <b>objects</b> and <b>tree view</b> level.<br>
     * Set the parent node always expanded and refresh the view.
     *
     * @param parentTreeNode - to which will be added a child node
     * @param childNode      - which will be added to parent node
     */
    public void insertNode(DefaultMutableTreeNode parentTreeNode, DigitNode childNode) {
        DigitNode parentNode = (DigitNode) parentTreeNode.getUserObject();
        parentNode.addChild(childNode);

        DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childNode);
        parentTreeNode.add(childTreeNode);

        setNodeExpandedState(parentTreeNode);
        updateTreeView();
    }

    /**
     * Use {@link #updateChildrenSumValue(DigitNode, int)} method and then refresh the tree view.
     *
     * @param initDigitNode - start node of recurrence
     * @param difference    - state value, which must be included to every node
     */
    public void updateNodesOnView(DigitNode initDigitNode, int difference) {
        initDigitNode.setValue(initDigitNode.getValue() + difference);
        updateChildrenSumValue(initDigitNode, difference);
        updateTreeView();
    }

    /**
     * <b>Works recursively!</b><br>
     * Iterate for every child node and adds the difference to current value of sum of parent nodes.
     *
     * @param parent     node in current iteration.
     * @param difference value which will be included to a sum of every iterated node.
     */
    private void updateChildrenSumValue(DigitNode parent, int difference) {
        List<DigitNode> children = parent.getChildren();
        if (children != null) {
            for (DigitNode child : children) {
                updateChildrenSumValue(child, difference);
            }
        }
        parent.setSum(parent.getSum() + difference);
    }

    /**
     * <b>Works recursively!</b><br>
     * Iterate throw every child of parent node and add its ID to the return list of ID's
     *
     * @param parent - parent node in current iteration
     * @param IDs    - list of ID's, which will be used in recurence process <b>(return value)</b>
     * @return filled list of ID's
     */
    public List<Long> getParentAndChildrenIDs(DigitNode parent, List<Long> IDs) {
        List<DigitNode> children = parent.getChildren();
        if (children != null) {
            for (DigitNode child : children) {
                getParentAndChildrenIDs(child, IDs);
            }
        }
        IDs.add(parent.getID());
        return IDs;
    }

    /**
     * Remove a node and all its children from current tree view.<br>
     * Also removes child from parent on <b>objects</b> level.<br>
     * Then refresh the view.
     *
     * @param node which will be removed with children
     */
    public void removeNode(DefaultMutableTreeNode node) {
        // Remove child from parent on objects level
        DigitNode digitNode = (DigitNode) node.getUserObject();
        digitNode.getParent().removeChild((DigitNode) node.getUserObject());
        digitNode = null;

        // Remove node on tree view level
        node.removeAllChildren();
        node.removeFromParent();

        updateTreeView();
    }

    private void updateTreeView() {
        tree.updateUI();
    }

    private void setTreeExpandedState() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
        setNodeExpandedState(node);
    }

    private void setNodeExpandedState(TreeNode node) {
        @SuppressWarnings("unchecked")
        ArrayList<? extends TreeNode> list = Collections.list(node.children());
        for (TreeNode treeNode : list) {
            setNodeExpandedState(treeNode);
        }
        tree.expandPath(new TreePath(((DefaultMutableTreeNode) node).getPath()));
    }

    private void makeTreeUnCollapsible() {
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {

            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                throw new ExpandVetoException(event, "Collapsing tree not allowed");
            }
        });
    }

    public DefaultMutableTreeNode uploadInitialNodes(boolean isForTest) {
        if (!isForTest) {
            my.projects.io.IOService database = IOServiceFactory.getIOService(ServiceType.Database);
            my.projects.repository.DigitNodeRepository repository = my.projects.repository.DigitNodeRepository.getInstance();

            Map<Long, DefaultMutableTreeNode> treeNodeMap = new HashMap<>();

            List<Long> allNodeIDs = database.getAllNodeIDs();
            for (Long ID : allNodeIDs) {
                DigitNode node = database.getNode(ID);

                repository.addNode(node);
                treeNodeMap.put(node.getID(), new DefaultMutableTreeNode(node));
            }

            for (Long ID : allNodeIDs) {
                DigitNode node = repository.getNode(ID);
                node.setParent(repository.getNode(database.getParentID(node.getID())));
                node.setChildren(repository.getNodes(database.getChildrenIDs(node.getID())));
            }

            DigitNode rootNode = repository.getNodes(allNodeIDs)
                    .stream()
                    .filter(node -> node.getParent() == null)
                    .findFirst()
                    .get();

            DefaultMutableTreeNode rootTreeNode = treeNodeMap.get(rootNode.getID());

            insertInitialNodesOnTreeView(rootTreeNode, treeNodeMap);

            my.projects.repository.DigitNodeRepository.dispose();

            return rootTreeNode;
        } else {
            DigitNode node1 = new DigitNode(1, 0, null);
            DigitNode node2 = new DigitNode(2, 15, node1);
            DigitNode node3 = new DigitNode(3, 5, node2);
            DigitNode node4 = new DigitNode(4, 15, node2);
            DigitNode node5 = new DigitNode(5, -45, node1);
            DigitNode node6 = new DigitNode(6, -5, node5);

            node1.addChild(node2);
            node1.addChild(node5);

            node2.addChild(node3);
            node2.addChild(node4);

            node5.addChild(node6);

            DefaultMutableTreeNode treeNode1 = new DefaultMutableTreeNode(node1);
            DefaultMutableTreeNode treeNode2 = new DefaultMutableTreeNode(node2);
            DefaultMutableTreeNode treeNode3 = new DefaultMutableTreeNode(node3);
            DefaultMutableTreeNode treeNode4 = new DefaultMutableTreeNode(node4);
            DefaultMutableTreeNode treeNode5 = new DefaultMutableTreeNode(node5);
            DefaultMutableTreeNode treeNode6 = new DefaultMutableTreeNode(node6);

            treeNode1.add(treeNode2);
            treeNode1.add(treeNode5);

            treeNode2.add(treeNode3);
            treeNode2.add(treeNode4);

            treeNode5.add(treeNode6);

            return treeNode1;
        }
    }

    private void insertInitialNodesOnTreeView(DefaultMutableTreeNode parent, Map<Long, DefaultMutableTreeNode> treeNodeMap) {
        DigitNode parentNode = (DigitNode) parent.getUserObject();
        if (parentNode.getChildren() != null) {
            for (DigitNode node : parentNode.getChildren()) {
                insertInitialNodesOnTreeView(treeNodeMap.get(node.getID()), treeNodeMap);
                parent.add(treeNodeMap.get(node.getID()));
            }
        }
    }
}
