import java.util.*;

/**
 * The class represents a node in the search graph.
 */
public class Node implements Comparable<Node> {

    private static int keyCounter = 1;
    public  enum Moves {L, U, R, D}
    private Board state;
    private Node parent;
    private int depth;
    private int key;
    private String step;
    private int h, f, cost;
    private String mark;


    /**
     * Constructor
     *
     * @param s state (the board)
     * @param p parent node
     * @param st last step
     * @param price cost from root till now
     */
    public Node(Board s, Node p, String st, int price) {
        cost = price;
        state = s;
        parent = p;
        if (p != null)
            depth = p.getDepth() + 1;
        else depth = 0;
        step = st;
        key=keyCounter++;
        heuristic();
        fCalc();
        mark = "";

    }

    /**
     * Checks whether the node is the goal state
     * Compare between to currentBoard to goalBoard
     * @return true- is the goal , false- otherwise
     */
    public boolean isGoal() {
        for (int i = 0; i < this.state.r; i++) {
            for (int j = 0; j < this.state.c; j++) {
                if (this.state.matrix[i][j] != this.state.goalMatrix[i][j])
                    return false;
            }
        }
        return true;
    }

    /**
     * Calculates all possible next moves from current node
     * and create a list of all the nodes
     * First try to move 2 tile and then only one
     * @return a list with all the sons of the current node
     */
    public ArrayList<Node> nextMoves() {
        ArrayList<Node> sons = new ArrayList<>();
        HashMap<String, Integer> lastMoves = this.getState().lastMoves;
        if (parent != null) lastMoves = this.parent.getState().lastMoves; // add all the last moves on that path
        lastMoves.putAll(this.getState().lastMoves);
        for (Moves m : Moves.values()) { // checks for 2 moves
            Board myState = new Board(this.state);
            int stepCost=0, val1=-1, val2=-1;
            boolean toMove = false; // if to make a move or not.

            if (checkTwoMoves(myState, m)) {
                int[][] updatedLocs = new int[2][2];
                //copy blanks
                for (int i = 0; i < myState.blanks.length; i++)
                    updatedLocs[i] = myState.blanks[i].clone();
                switch(m) {
                    case L: //left
                        updatedLocs[0][1] += 1;
                        updatedLocs[1][1] += 1;
                        val1 = myState.matrix[updatedLocs[0][0]][updatedLocs[0][1]];
                        val2 = myState.matrix[updatedLocs[1][0]][updatedLocs[1][1]];
                        stepCost = 6;
                        toMove = checkLastMoves(lastMoves, val1, updatedLocs[0][0], updatedLocs[0][1] - 1)
                                && checkLastMoves(lastMoves, val2, updatedLocs[1][0], updatedLocs[1][1] - 1);
                        if (toMove)
                            myState.moveTile(null, true, m);
                        break;
                        case U: //Up
                        updatedLocs[0][0] += 1;
                        updatedLocs[1][0] += 1;
                        val1 = myState.matrix[updatedLocs[0][0]][updatedLocs[0][1]];
                        val2 = myState.matrix[updatedLocs[1][0]][updatedLocs[1][1]];
                        stepCost = 7;
                        toMove = checkLastMoves(lastMoves, val1, updatedLocs[0][0] - 1, updatedLocs[0][1])
                                && checkLastMoves(lastMoves, val2, updatedLocs[1][0] - 1, updatedLocs[1][1]);
                        if (toMove)
                            myState.moveTile(null, true, m);
                        break;

                        case R: //Right
                        updatedLocs[0][1] -= 1;
                        updatedLocs[1][1] -= 1;
                        val1 = myState.matrix[updatedLocs[0][0]][updatedLocs[0][1]];
                        val2 = myState.matrix[updatedLocs[1][0]][updatedLocs[1][1]];
                        stepCost = 6;
                        toMove = checkLastMoves(lastMoves, val1, updatedLocs[0][0], updatedLocs[0][1] + 1)
                                && checkLastMoves(lastMoves, val2, updatedLocs[1][0], updatedLocs[1][1] + 1);
                        if (toMove)
                            myState.moveTile(null, true, m);
                        break;

                        case D: //Down
                        updatedLocs[0][0] -= 1;
                        updatedLocs[1][0] -= 1;
                        val1 = myState.matrix[updatedLocs[0][0]][updatedLocs[0][1]];
                        val2 = myState.matrix[updatedLocs[1][0]][updatedLocs[1][1]];
                        stepCost = 7;
                        toMove = checkLastMoves(lastMoves, val1, updatedLocs[0][0] + 1, updatedLocs[0][1])
                                && checkLastMoves(lastMoves, val2, updatedLocs[1][0] + 1, updatedLocs[1][1]);
                        if (toMove)
                            myState.moveTile(null, true, m);
                        break;
                }
                if (toMove) {
                    // create new node and update blanks.
                    Node n = new Node(myState, this, val1 + "&" + val2 + m + "-", this.cost + stepCost);
                    for (int i = 0; i < n.state.blanks.length; i++)
                        n.state.blanks[i] = updatedLocs[i].clone();
                    sons.add(n);
                }
            }
        }
        //move one tile
        for (Moves m : Moves.values()) {
            state.sortBlanks();// compare by row and then by col
            //Check each blank tile
            for (int i = 0; i < state.blanks.length && state.blanks[i][0] != -1; i++) {
                boolean toMove = false;
                Board myState = new Board(this.state);
                int val = 0, updatedR = 0, updatedC = 0;
                int[] blank = state.blanks[i].clone();
                if (checkOneMove(myState, m, blank)) {
                    switch (m){
                        case L : //left
                        updatedR = blank[0];
                        updatedC = blank[1] + 1;
                        val = myState.matrix[updatedR][updatedC];
                        toMove = checkLastMoves(lastMoves, val, blank[0],blank[1]);
                        if (toMove) myState.moveTile(blank, false, m);
                        break;
                        case U: //Up
                        updatedR = blank[0] + 1;
                        updatedC = blank[1];
                        val = myState.matrix[updatedR][updatedC];
                        toMove = checkLastMoves(lastMoves, val, blank[0], blank[1]);
                        if (toMove) myState.moveTile(blank, false, m);
                        break;

                        case R : //Right
                        updatedR = blank[0];
                        updatedC = blank[1] - 1;
                        val = myState.matrix[updatedR][updatedC];
                        toMove = checkLastMoves(lastMoves, val, blank[0], blank[1]);
                        if (toMove) myState.moveTile(blank, false, m);
                        break;
                        case D: //Down
                        updatedR = blank[0] - 1;
                        updatedC = blank[1];
                        val = myState.matrix[updatedR][updatedC];
                        toMove = checkLastMoves(lastMoves, val, blank[0],blank[1]);
                        if (toMove) myState.moveTile(blank, false, m);
                        break;

                }
                    if (toMove) {
                        // create new node and upadte blanks
                        Node n = new Node(myState, this, val + m.toString() + "-", this.cost + 5);
                        for (int j = 0; j < n.state.blanks.length; j++)
                            if (n.state.blanks[j][0] == blank[0] && n.state.blanks[j][1] == blank[1]) { // update the blank tile place
                                n.state.blanks[j][0] = updatedR;
                                n.state.blanks[j][1] = updatedC;
                            }
                        n.state.sortBlanks();
                        sons.add(n);
                    }
                }
            }
        }
        return sons;
    }

    /**
     * Check whether the current tile did the opposite move in that path.
     * @param lastMoves a map contains the blank the last value that it was switch with
     * @param value te value tile
     * @param blankR the row of the blank
     * @param blankC the column of the blank
     * @return true - the opposite move is not on the path , false- otherwise.
     */
    private boolean checkLastMoves(HashMap<String, Integer> lastMoves ,int value , int blankR , int blankC){
    String loc = blankR + "," + blankC;
    if (lastMoves.get(loc) == null || lastMoves.get(loc) != value)
        return true;
    return false;
}

    /**
     * Check if the given move is possible with the given blank tile
     * @param b the borad
     * @param m the move
     * @param blank the blank tile
     * @return true- possible, false-otherwise.
     */
    private boolean checkOneMove(Board b, Moves m, int[] blank) {
        int[][] matrix = this.state.matrix; //previous matrix
        switch (m) {
            case L:
                if (blank[1] != b.c - 1
                        && matrix[blank[0]][blank[1] + 1] != 0)//not a blank
                    return true;
                break;
            case U:
                if (blank[0] != b.r - 1
                        && matrix[blank[0] + 1][blank[1]] != 0) //not a blank
                    return true;
                break;
            case R:
                if (blank[1] != 0
                        && matrix[blank[0]][blank[1] - 1] != 0)
                    return true;
                break;
            case D:
                if (blank[0] != 0
                        && matrix[blank[0] - 1][blank[1]] != 0)
                    return true;
                break;
        }
        return false;
    }

    /**
     * Check if the given move is possible with the two blank tiles
     * @param b the board
     * @param m the move
     * @return true- possible , false- otherwise
     */
    private boolean checkTwoMoves(Board b, Moves m) {
        int[][] blanks = b.blanks.clone();
        if (blanks[1][0] == -1) // there is only one blank and try to move two.
            return false;
        switch (m) {
            case L: // One below the other and not on the last column
                if ((Math.abs(blanks[0][0] - blanks[1][0]) == 1)
                        && blanks[0][1] == blanks[1][1]
                        && blanks[0][1] != this.state.c - 1)
                    return true;
                break;

            case U:
                // One next to the other and not on the last row
                if ((Math.abs(blanks[0][1] - blanks[1][1]) == 1)
                        && blanks[0][0] == blanks[1][0]
                        && blanks[0][0] != this.state.r - 1)
                    return true;
                break;

            case R:
                // One below the other and not on the first column
                if ((Math.abs(blanks[0][0] - blanks[1][0]) == 1)
                        && blanks[0][1] == blanks[1][1]
                        && blanks[0][1] != 0)
                    return true;
                break;

            case D:
                // One next to the other and not on the first row
                if ((Math.abs(blanks[0][1] - blanks[1][1]) == 1)
                        && blanks[0][0] == blanks[1][0]
                        && blanks[0][0] != 0)
                    return true;
                break;
        }
        return false;
    }

    /**
     * Returns a string represents the path from root node to current node
     * @return the path
     */
    public String path() {
        if (this.parent == null)
            return "";
        String s = this.step;
        return this.parent.path() + s;
    }


    /*
     * getters & setters
     */
    public int getDepth() {
        return depth;
    }

    public Board getState() {
        return state;
    }

    public void setState(Board state) {
        this.state = state;
    }

    public int getF() {
        return f;
    }

    public int heuristic() {
        h = manhattanDist();
        return h;
    }

    public int fCalc() {
        f = h + cost;
        return f;
    }

    public int getCost() {
        return cost;
    }

    public String getmark() {
        return mark;
    }

    public void setmark(String mark) {
        this.mark = mark;
    }

    public String getStateString() {
        return this.state.StateString();
    }

    public int getKey() {
        return key;
    }

    /**
     * Deep compare to another node
     *
     * @param other the other node to be compared to
     * @return true- eqauls , false- otherwise.
     */
    boolean equals(Node other) {
        if (other != null && this.state.equals(other.state))
            return true;
        return false;
    }

    public String toString() {
        return "state:" + state + "\n step: " + step + "\n depth: " + depth + "\n f-value: " + f;
    }

    /**
     *Calculate the heuristic function-  use Manhattan distance.
     * Sum the distance of each tile from his goal place on the goal board.
     * @return the total sum of all tiles on the board.
     */
    private int manhattanDist() {
        int cost = 0;
        for (int i = 0; i < this.getState().r; i++) { //rows
            for (int j = 0; j < this.getState().c; j++) { //columns
                int value = this.getState().matrix[i][j];
                if (value != this.getState().goalMatrix[i][j]) {
                    cost += calcDist(value,i,j);
                }
            }
        }
        return cost;
    }


    /**
     * Calculate the distance from his goal place by rows and cols
     * @param value the value of the tile
     * @param currRow current row
     * @param currCol current column
     * @return distance from goal of current tile
     */
    public int calcDist(int value,int currRow, int currCol) {
        int goalRow = -1 , goalCol = -1;
        for (int i = 0; i < this.getState().r; i++) {
            for (int j = 0; j < this.getState().c; j++) {
                if (this.getState().goalMatrix[i][j] == value) {
                    goalRow = i;
                    goalCol = j;
                }
            }
        }
        return Math.abs(currRow-goalRow) + Math.abs(currCol-goalCol);
    }

    /**
     * Sort the nodes by their f-values.
     * If f-values are identical - sort by creation time (key)
     * @param o the other node
     * @return 1- the current node has higher f-value
     * -1 - the other node hase higher f-value
     * 0- identical
     */
    @Override
    public int compareTo(Node o) {
        if (o.fCalc() < this.fCalc()) {
            return 1;
        } else if (o.fCalc() > this.fCalc()) {
            return -1;
        }
        if (this.fCalc() == o.fCalc()) {
            if (this.getKey() > o.getKey()) //o is younger so return 1
                return 1;
            else return -1;
        }
        return 0;

    }
}
