package my.projects.io;

import my.projects.model.DigitNode;

import java.util.List;

public interface IOService {
    List<Long> getAllNodeIDs();

    DigitNode insertNode(DigitNode node);

    DigitNode getNode(long nodeID);

    void updateNodeValues(DigitNode node);

    void updateChildrenSums(List<Long> childrenIDs, int difference);

    void removeNodeAndChildren(List<Long> IDs);

    void insertRelation(long parentID, long childID);

    List<Long> getChildrenIDs(long parentID);

    Long getParentID(long childID);
}
