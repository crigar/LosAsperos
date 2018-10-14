import  java.util.*;

public class Main {
    private static HashMap<Node, Integer> added = new HashMap();
    private static int searchedNoes = 1;
    private static int generatedNodes = 1;
    private static int lastDepth = 0;

    public static int[][] createPuzzle(int sizePuzzle){
        int[][] puzzle = new int[sizePuzzle][sizePuzzle];
        int count = 1;
        for (int i = 0; i < sizePuzzle; i++) {
            for (int j = 0; j < sizePuzzle; j++) {
                puzzle[i][j] = count % ((sizePuzzle * sizePuzzle));
                count ++;
            }
        }
        return puzzle;
    }
    public static void mess(Node root, int numMoves){
        int[][] puzzleMess = root.getState();
        int[] posCero = {2,2};
        for (int i = 0; i < numMoves; i++) {
            boolean validPos = false;
            int[] nextPos = new int[2];
            while (!validPos){
                Random r = new Random();
                int nextMove = r.nextInt(4-0) + 0;
                nextPos[0] = posCero[0] - 1;
                nextPos[1] = posCero[1];
                if (nextMove == 0){
                    nextPos[0] = posCero[0];
                    nextPos[1] = posCero[1] - 1;
                }
                if (nextMove == 1){
                    nextPos[0] = posCero[0] + 1;
                    nextPos[1] = posCero[1];
                }
                if (nextMove == 2){
                    nextPos[0] = posCero[0];
                    nextPos[1] = posCero[1] + 1;
                }
                if (nextPos[0] >= 0 && nextPos[0] < root.getState().length && nextPos[1] >= 0 && nextPos[1] < root.getState().length){
                    validPos = true;
                }
            }
            puzzleMess[posCero[0]][posCero[1]] = puzzleMess[nextPos[0]][nextPos[1]];
            puzzleMess[nextPos[0]][nextPos[1]] = 0;
            posCero[0] = nextPos[0];
            posCero[1] = nextPos[1];
        }
        root.setState(puzzleMess);
        root.setCeroPos(posCero);
    }

    public static HashMap<Integer, int[] > goalPos(int sizePuzzle){
        int num = 1;
        HashMap<Integer, int[] > goalPos = new HashMap<>();
        for (int i = 0; i < sizePuzzle; i++) {
            for (int j = 0; j < sizePuzzle; j++) {
                int[] pos = {i,j};
                goalPos.put(num % (sizePuzzle * sizePuzzle), pos);
                num++;
            }
        }
        return goalPos;
    }
    public static int findMoves(int[] currentPos, int[] goalPos){
        return Math.abs(currentPos[0] - goalPos[0]) + Math.abs(currentPos[1] - goalPos[1]);
    }
    public static int getCost(Node node , HashMap<Integer, int[]> goalPos,  int heuristic){
        int cost = 0;
        int totalPos = (int) (Math.pow(node.getState()[0].length, 2) );
        int[][] currentState =  node.getState();
        if (heuristic == 0){ // Manhatan
            for (int num = 1; num < totalPos; num++) {
                search:
                for (int i = 0; i < node.getState()[0].length; i++) {
                    for (int j = 0; j < node.getState()[0].length; j++) {
                        if (currentState[i][j] == num){
                            int[] current = {i,j};
                            int[] goal = goalPos.get(num);
                            cost = cost + findMoves(current, goal);
                            break search;
                        }
                    }
                }
            }
        }
        if (heuristic == 1){
            for (int i = 0; i < node.getState()[0].length; i++) {
                for (int j = 0; j < node.getState()[0].length; j++) {
                    if (goalPos.get(currentState[i][j])[0] != i || goalPos.get(currentState[i][j])[1] != j){
                        cost ++;
                    }
                }
            }
        }

        return cost;
    }
    public static int[][] copyState(int[][] state){
        int[][] copyState = new int[state.length][state.length];
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                copyState[i][j] = state[i][j];
            }
        }
        return copyState;
    }
    public static boolean isAdded(Node node){
        for (Map.Entry<Node, Integer> element : added.entrySet()) {
            if (node.isSame(element.getKey())) return true;
        }
        return false;
    }
    public static LinkedList<Node> getChildren(Node node, HashMap<Integer, int[]> goalPos,  int heuristic){
        LinkedList<Node> children  = new LinkedList<>();
        int[] posCero = node.getCeroPos();
        if (posCero[0] - 1 >= 0 ){
            int[][] state = copyState(node.getState());
            state[posCero[0]][posCero[1]] = state[posCero[0] - 1][posCero[1]];
            state[posCero[0] - 1][posCero[1]] = 0;
            int[] newPosCero = {posCero[0] - 1, posCero[1]};
            Node child = new Node(state, node, null, node.getDepth() + 1, newPosCero);
            child.setCost(getCost(child, goalPos, heuristic ));
            if (!isAdded(child)) {
                children.add(child);
                added.put(child, 1);
            }

        }
        if (posCero[1] + 1 < node.getState()[0].length ){
            int[][] state = copyState(node.getState());
            state[posCero[0]][posCero[1]] = state[posCero[0]][posCero[1] + 1];
            state[posCero[0]][posCero[1] + 1] = 0;
            int[] newPosCero = {posCero[0], posCero[1] + 1};
            Node child = new Node(state, node, null, node.getDepth() + 1, newPosCero );
            child.setCost(getCost(child, goalPos, heuristic ));
            if (!isAdded(child)) {
                children.add(child);
                added.put(child, 1);
            }
        }
        if (posCero[0] + 1 < node.getState()[0].length){
            int[][] state = copyState(node.getState());
            state[posCero[0]][posCero[1]] = state[posCero[0] + 1][posCero[1]];
            state[posCero[0] + 1][posCero[1]] = 0;
            int[] newPosCero = {posCero[0] + 1, posCero[1]};
            Node child = new Node(state, node, null, node.getDepth() + 1, newPosCero );
            child.setCost(getCost(child, goalPos, heuristic ));
            if (!isAdded(child)) {
                children.add(child);
                added.put(child, 1);
            }
        }
        if (posCero[1] - 1 >= 0 ){
            int[][] state = copyState(node.getState());
            state[posCero[0]][posCero[1]] = state[posCero[0]][posCero[1] - 1];
            state[posCero[0]][posCero[1] - 1] = 0;
            int[] newPosCero = {posCero[0], posCero[1] - 1};
            Node child = new Node(state, node, null, node.getDepth() + 1, newPosCero );
            child.setCost(getCost(child, goalPos, heuristic ));
            if (!isAdded(child)) {
                children.add(child);
                added.put(child, 1);
            }
        }
        return children;
    }
    public static boolean testGoal(HashMap<Integer, int[]> goalPos, Node node){
        for (int i = 0; i < node.getState()[0].length; i++) {
            for (int j = 0; j < node.getState()[0].length; j++) {
                if (goalPos.get(node.getState()[i][j])[0] != i || goalPos.get(node.getState()[i][j])[1] != j ){
                    return false;
                }
            }
        }
        return true;
    }
    public static int aStar(Node root, HashMap<Integer, int[]> goalPos, int heuristic){
        Queue< Node > queue = new LinkedList<>();
        queue.add(root);
        Node node =  ((LinkedList<Node>) queue).removeFirst();
        node.setChildren(getChildren(node,goalPos,heuristic));
        while (!testGoal(goalPos, node)){
            int count = 0;
            LinkedList<Integer> indexs = new LinkedList<>();
            while(count < node.getChildren().size()) {
                int indexToAdd = 0;
                int min = Integer.MAX_VALUE;
                int index = 0;
                for (Node child : node.getChildren()) {
                    if (child.getCost() < min && !indexs.contains(index)) {
                        indexToAdd = index;
                        min = child.getCost();
                    }
                    index++;
                }
                indexs.add(indexToAdd);
                queue.add(node.getChildren().get(indexToAdd));
                generatedNodes++;
                count++;
            }
            node = ((LinkedList<Node>) queue).removeFirst();
            searchedNoes++;
            node.setChildren(getChildren(node,goalPos,heuristic));

        }
        lastDepth = node.getDepth();
        return node.getDepth();
    }
    public static int ids(Node root, HashMap<Integer, int[]> goalPos, int heuristic){
        int depth = 0;
        while (true){
            Queue< Node > queue = new LinkedList<>();
            queue.add(root);
            Node node = ((LinkedList<Node>) queue).removeFirst();

            while (node.getDepth() < depth){
                if (testGoal(goalPos, node)){
                    lastDepth = node.getDepth();
                    return node.getDepth();
                }
                node.setChildren(getChildren(node,goalPos,heuristic));
                for ( Node child: node.getChildren()) {
                    queue.add(child);
                    generatedNodes++;
                }
                node = ((LinkedList<Node>) queue).removeFirst();
                searchedNoes++;
            }
            added.clear();
            added.put(root, 1);
            depth++;
        }
    }
    public static void print(HashMap<Integer, int[] > goal){
        for (Map.Entry<Integer, int[] > entry : goal.entrySet()) {

            System.out.println ("Key: " + entry.getKey() + " Value: " + entry.getValue()[0] + ", " + entry.getValue()[1]) ;
        }
    }
    public static void main(String[] args){
        int sizePuzzle = 3;
        int[][] puzzle = createPuzzle(sizePuzzle);
        int[] ceroPos = {sizePuzzle - 1,sizePuzzle - 1};

        Node root = new Node(puzzle, null, null, 0, ceroPos);
        mess(root, 14);
        added.put(root,1);
        //int[][] prueba = {{8,7,2}, {4,1,3}, {5,0,6}};
        //int[][] prueba = {{0,6,2}, {1,4,3}, {7,5,8}};
        //root.setState(prueba);
        //root.setCeroPos(new int[]{2,1});
        HashMap<Integer, int[]> goalPos = goalPos(sizePuzzle);
        System.out.println(root);



        long start = System.currentTimeMillis();
        //aStar(root, goalPos, 0);
        //aStar(root, goalPos, 1);
        ids(root, goalPos, 1);
        long finish = System.currentTimeMillis();
        long totalTime = finish - start;

        System.out.println("search: " + searchedNoes);
        System.out.println("generated: " + generatedNodes);
        System.out.println("depth: " + lastDepth);
        System.out.println("time: " + totalTime);


    }
}
