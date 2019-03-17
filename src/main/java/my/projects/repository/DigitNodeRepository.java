package my.projects.repository;

import my.projects.model.DigitNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DigitNodeRepository {
    private static DigitNodeRepository instance;

    private Map<Long, DigitNode> nodeMap;

    private DigitNodeRepository() {
        this.nodeMap = new HashMap<>();
    }

    public static DigitNodeRepository getInstance() {
        if (instance == null) {
            instance = new DigitNodeRepository();
        }
        return instance;
    }

    public static void dispose() {
        instance = null;
    }

    public void addNode(DigitNode node) {
        nodeMap.put(node.getID(), node);
    }

    public void removeNode(Long ID) {
        if (nodeMap.containsKey(ID)) {
            nodeMap.remove(ID);
        }
    }

    public void removeNodes(List<Long> IDs) {
        if (IDs != null) {
            for (Long ID : IDs) {
                if (nodeMap.containsKey(ID)) {
                    nodeMap.remove(ID);
                }
            }
        }
    }

    public DigitNode getNode(Long ID) {
        DigitNode result = null;
        if (nodeMap.containsKey(ID)) {
            result = nodeMap.get(ID);
        }
        return result;
    }

    public List<DigitNode> getNodes(List<Long> IDs) {
        List<DigitNode> result = null;
        if (IDs != null) {
            for (Long ID : IDs) {
                if (nodeMap.containsKey(ID)) {
                    if (result == null) result = new LinkedList<>();
                    result.add(nodeMap.get(ID));
                }
            }
        }
        return result;
    }
}
