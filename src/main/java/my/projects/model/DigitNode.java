package my.projects.model;

import java.util.LinkedList;
import java.util.List;

public class DigitNode {
    public DigitNode() {

    }

    public DigitNode(int value, DigitNode parent) {
        this.value = value;
        this.parent = parent;
        this.sum = (parent != null ? parent.getSum() : 0) + value;
    }

    public DigitNode(long ID, int value, DigitNode parent) {
        this(value, parent);
        this.ID = ID;
    }

    private long ID;
    private int value;
    private int sum;
    private DigitNode parent;
    private List<DigitNode> children;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public DigitNode getParent() {
        return parent;
    }

    public void setParent(DigitNode parent) {
        this.parent = parent;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<DigitNode> getChildren() {
        return children;
    }

    public void setChildren(List<DigitNode> children) {
        this.children = children;
    }

    public void addChild(DigitNode c) {
        if (children == null) {
            children = new LinkedList<>();
        }
        children.add(c);
    }

    public void removeChild(DigitNode c) {
        if (children != null) {
            children.remove(c);
        }
        if (children.size() < 1) {
            children = null;
        }
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int parentsSum) {
        this.sum = parentsSum;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
