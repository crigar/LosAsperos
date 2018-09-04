package unalcol.agents.examples.labyrinth.teseo.IA2018.LosAsperos;

import unalcol.agents.simulate.util.SimpleLanguage;

import java.util.*;

public class Node {

    private int x;
    private int y;
    private Node dad;
    private List<Node> children;
    private int typeChild;

    public Node(){};
    public Node(int x, int y, Node dad, List<Node> children) {
        this.x = x;
        this.y = y;
        this.dad = dad;
        this.children = children;
    }

    public Node(int x, int y, Node dad, int typeChild) {
        this.x = x;
        this.y = y;
        this.dad = dad;
        this.typeChild = typeChild;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Node getDad() {
        return dad;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDad(Node dad) {
        this.dad = dad;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public int getTypeChild() {
        return typeChild;
    }

    public void setTypeChild(int typeChild) {
        this.typeChild = typeChild;
    }

    @Override
    public boolean equals(Object obj) {
        if (((Node)obj).getX() == x && ((Node)obj).getY() == y){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(x: " + x + " , y: " + y + " )";
    }
}
