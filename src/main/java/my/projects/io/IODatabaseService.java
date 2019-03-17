package my.projects.io;

import my.projects.model.DigitNode;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Service
public class IODatabaseService implements IOService {
    private final static String DIGIT_TREE = "digit_tree";
    private final static String RELATIONS = "relations";

    private final static String DIGIT_NODE_ID = "digit_node_id";
    private final static String VALUE = "value";
    private final static String SUM = "sum";

    private final static String PARENT_ID = "parent_id";
    private final static String CHILD_ID = "child_id";

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:db";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private Connection connection = null;

    private void insureConnection() throws Exception {
        if (connection == null) {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        }
    }

    private void closeConnection() throws Exception {
        if (connection != null) connection.close();
        connection = null;
    }

    public List<Long> getAllNodeIDs() {
        List<Long> result = null;
        try {
            String query = "SELECT " + DIGIT_NODE_ID + " FROM " + DIGIT_TREE;
            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                result = new LinkedList<>();
                do {
                    result.add(set.getLong(DIGIT_NODE_ID));
                }
                while (set.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public DigitNode insertNode(DigitNode node) {
        try {
            String query = "INSERT INTO " + DIGIT_TREE + " (" + VALUE + ", " + SUM + ") VALUES (" + node.getValue() + ", " + node.getSum() + ")";
            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            while (keys.next()) {
                node.setID(keys.getLong(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return node;
    }

    public DigitNode getNode(long nodeID) {
        DigitNode result = null;
        try {
            String query = "SELECT " + DIGIT_NODE_ID + ", " + VALUE + ", " + SUM + " FROM " + DIGIT_TREE + " WHERE " + DIGIT_NODE_ID + " = " + nodeID;
            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                result = new DigitNode();
                result.setID(set.getLong(DIGIT_NODE_ID));
                result.setValue(set.getInt(VALUE));
                result.setSum(set.getInt(SUM));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void updateNodeValues(DigitNode node) {
        try {
            String query = "UPDATE " + DIGIT_TREE + " SET " + VALUE + " = " + node.getValue() + ", " + SUM + " = " + node.getSum() + " WHERE " + DIGIT_NODE_ID + " = "
                    + node.getID();
            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateChildrenSums(List<Long> childrenIDs, int difference) {
        try {
            String idsString = prepareListOfIDs(childrenIDs);
            String query = "UPDATE " + DIGIT_TREE + " SET " + SUM + " = (" + SUM + " + " + difference + ") WHERE " + DIGIT_NODE_ID + " IN " + idsString;

            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeNodeAndChildren(List<Long> IDs) {
        try {
            String idsString = prepareListOfIDs(IDs);
            String query = "DELETE FROM " + DIGIT_TREE + " WHERE " + DIGIT_NODE_ID + " IN " + idsString;
            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void insertRelation(long parentID, long childID) {
        try {
            String query = "INSERT INTO " + RELATIONS + " (" + PARENT_ID + ", " + CHILD_ID + ") VALUES (" + parentID + ", " + childID + ")";
            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Long> getChildrenIDs(long parentID) {
        List<Long> result = null;
        try {
            String query = "SELECT " + CHILD_ID + " FROM " + RELATIONS + " WHERE " + PARENT_ID + " = " + parentID;
            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                result = new LinkedList<>();
                do {
                    result.add(set.getLong(CHILD_ID));
                }
                while (set.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public Long getParentID(long childID) {
        Long result = null;
        try {
            String query = "SELECT " + PARENT_ID + " FROM " + RELATIONS + " WHERE " + CHILD_ID + " = " + childID;
            insureConnection();

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                do {
                    result = set.getLong(PARENT_ID);
                }
                while (set.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String prepareListOfIDs(List<Long> IDs) {
        return IDs.toString().replace('[', '(').replace(']', ')');
    }
}
