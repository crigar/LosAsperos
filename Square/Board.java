package unalcol.agents.examples.games.squares.IA2018.LosAsperos;

import unalcol.agents.examples.games.squares.Squares;
import unalcol.agents.examples.labyrinth.generate.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Board {
    private HashMap<Coordinates, Node> boardTree;
    private ArrayList<Coordinates> empityNodes;
    private HashMap<String, Integer> scores;
    private Coordinates coordinatesToMove;
    private String sideToMove;
    private Board dad;
    private Board boardToMove;

    public Board(){}
    public Board(HashMap<Coordinates, Node> boardTree, ArrayList<Coordinates> empityNodes) {
        this.boardTree = boardTree;
        this.empityNodes = empityNodes;
        scores = new HashMap<>();
        coordinatesToMove = new Coordinates();
    }
    public Board(HashMap<Coordinates, Node> boardTree){
        this.boardTree = boardTree;
        scores = new HashMap<>();
        coordinatesToMove = new Coordinates();
    }

    public Board(Board board){
        this.boardTree = (HashMap<Coordinates, Node>) board.getBoardTree().clone();
        this.empityNodes = board.getEmpityNodes();
        scores = new HashMap<>();
        coordinatesToMove = new Coordinates();
    }

    public HashMap<Coordinates, Node> getBoardTree() {
        return boardTree;
    }

    public void setBoardTree(HashMap<Coordinates, Node> boardTree) {
        this.boardTree = boardTree;
    }

    public ArrayList<Coordinates> getEmpityNodes() {
        empityNodes = new ArrayList<>();
        for (Coordinates coor: boardTree.keySet()) {
            if (boardTree.get(coor).getEmpitySides().size() > 0)
                empityNodes.add(coor);
        }
        return empityNodes;
    }

    public Board getBoardToMove() {
        return boardToMove;
    }

    public void setBoardToMove(Board boardToMove) {
        this.boardToMove = boardToMove;
    }

    public void setEmpityNodes(ArrayList<Coordinates> empityNodes) {
        this.empityNodes = empityNodes;
    }

    public HashMap<String, Integer> getScores() {
        return scores;
    }

    public void setScores(HashMap<String, Integer> scores) {
        this.scores = scores;
    }

    public Coordinates getCoordinatesToMove() {
        return coordinatesToMove;
    }

    public void setCoordinatesToMove(Coordinates coordinatesToMove) {
        this.coordinatesToMove = coordinatesToMove;
    }

    public String getSideToMove() {
        return sideToMove;
    }

    public void setSideToMove(String sideToMove) {
        this.sideToMove = sideToMove;
    }

    public Board getDad() {
        return dad;
    }

    public void setDad(Board dad) {
        this.dad = dad;
    }
    // esta funcion elimina los lados de los nodos vecinos cuando uno coloca una raya,
    //ya que la raya afecta el lado de dos nodos a la vez
    public void removeNeighborSides(Node node){
        if (node.getEmpitySides().size() == 1){
            String side = node.getEmpitySides().get(0);
            Coordinates coorNeighbor = node.getNeighbors().get(side);
            Node neighborNode = boardTree.get(coorNeighbor);
            if (side.equals(Squares.TOP)){
                neighborNode.getEmpitySides().remove(Squares.BOTTOM);
                node.getEmpitySides().clear();
                removeNeighborSides(neighborNode);
            }
            if (side.equals(Squares.RIGHT)){
                neighborNode.getEmpitySides().remove(Squares.LEFT);
                node.getEmpitySides().clear();
                removeNeighborSides(neighborNode);
            }
            if (side.equals(Squares.BOTTOM)){
                neighborNode.getEmpitySides().remove(Squares.TOP);
                node.getEmpitySides().clear();
                removeNeighborSides(neighborNode);
            }
            if (side.equals(Squares.LEFT)){
                neighborNode.getEmpitySides().remove(Squares.RIGHT);
                node.getEmpitySides().clear();
                removeNeighborSides(neighborNode);
            }

        }else{
            return;
        }

    }
    //remueve la raya de un nodo dado, recordemos que los nodos guardan los lados vacios, donde no hay rayas
    public void removeSide(Coordinates coor, String side, HashMap<Coordinates, HashMap<String, Boolean>> sidesSeen){
        Node node = boardTree.get(coor);
        Coordinates neighborCoor = node.getNeighbors().get(side);
        Node neighborNode = boardTree.get(neighborCoor);
        HashMap<String, Boolean> sides = new HashMap<>();
        if (side.equals(Squares.TOP)){
            if (neighborNode.getEmpitySides().size() > 0) neighborNode.getEmpitySides().remove(Squares.BOTTOM);
            if (neighborNode.getEmpitySides().size() == 1) removeNeighborSides(neighborNode);
            sides.put(Squares.BOTTOM, true);
            sidesSeen.put(new Coordinates(coor.getRow() - 1, coor.getCol()), sides );
        }
        if (side.equals(Squares.RIGHT)){
            if (neighborNode.getEmpitySides().size() > 0) neighborNode.getEmpitySides().remove(Squares.LEFT);
            if (neighborNode.getEmpitySides().size() == 1) removeNeighborSides(neighborNode);
            sides.put(Squares.LEFT, true);
            sidesSeen.put(new Coordinates(coor.getRow(), coor.getCol() + 1), sides );

        }
        if (side.equals(Squares.BOTTOM)){
            if (neighborNode.getEmpitySides().size() > 0)neighborNode.getEmpitySides().remove(Squares.TOP);
            if (neighborNode.getEmpitySides().size() == 1) removeNeighborSides(neighborNode);
            sides.put(Squares.TOP, true);
            sidesSeen.put(new Coordinates(coor.getRow() + 1, coor.getCol()), sides );
        }
        if (side.equals(Squares.LEFT)){
            if (neighborNode.getEmpitySides().size() > 0) neighborNode.getEmpitySides().remove(Squares.RIGHT);
            if (neighborNode.getEmpitySides().size() == 1) removeNeighborSides(neighborNode);
            sides.put(Squares.RIGHT, true);
            sidesSeen.put(new Coordinates(coor.getRow() , coor.getCol() - 1), sides );
        }
        node.getEmpitySides().remove(side);
        if (node.getEmpitySides().size() == 1) removeNeighborSides(node);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(boardTree, board.boardTree) &&
                Objects.equals(empityNodes, board.empityNodes) &&
                Objects.equals(coordinatesToMove, board.coordinatesToMove) &&
                Objects.equals(sideToMove, board.sideToMove)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardTree, empityNodes);
    }


    //clona todo el tablero
    @Override
    protected Board clone()  {
        HashMap<Coordinates, Node> newBoardTree = new HashMap<>();
        for (Coordinates coor: boardTree.keySet()) {
            Node newNode = boardTree.get(coor).clone();
            newBoardTree.put(coor, newNode);
        }
        Board newBoard = new Board(newBoardTree);
        return newBoard;
    }

    //clona el tablero unicamente teniendo en cuenta los nodos vacios los demas nodos los ignora, esto se hace
    //para el minMax, para no clonar informacion inecesaria
    public Board cloneToTree(){
        HashMap<Coordinates, Node> newBoardTree = new HashMap<>();
        for (Coordinates coor: boardTree.keySet()) {
            Node newNode = boardTree.get(coor);
            if (newNode.getEmpitySides().size() > 0){
                newBoardTree.put(coor, newNode.clone());
            }else{
                if (newNode.getNeighbors().size() > 0){
                    newBoardTree.put(coor, newNode.clone());
                }
            }
        }
        Board newBoard = new Board(newBoardTree);
        return newBoard;
    }
}
