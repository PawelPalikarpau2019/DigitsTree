package my.projects.components;

import my.projects.model.DigitNode;
import my.projects.repository.DigitNodeRepository;
import my.projects.service.TreeService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TreePanelTest {
    @InjectMocks
    private ConfigurationPanel configurationPanel;

    @InjectMocks
    private TreeService treeService;

    private DefaultMutableTreeNode rootTreeNode;
    private DigitNode rootNode;

    @Before
    public void setUp() {
        JPanel panel = new TreePanel(treeService, configurationPanel).getPanel(true);
        assertNotNull(panel);

        Component scroll = panel.getComponent(0);
        assertNotNull(scroll);
        assertTrue(scroll instanceof JScrollPane);

        Component viewport = ((JScrollPane) scroll).getComponent(0);
        assertNotNull(viewport);
        assertTrue(viewport instanceof JViewport);

        Component tree = ((JViewport) viewport).getComponent(0);
        assertNotNull(tree);
        assertTrue(tree instanceof JTree);

        assertTrue(((JTree) tree).getModel().getRoot() instanceof DefaultMutableTreeNode);
        rootTreeNode = (DefaultMutableTreeNode) ((JTree) tree).getModel().getRoot();
        assertNotNull(rootTreeNode);
        assertTrue(rootTreeNode.getUserObject() instanceof DigitNode);

        this.rootNode = (DigitNode) rootTreeNode.getUserObject();
        assertNotNull(rootNode);
    }

    @After
    public void tearDown() {
        configurationPanel = null;
        treeService = null;
        rootTreeNode = null;
        rootNode = null;
    }

    @Test
    public void test_InitializeTree() {
        assertEquals(getNodesAmountOnView(rootNode), 6);

        assertDigitNode(rootNode, 1, 0, 0);
        assertNull(rootNode.getParent());

        List<DigitNode> rootChildren = rootNode.getChildren();
        assertNotNull(rootChildren);
        assertEquals(rootChildren.size(), 2);

        DigitNode child_01 = rootChildren.get(0);
        DigitNode child_02 = rootChildren.get(1);
        assertNotSame(child_01, child_02);

        assertDigitNode(child_01, 2, 15, 15);
        assertDigitNode(child_02, 5, -45, -45);

        assertSame(child_01.getParent(), rootNode);
        assertSame(child_02.getParent(), rootNode);

        List<DigitNode> children_01 = child_01.getChildren();
        assertNotNull(children_01);
        assertEquals(children_01.size(), 2);

        List<DigitNode> children_02 = child_02.getChildren();
        assertNotNull(children_02);
        assertEquals(children_02.size(), 1);

        DigitNode child_011 = children_01.get(0);
        DigitNode child_012 = children_01.get(1);
        assertNotSame(child_011, child_012);

        assertDigitNode(child_011, 3, 5, 20);
        assertDigitNode(child_012, 4, 15, 30);

        assertSame(child_011.getParent(), child_01);
        assertSame(child_012.getParent(), child_01);

        assertNull(child_011.getChildren());
        assertNull(child_012.getChildren());

        DigitNode child_021 = children_02.get(0);
        assertDigitNode(child_021, 6, -5, -50);
        assertNull(child_021.getChildren());
        assertSame(child_021.getParent(), child_02);
    }

    @Test
    public void test_InsertNode() {
        assertEquals(getNodesAmountOnView(rootNode), 6);

        assertEquals(rootTreeNode.getChildCount(), 2);
        DefaultMutableTreeNode childTree_02 = (DefaultMutableTreeNode) rootTreeNode.getChildAt(1);
        assertEquals(childTree_02.getChildCount(), 1);

        DigitNode child_02 = rootNode.getChildren().get(1);

        assertNotNull(child_02.getChildren());
        assertEquals(child_02.getChildren().size(), 1);
        assertDigitNode(child_02, 5, -45, -45);

        treeService.insertNode(childTree_02, new DigitNode(7, 20, child_02));

        assertEquals(getNodesAmountOnView(rootNode), 7);

        assertEquals(childTree_02.getChildCount(), 2);
        assertNotNull(child_02.getChildren());
        assertEquals(child_02.getChildren().size(), 2);

        DigitNode child_021 = child_02.getChildren().get(0);
        DigitNode child_022 = child_02.getChildren().get(1);

        assertNotSame(child_021, child_022);

        assertDigitNode(child_022, 7, 20, -25);
        assertNull(child_022.getChildren());
        assertSame(child_022.getParent(), child_02);

        assertEquals(childTree_02.getChildCount(), 2);
        DefaultMutableTreeNode childTree_022 = (DefaultMutableTreeNode) childTree_02.getChildAt(1);
        treeService.insertNode(childTree_022, new DigitNode(8, -100, child_022));

        assertEquals(getNodesAmountOnView(rootNode), 8);

        assertEquals(childTree_022.getChildCount(), 1);
        assertNotNull(child_022.getChildren());
        assertEquals(child_022.getChildren().size(), 1);

        DigitNode child_0221 = child_022.getChildren().get(0);

        assertDigitNode(child_0221, 8, -100, -125);
        assertNull(child_0221.getChildren());
        assertSame(child_0221.getParent(), child_022);
    }

    @Test
    public void test_RemoveSingleNode() {
        assertEquals(getNodesAmountOnView(rootNode), 6);

        assertEquals(rootTreeNode.getChildCount(), 2);
        DefaultMutableTreeNode childTree_02 = (DefaultMutableTreeNode) rootTreeNode.getChildAt(1);
        assertEquals(childTree_02.getChildCount(), 1);

        assertEquals(childTree_02.getChildCount(), 1);
        DefaultMutableTreeNode childTree_021 = (DefaultMutableTreeNode) childTree_02.getChildAt(0);
        assertEquals(childTree_021.getChildCount(), 0);

        assertNotNull(rootNode.getChildren());
        assertEquals(rootNode.getChildren().size(), 2);
        DigitNode child_02 = rootNode.getChildren().get(1);

        assertNotNull(child_02.getChildren());
        assertEquals(child_02.getChildren().size(), 1);

        treeService.removeNode(childTree_021);

        assertEquals(getNodesAmountOnView(rootNode), 5);
        assertEquals(childTree_02.getChildCount(), 0);
        assertNull(child_02.getChildren());
    }

    @Test
    public void test_RemoveMultipleNodesCascade() {
        assertEquals(getNodesAmountOnView(rootNode), 6);

        assertEquals(rootTreeNode.getChildCount(), 2);
        DefaultMutableTreeNode childTree_01 = (DefaultMutableTreeNode) rootTreeNode.getChildAt(0);
        assertEquals(childTree_01.getChildCount(), 2);
        DefaultMutableTreeNode childTree_011 = (DefaultMutableTreeNode) childTree_01.getChildAt(0);
        assertEquals(childTree_011.getChildCount(), 0);
        DefaultMutableTreeNode childTree_012 = (DefaultMutableTreeNode) childTree_01.getChildAt(1);
        assertEquals(childTree_012.getChildCount(), 0);

        assertNotNull(rootNode.getChildren());
        assertEquals(rootNode.getChildren().size(), 2);

        DigitNode child_01 = rootNode.getChildren().get(0);

        assertNotNull(child_01.getChildren());
        assertEquals(child_01.getChildren().size(), 2);

        DigitNode child_011 = child_01.getChildren().get(0);
        DigitNode child_012 = child_01.getChildren().get(1);
        assertNotSame(child_011, child_012);
        assertSame(child_011.getParent(), child_01);
        assertSame(child_012.getParent(), child_01);
        assertNull(child_011.getChildren());
        assertNull(child_012.getChildren());

        treeService.removeNode(childTree_01);

        assertEquals(getNodesAmountOnView(rootNode), 3);

        assertNotNull(rootNode.getChildren());
        assertEquals(rootNode.getChildren().size(), 1);

        assertEquals(rootTreeNode.getChildCount(), 1);
    }

    @Test
    public void test_UpdateLeaveChildNodeValues() {
        assertNotNull(rootNode.getChildren());
        assertEquals(rootNode.getChildren().size(), 2);

        DigitNode child_02 = rootNode.getChildren().get(1);
        assertDigitNode(child_02, 5, -45, -45);
        assertSame(rootNode, child_02.getParent());

        assertNotNull(child_02.getChildren());
        assertEquals(child_02.getChildren().size(), 1);

        DigitNode child_021 = child_02.getChildren().get(0);
        assertDigitNode(child_021, 6, -5, -50);
        assertSame(child_02, child_021.getParent());

        assertNull(child_021.getChildren());

        treeService.updateNodesOnView(child_021, 50);

        assertDigitNode(child_02, 5, -45, -45);
        assertDigitNode(child_021, 6, 45, 0);
    }

    @Test
    public void test_UpdateParentNodeValues() {
        assertDigitNode(rootNode, 1, 0, 0);

        assertNotNull(rootNode.getChildren());
        assertEquals(rootNode.getChildren().size(), 2);

        DigitNode child_01 = rootNode.getChildren().get(0);
        assertDigitNode(child_01, 2, 15, 15);
        assertSame(rootNode, child_01.getParent());

        assertNotNull(child_01.getChildren());
        assertEquals(child_01.getChildren().size(), 2);

        DigitNode child_011 = child_01.getChildren().get(0);
        DigitNode child_012 = child_01.getChildren().get(1);
        assertDigitNode(child_011, 3, 5, 20);
        assertDigitNode(child_012, 4, 15, 30);
        assertSame(child_01, child_011.getParent());
        assertSame(child_01, child_012.getParent());

        assertNull(child_011.getChildren());
        assertNull(child_012.getChildren());

        treeService.updateNodesOnView(child_01, -10);

        assertDigitNode(rootNode, 1, 0, 0);
        assertDigitNode(child_01, 2, 5, 5);
        assertDigitNode(child_011, 3, 5, 10);
        assertDigitNode(child_012, 4, 15, 20);

        treeService.updateNodesOnView(child_011, -100);

        assertDigitNode(rootNode, 1, 0, 0);
        assertDigitNode(child_01, 2, 5, 5);
        assertDigitNode(child_011, 3, -95, -90);
        assertDigitNode(child_012, 4, 15, 20);
    }

    private int getNodesAmountOnView(DigitNode parent) {
        int count = 1;
        if (parent.getChildren() != null) {
            for (DigitNode child : parent.getChildren()) {
                count += getNodesAmountOnView(child);
            }
        }
        return count;
    }

    private void assertDigitNode(DigitNode nodeToCheck, long expID, int expValue, int expSum) {
        assertEquals(nodeToCheck.getID(), expID);
        assertEquals(nodeToCheck.getValue(), expValue);
        assertEquals(nodeToCheck.getSum(), expSum);
    }
}
