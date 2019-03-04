package unalcol.agents.examples.labyrinth.teseo.IA2018.LosAsperos;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.agents.Action;
import unalcol.types.collection.vector.Vector;
import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Cristian Garcia - Daniel Caita
 * @version 1.0
 */
public class LosAsperos implements AgentProgram{
    protected SimpleLanguage language;
    protected Vector<String> cmd = new Vector<String>();

    private HashMap<Node, Integer> nodesSeen;
    private int orientation;
    private Node currentNode;
    private HashMap<Integer, HashMap<Integer, Integer> > orientationTranslator;
    private HashMap<Node, HashMap<Node, Integer> > tree;
    private Deque< Node > cells;
    private Queue< Node > path;

    public LosAsperos() {
        nodesSeen = new HashMap<>();
        nodesSeen.put(new Node(0,0,null,null), 1);

        orientation = 0;
        currentNode = new Node(0,0, null,null);
        orientationTranslator = new HashMap<>();
        tree = new HashMap<>();
        cells = new LinkedList<>();
        path = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            HashMap<Integer, Integer> couples = new HashMap<>();
            if (i == 0){
                couples.put(0, 0);
                couples.put(1, 1);
                couples.put(2, 2);
                couples.put(3, 3);
            }
            if (i == 1){
                couples.put(0, 3);
                couples.put(1, 0);
                couples.put(2, 1);
                couples.put(3, 2);
            }
            if (i == 2){
                couples.put(0, 2);
                couples.put(1, 3);
                couples.put(2, 0);
                couples.put(3, 1);
            }
            if (i == 3){
                couples.put(0, 1);
                couples.put(1, 2);
                couples.put(2, 3);
                couples.put(3, 0);
            }
            orientationTranslator.put(i, couples);
        }
    }
    public LosAsperos(SimpleLanguage _language  ) {
        language = _language;
    }
    public void setLanguage(  SimpleLanguage _language ){
        language = _language;
    }
    public void init(){
        cmd.clear();
    }

    private void getChildrenAndAdjacentNodes(boolean[] percepts){
        List<Node> children = new LinkedList<>(); //nodos adyacentes al nodo actual que no sean hijos de otros nodos
        HashMap<Node, Integer> adjacentNodes = new HashMap<>(); //nodos adyacentes al nodo actual sin importar que sean hijos de otros nodos
        Node nodeUp = new Node(currentNode.getX(), currentNode.getY() + 1, currentNode, 0);
        Node nodeRight = new Node(currentNode.getX() + 1, currentNode.getY(), currentNode, 1);
        Node nodeDown = new Node(currentNode.getX(), currentNode.getY() - 1, currentNode, 2);
        Node nodeLeft = new Node(currentNode.getX() - 1 , currentNode.getY(), currentNode, 3);

        Node[] nodesPosibles = new Node[4];
        nodesPosibles[0] = nodeUp;
        nodesPosibles[1] = nodeRight;
        nodesPosibles[2] = nodeDown;
        nodesPosibles[3] = nodeLeft;

        for (int j = 0; j < 4; j++) {
            int cardinalCoordinate = (orientation + j) % 4;
            Node node = nodesPosibles[ cardinalCoordinate ];
            if (!percepts[j]){ // si para ir a esa casilla no hay pared
                if (!nodesSeen.containsKey(node) || !tree.containsKey(node)){//Si no hemos visto ese nodo o si no lo hemos visitad
                    nodesSeen.put(node,1);//sera un nodo visto
                    children.add(node);//sera un nodo hijo
                }
                adjacentNodes.put(node,1);//sera un nodo adyacente
            }
        }
        currentNode.setChildren( children  );//nuevos hijos del nodo actual
        tree.put(currentNode, adjacentNodes);//nuevos nodos adyacentes

    }
    private int getCardinalityFromCurrentNode(Node nextMove){
        int cardinality = -1;
        if (currentNode.getX() == nextMove.getX() && currentNode.getY() + 1 == nextMove.getY()) cardinality = 0;
        if (currentNode.getX() + 1 == nextMove.getX() && currentNode.getY() == nextMove.getY()) cardinality = 1;
        if (currentNode.getX() == nextMove.getX() && currentNode.getY() - 1 == nextMove.getY()) cardinality = 2;
        if (currentNode.getX() - 1 == nextMove.getX() && currentNode.getY()  == nextMove.getY()) cardinality = 3;
        return cardinality;
    }
    private void getPath(Node cellGoal){
        //algoritmo de busqueda en amplitud
        HashMap<Node, Integer> explored = new HashMap<>();
        Queue< ArrayDeque<Node> > queue = new LinkedList<>();
        ArrayDeque<Node> partialPath = new ArrayDeque<>();
        partialPath.add(currentNode);
        queue.add(partialPath);

        while (queue.size() > 0){
            ArrayDeque<Node> actualPath = queue.remove();
            Node focus = actualPath.getLast();
            for (Map.Entry<Node, Integer> element : tree.get(focus).entrySet()) {
                if (element.getKey().equals(cellGoal)){
                    actualPath.add(element.getKey());
                    actualPath.removeFirst();
                    path = actualPath;
                    return;
                }else{
                    if (!explored.containsKey(element.getKey())){
                        ArrayDeque<Node> newPath = actualPath.clone();
                        if (tree.containsKey(element.getKey()))newPath.add(element.getKey());
                        queue.add(newPath);
                    }
                }
            };
            explored.put(focus, 1);
        }

    }
    private void testEnclosedBox(){
        boolean cellTest1 = false;
        boolean cellTest2 = false;
        boolean cellTest3 = false;
        Node next = new Node();

        next = new Node(currentNode.getX(), currentNode.getY() - 1);
        cellTest1 = tree.containsKey(new Node(currentNode.getX() + 1, currentNode.getY() - 1));
        cellTest2 = tree.containsKey(new Node(currentNode.getX(), currentNode.getY() - 2));
        cellTest3 = tree.containsKey(new Node(currentNode.getX() - 1, currentNode.getY() - 1));
        if (cellTest1 && cellTest2 && cellTest3 && nodesSeen.containsKey(next)){
            cells.add(next);
        }
        next = new Node(currentNode.getX() - 1, currentNode.getY());
        cellTest1 = tree.containsKey(new Node(currentNode.getX() - 1, currentNode.getY() - 1));
        cellTest2 = tree.containsKey(new Node(currentNode.getX() - 2, currentNode.getY() ));
        cellTest3 = tree.containsKey(new Node(currentNode.getX() - 1, currentNode.getY() + 1));
        if (cellTest1 && cellTest2 && cellTest3 && nodesSeen.containsKey(next)){
            cells.add(next);
        }
        next = new Node(currentNode.getX(), currentNode.getY() + 1);
        cellTest1 = tree.containsKey(new Node(currentNode.getX() - 1, currentNode.getY() + 1));
        cellTest2 = tree.containsKey(new Node(currentNode.getX() , currentNode.getY() + 2));
        cellTest3 = tree.containsKey(new Node(currentNode.getX() + 1, currentNode.getY() + 1));
        if (cellTest1 && cellTest2 && cellTest3 && nodesSeen.containsKey(next)){
            cells.add(next);
        }
        next = new Node(currentNode.getX() + 1, currentNode.getY());
        cellTest1 = tree.containsKey(new Node(currentNode.getX() + 1, currentNode.getY() + 1));
        cellTest2 = tree.containsKey(new Node(currentNode.getX() + 2, currentNode.getY() ));
        cellTest3 = tree.containsKey(new Node(currentNode.getX() + 1, currentNode.getY() - 1));
        if (cellTest1 && cellTest2 && cellTest3 && nodesSeen.containsKey(next) ){
            cells.add(next);
        }
    }
    private int getNumRotations(){
        int numRotations = -1;
        if (path.size() == 0){
            testEnclosedBox();
            //obtiene la sigiente celda objetivo mientras no la hayamos visitado
            Node cellGoal = cells.removeLast();
            while (tree.containsKey(cellGoal)) {
                cellGoal = cells.removeLast();
            }
            getPath(cellGoal);//obtiene el camino mas corto desde el nodo actual hasta la celda objetivo
        }
        //de acuerdo al camino encontrado retorna las rotaciones de casilla en casilla hasta que haya llegado a la celda objetivo
        Node nextMove = path.remove();
        int cardinality;
        if (currentNode.equals(nextMove.getDad()))cardinality = nextMove.getTypeChild();
        else cardinality = getCardinalityFromCurrentNode(nextMove);
        numRotations = orientationTranslator.get(orientation).get(cardinality);

        orientation = (orientation + numRotations) % 4;
        currentNode = nextMove;
        return numRotations;
    }
    private void addGoals(){
        //esta funcion agrega a la pila "cells" los hijos de acuerdo a su prioridad
        //prioridad: hijo que menos rotaciones requiera
        HashMap<Integer, Node> prioriti = new HashMap<>();
        for (Node node : currentNode.getChildren()) {
            int rotations = orientationTranslator.get(orientation).get(node.getTypeChild());
            prioriti.put(rotations, node);
        }
        for (int i = 3; i >= 0; i--) {
            if (prioriti.containsKey(i)) cells.add(prioriti.get(i));

        }
    }
    private int accion(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL) {
        //En caso de que encuentre la salida
        if (MT)return -1;
        boolean[] percepts = new boolean[6];
        percepts[0] = PF;
        percepts[1] = PD;
        percepts[2] = PA;
        percepts[3] = PI;
        percepts[4] = MT;
        percepts[5] = FAIL;
        //obtiene y agrega los nodos hijos del nodo acutal,
        // tambien crea el arbol con los nodos adyacentes (para busqueda en amplitud posterior)
        getChildrenAndAdjacentNodes(percepts);
        //agrega a la pila: "cells" las casillas que debe recorrer
        addGoals();
        //obtiene el numero de rotaciones necesarias para ir a la siguiente casilla
        return getNumRotations();
    }

    public Action compute(Percept p){
        if( cmd.size() == 0 ){
            boolean PF   = (boolean) p.getAttribute("front");
            boolean PD   = (boolean) p.getAttribute("right");
            boolean PA   = (boolean) p.getAttribute("back");
            boolean PI   = (boolean) p.getAttribute("left");
            boolean MT   = (boolean) p.getAttribute("treasure");
            boolean FAIL = (boolean) p.getAttribute("fail");

            int d = accion(PF, PD, PA, PI, MT, FAIL);
            if (0 <= d && d < 4) {
                for (int i = 1; i <= d; i++) {
                    cmd.add("rotate"); //rotate
                }
                cmd.add("advance"); // advance
            }
            else {
                cmd.add("die"); // die
            }
        }
        String x = cmd.get(0);
        cmd.remove(0);
        return new Action(x);
    }
    public boolean goalAchieved( Percept p ){
        return (boolean) p.getAttribute("treasure");
    }
}
