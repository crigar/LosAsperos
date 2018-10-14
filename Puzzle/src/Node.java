import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Node {

    private int[][] state;
    private Node parent;
    private LinkedList<Node> children;
    private int depth;
    private int[] ceroPos;
    private int cost;


    public Node(int[][] state, Node parent, LinkedList<Node> children, int depth, int[] ceroPos ){
        this.state = state;
        this.parent = parent;
        this.children = children;
        this.depth = depth;
        this.ceroPos = ceroPos;
    };

    public int[] getCeroPos() {
        return ceroPos;
    }

    public void setCeroPos(int[] ceroPos) {
        this.ceroPos = ceroPos;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int[][] getState() {
        return state;
    }

    public void setState(int[][] state) {
        this.state = state;
    }

    public Node getParent() {
        return parent;
    }

    public LinkedList<Node> getChildren() {
        return children;
    }

    public int getDepth() {
        return depth;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setChildren(LinkedList<Node> children) {
        this.children = children;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }


    public boolean isSame(Node node){
        for (int i = 0; i < state[0].length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] != node.state[i][j] ){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        for (int i = 0; i < state[0].length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] != node.state[i][j] ){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    @Override
    public String toString() {
        String toString = "";
        for (int i = 0; i < this.state.length; i++) {
            for (int j = 0; j < this.state.length; j++) {

                toString = toString + this.state[i][j];
            }
            toString = toString + '\n';
        }
        return toString;
    }
}
