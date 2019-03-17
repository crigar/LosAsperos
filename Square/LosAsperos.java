package unalcol.agents.examples.games.squares.IA2018.LosAsperos;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.squares.Squares;
import unalcol.agents.examples.labyrinth.generate.Coordinate;
import unalcol.types.collection.vector.Vector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author Cristian Garcia y Daniel Caita
 */
public class LosAsperos implements AgentProgram {
    protected String color;

    private Percept p;
    private int boardSize;
    private Board currentBoard;
    private HashMap<Board, HashMap<Board, HashMap<String, Integer> >> minMaxTree = new HashMap<>();
    private int numNodesEmptyToMinMax = 25;

    public LosAsperos( String color ){
        this.color = color;
    }

    public void createMinMaxTree(Board root, String turn){
        //es una funcion recursiva, empieza con el tablero actualizado despues de cada turno del oponente
        if ( root == null ){
            root = currentBoard;
            turn = "max";
            root.getScores().put("max", 0);
            root.getScores().put("min", 0);
        }else{
            if (turn == "max") turn = "min";
            else turn = "max";
        }
        if (root.getEmpityNodes().size() == 0) return;

        //genera las posibles jugadas (hijos) desde el tablero actual
        //y las posibles jugadas de sus hijos hasta que el tablero haya sido llenado
        //se poda el arbol, se hace el calculo minMax, y va subiendo los resultados para hacer la mejor jugada posible

        HashMap<Board, HashMap<String, Integer> > children = new HashMap<>();
        HashMap<Coordinates, HashMap<String, Boolean>> sidesSeen = new HashMap<>();
        for (Coordinates coor: root.getEmpityNodes()) {
            for (String side: root.getBoardTree().get(coor).getEmpitySides()) {
                boolean flag = false;
                boolean flag1 = false;
                String newTurn = (turn == "max")? "min":"max";

                if (root.getDad() == null) flag1 = true;
                else{
                    if (root.getDad().getBoardToMove() == null) flag1 = true;
                    else{
                        if (root.getBoardToMove() == null) flag1 = true;
                        else{
                            if (turn == "max"){
                                if (root.getBoardToMove().getScores().get(newTurn) < root.getDad().getBoardToMove().getScores().get(turn)){
                                    flag1 = true;
                                }
                            }else{
                                if (root.getBoardToMove().getScores().get(newTurn) > root.getDad().getBoardToMove().getScores().get(turn)){
                                    flag1 = true;
                                }
                            }
                        }
                    }
                }

                if (sidesSeen.get(coor) == null) flag = true;
                else{
                    if (sidesSeen.get(coor).get(side) == null)flag = false;
                }

                if (flag && flag1){
                    Board newBoard = root.clone();

                    newBoard.removeSide(coor, side, sidesSeen);
                    newBoard.getScores().put("max", root.getScores().get("max"));
                    newBoard.getScores().put("min", root.getScores().get("min"));
                    int currentScorePlayer = newBoard.getScores().get(newTurn) + ( root.getEmpityNodes().size() - newBoard.getEmpityNodes().size() );
                    newBoard.getScores().put(newTurn, currentScorePlayer);
                    newBoard.setCoordinatesToMove(coor);
                    newBoard.setSideToMove(side);
                    newBoard.setDad(root);

                    root.setBoardToMove(newBoard);
                    //children.put(newBoard, newBoard.getScores());

                    if (turn == "max"){
                        if (newBoard.getScores().get("min") > root.getBoardToMove().getScores().get("min")){
                            root.setBoardToMove(newBoard);
                        }
                    }else{
                        if (newBoard.getScores().get("max") < root.getBoardToMove().getScores().get("min")){
                            root.setBoardToMove(newBoard);
                        }
                    }

                    createMinMaxTree(newBoard, turn);

                }
            }
        }
        //minMaxTree.put(root,children);
        if (root.getDad() != null) root.getDad().setBoardToMove(root.getBoardToMove());
    }

    public String minMax(){
        //crea el minMax, y devuelve el movimiento que deberia hacer en el tablero actual
        createMinMaxTree(null, null);
        return currentBoard.getBoardToMove().getCoordinatesToMove().getRow()+":"+
                currentBoard.getBoardToMove().getCoordinatesToMove().getCol()+":"+
                currentBoard.getBoardToMove().getSideToMove();
    }

    public String initialMoves(){
        //recorre los nodos del tablero actual, para colocar una raya en donde no le deje cuadros al oponente
        for (Coordinates coor: currentBoard.getEmpityNodes()) {
            Node node = currentBoard.getBoardTree().get(coor);
            if (node.getEmpitySides().size() > 2){
                Collections.shuffle(node.getEmpitySides());
                for (String side: node.getEmpitySides()) {
                    Coordinates neighborCoor = node.getNeighbors().get(side);
                    Node neighbor = currentBoard.getBoardTree().get(neighborCoor);
                    if (neighbor.getEmpitySides().size() > 2){
                        return coor.getRow()+":"+
                                coor.getCol()+":"+
                                side;
                    }

                }
            }
        }

        return "";
    }

    public String move(){
        String codeToMove = "";
        //si la cantidad de nodos vacios es mayor al valor en el que se deberia empezar a hacer minMax se llama la funcion initialMoves()
        if (currentBoard.getEmpityNodes().size() > numNodesEmptyToMinMax){
            codeToMove = initialMoves();
        }
        //cuando no haya mas rayas que poner sin dejar cuadro al oponente
        if (codeToMove.equals("") ){
            //si la cantidad de nodos es apta para hacer minMax se hace minMax
            if (currentBoard.getEmpityNodes().size() <= numNodesEmptyToMinMax){
                codeToMove = minMax();
            }else{ //de lo contrario ponemos aleatoriamente una raya en el tablero hasta que se pueda hacer minMax
                int randomIndexNode = (int) (Math.random() * (currentBoard.getEmpityNodes().size() - 1));
                Coordinates coor = currentBoard.getEmpityNodes().get(randomIndexNode);
                Node node = currentBoard.getBoardTree().get(coor);
                int randomIndexSide = (int) (Math.random() * (node.getEmpitySides().size() - 1));
                codeToMove = coor.getRow()+":"+ coor.getCol()+":"+ node.getEmpitySides().get(randomIndexSide);
            }
        }
        return codeToMove;
    }

    public ArrayList<String> getEmpitySides(int i, int j){
        //obtiene los lados donde no se ha puesto rayas en una celda (nodo)
        ArrayList<String> empitySides = new ArrayList();
        HashMap<String, Coordinates> neighbors = new HashMap<>();
        if(((String)p.getAttribute(i+":"+j+":"+Squares.LEFT)).equals(Squares.FALSE))
            empitySides.add(Squares.LEFT);
        if(((String)p.getAttribute(i+":"+j+":"+Squares.TOP)).equals(Squares.FALSE))
            empitySides.add(Squares.TOP);
        if(((String)p.getAttribute(i+":"+j+":"+Squares.BOTTOM)).equals(Squares.FALSE))
            empitySides.add(Squares.BOTTOM);
        if(((String)p.getAttribute(i+":"+j+":"+Squares.RIGHT)).equals(Squares.FALSE))
            empitySides.add(Squares.RIGHT);

        return empitySides;
    }

    public HashMap<String, Coordinates> getNeighbors(int i, int j){
        //devuelve los vecinos de un nodo en unas coordenadas dadas, String : top, bottom, right, left
        HashMap<String, Coordinates> neighbors = new HashMap<>();
        if (i - 1 >= 0 ) neighbors.put(Squares.TOP, new Coordinates(i - 1, j));
        if (i + 1 < boardSize ) neighbors.put(Squares.BOTTOM, new Coordinates(i + 1, j));
        if (j - 1 >= 0 ) neighbors.put(Squares.LEFT, new Coordinates(i , j - 1 ));
        if (j + 1 < boardSize ) neighbors.put(Squares.RIGHT, new Coordinates(i , j + 1));
        return neighbors;
    }

    public void updateCurrentBoard(){
        //actualizamos los nodos, los nodos vecinos de cada nodo y las rayas puestas actualmente en cada nodo
        if (currentBoard == null){
            currentBoard = new Board(new HashMap());
        }
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Node node = currentBoard.getBoardTree().get(new Coordinates(i,j));
                if (node == null) node = new Node();
                node.setEmpitySides(getEmpitySides(i,j));
                node.setNeighbors(getNeighbors(i,j));
                currentBoard.getBoardTree().put(new Coordinates(i,j), node);
            }
        }
    }

    @Override
    public Action compute(Percept p) {
        long time = (long)(200 * Math.random());
        this.p = p;
        try{
            Thread.sleep(time);
        }catch(Exception e){}
        if( p.getAttribute(Squares.TURN).equals(color) ){
            //guardamos el tamaño actual del tablero
            boardSize = Integer.parseInt((String)p.getAttribute(Squares.SIZE));
            //cada vez que es nuestro turno actualizamos el tablero actual
            updateCurrentBoard();
            //pedimos que movimiento hacer a la funcion move()
            return new Action( move() );
        }
        return new Action(Squares.PASS);
    }

    @Override
    public void init() {
    }

}

