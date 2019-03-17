# DigitsTree
## User manual

* Sum of all parents in a way from root node to currently selected node shown in top right corner (only if any node has been selected).
* If none node is currently selected all buttons will be disabled and the sum hidden.
* You can change language selecting it to the left from "Sum of parents" label.
* When you select any node on the tree view you will be able to perform one of three actions now:
  1.	Create a child node from currently selected node (“Create Node” button).
  2.	Update currently selected node value. This will also update every child “sum of parents” value till the leaves (“Update Value” button).
  3.	Delete selected node and all its children if any exist. The exception is the root node, you can’t delete it. (“Delete Node” button).

![DigitsTree_01](https://user-images.githubusercontent.com/48453450/54496462-d89c0d00-48ef-11e9-9b29-37e2fc9cf9af.png)

## Programmers manual

To run the project, you can use a command from the folder of the project. Go to the folder where you save keep the project, for example. “**D:\Homework\DigitsTree**”. Open a command line here and type the following command: “**mvn clean package**”, than you can type the second command to run the project: “**java -jar target\digit-tree-1.0.jar**”. You also can combine these two commands in one: “**mvn clean package && java -jar target\digit-tree-1.0.jar**”. Or after executing the first one you can go into “target” folder and launch generated “digit-tree-1.0.jar” by double clicking it.
After the project will be executed you can manage the database from your browser. Just open any browser you use and insert http://localhost:8080/h2-console/. Then you must fill all the fields according to the screenshot below.

![DigitsTree_02](https://user-images.githubusercontent.com/48453450/54496478-1731c780-48f0-11e9-81df-aaf32a48a57c.png)
