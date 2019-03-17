import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class XmlDom {

    private static List<String> nodes = new ArrayList<>();
    private static Map<String, NodeDetails> nodesMap = new HashMap<>();


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File("C:\\Users\\Elitebook 8770w\\Downloads\\sample.xml"));
        Element documentElement = document.getDocumentElement();

        XmlDom.getAllNodePaths(documentElement.getParentNode().getFirstChild(), null);

        XmlDom.printAllNodes(documentElement.getParentNode().getFirstChild(), null);

        System.out.println("Done");
    }

    private static void getAllNodePaths(Node node, String rootPath) {
        if (node.getNodeType() != Node.TEXT_NODE) {
            NodeList nodeList = node.getChildNodes();
            String currentNodePath = getXPath(node, rootPath);
            if (!nodes.contains(currentNodePath)) {
                nodes.add(currentNodePath);
                NodeDetails nodeDetails = new NodeDetails(0, nodes.indexOf(currentNodePath), isItARealParentNode(node));
                nodesMap.put(currentNodePath, nodeDetails);
            }
            String format = "CurrentPath: %-30s Column: %s%n";
            System.out.printf(format, currentNodePath, nodes.indexOf(currentNodePath));
            if (isItARealParentNode(node)) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    if (nodeList.item(i).getNodeType() != Node.TEXT_NODE) {
                        getAllNodePaths(nodeList.item(i), currentNodePath);
                    }
                }
            }
        }
    }


    private static void printAllNodes(Node node, String rootPath) {
        String currentNodePath = null;
        if (node.getNodeType() != Node.TEXT_NODE) {
            currentNodePath = getXPath(node, rootPath);
            NodeDetails nodeDetails = nodesMap.get(currentNodePath);
            String format = "Node: %-30s Row: %d Column: %d CurrentPath: %s%n";
            System.out.printf(format, node.getNodeName(), nodeDetails.getRow(), nodeDetails.getColumn(), currentNodePath);
        }

        if (isItARealParentNode(node)) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node currentChildNode = node.getChildNodes().item(i);
                if (currentChildNode.getNodeType() != Node.TEXT_NODE) {
                    printAllNodes(currentChildNode, currentNodePath);
                }
            }
            incrementNodeRow(currentNodePath);
        }
    }

    private static void incrementNodeRow(String node){
        List<String> nodesToIncrement =  nodesMap.keySet().stream()
                                                          .filter(path -> path.contains(node))
                                                          .collect(Collectors.toList());
        Integer maxRow = 0;
        for (String nodeToIncrement : nodesToIncrement) {
            if(nodesMap.get(nodeToIncrement).getRow()> maxRow)
                maxRow = nodesMap.get(nodeToIncrement).getRow();
        }

        for (String nodeToIncrement : nodesToIncrement){
            nodesMap.get(nodeToIncrement).setRow(maxRow + 1);
        }
    }


    private static boolean isItARealParentNode(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes() && node.getChildNodes().getLength() > 1;
    }

    private static String getXPath(Node node, String rootPath) {
        StringBuilder stringBuilder = new StringBuilder();
        if (rootPath == null) {
            stringBuilder.append(node.getNodeName());
            return stringBuilder.toString();
        } else {
            stringBuilder.append(rootPath)
                    .append("-")
                    .append(node.getNodeName())
                    .append("|");
            return stringBuilder.toString();
        }
    }

}


class NodeDetails {
    private int column;
    private int row;
    private boolean isParent;

    public NodeDetails(int row, int column, boolean isParent) {
        this.column = column;
        this.row = row;
        this.isParent = isParent;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public void incrementRow(){
        this.row++;
    }

}