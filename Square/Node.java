package unalcol.agents.examples.games.squares.IA2018.LosAsperos;

import unalcol.agents.examples.games.squares.Squares;
import unalcol.agents.examples.labyrinth.generate.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Node {
    private HashMap<String, Coordinates> neighbors;
    private ArrayList<String> empitySides;

    public Node(){}

    public Node(HashMap<String, Coordinates> neighbors, ArrayList<String> empitySides) {
        this.neighbors = neighbors;
        this.empitySides = empitySides;
    }

    public Node(Node node){
        neighbors = node.getNeighbors();
        empitySides = node.getEmpitySides();
    }

    public HashMap<String, Coordinates> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(HashMap<String, Coordinates> neighbors) {
        this.neighbors = neighbors;
    }

    public ArrayList<String> getEmpitySides() {
        return empitySides;
    }

    public void setEmpitySides(ArrayList<String> empitySides) {
        this.empitySides = empitySides;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(neighbors, node.neighbors) &&
                Objects.equals(empitySides, node.empitySides);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neighbors, empitySides);
    }

    @Override
    protected Node clone() {
        HashMap<String, Coordinates> newNeighbors = new HashMap<>(neighbors);
        ArrayList<String> newEmpitySides = new ArrayList<>(empitySides);
        return new Node(newNeighbors, newEmpitySides);
    }

    @Override
    public String toString() {
        return "empitySides=" + empitySides;
    }
}
