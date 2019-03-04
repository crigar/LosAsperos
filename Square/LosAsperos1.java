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
import java.util.HashMap;

/**
 *
 * @author Jonatan
 */
public class LosAsperos1 implements AgentProgram {
    protected String color;

    private Percept p;
    private int boardSize;
    private Board currentBoard;
    private HashMap<Board, HashMap<Board, HashMap<String, Integer> >> minMaxTree = new HashMap<>();
    private int currentMax;
    private int currentMin;

    public LosAsperos1( String color ){
        this.color = color;
    }

    public void createMinMaxTree(Board root, String turn){
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
        createMinMaxTree(null, null);
        return currentBoard.getBoardToMove().getCoordinatesToMove().getRow()+":"+
                currentBoard.getBoardToMove().getCoordinatesToMove().getCol()+":"+
                currentBoard.getBoardToMove().getSideToMove();
    }

    public String initialMoves(){
        for (Coordinates coor: currentBoard.getEmpityNodes()) {
            Node node = currentBoard.getBoardTree().get(coor);
            if (node.getEmpitySides().size() > 2){
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
        if (currentBoard.getEmpityNodes().size() > 30){
            codeToMove = initialMoves();
        }

        if (codeToMove.equals("") ){
            if (currentBoard.getEmpityNodes().size() <= 30){
                codeToMove = minMax();
            }else{
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
        HashMap<String, Coordinates> neighbors = new HashMap<>();
        if (i - 1 >= 0 ) neighbors.put(Squares.TOP, new Coordinates(i - 1, j));
        if (i + 1 < boardSize ) neighbors.put(Squares.BOTTOM, new Coordinates(i + 1, j));
        if (j - 1 >= 0 ) neighbors.put(Squares.LEFT, new Coordinates(i , j - 1 ));
        if (j + 1 < boardSize ) neighbors.put(Squares.RIGHT, new Coordinates(i , j + 1));
        return neighbors;
    }

    public void updateCurrentBoard(){
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
            boardSize = Integer.parseInt((String)p.getAttribute(Squares.SIZE));
            updateCurrentBoard();
//            int i = 0;
//            int j = 0;
//            Vector<String> v = new Vector<String>();
//            while(v.size()==0){
//                i = (int)(size*Math.random());
//                j = (int)(size*Math.random());
//                if(((String)p.getAttribute(i+":"+j+":"+Squares.LEFT)).equals(Squares.FALSE))
//                    v.add(Squares.LEFT);
//                if(((String)p.getAttribute(i+":"+j+":"+Squares.TOP)).equals(Squares.FALSE))
//                    v.add(Squares.TOP);
//                if(((String)p.getAttribute(i+":"+j+":"+Squares.BOTTOM)).equals(Squares.FALSE))
//                    v.add(Squares.BOTTOM);
//                if(((String)p.getAttribute(i+":"+j+":"+Squares.RIGHT)).equals(Squares.FALSE))
//                    v.add(Squares.RIGHT);
//            }
            return new Action( move() );
        }
        return new Action(Squares.PASS);
    }

    @Override
    public void init() {
    }

}

