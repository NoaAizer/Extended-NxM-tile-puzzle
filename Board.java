import java.util.*;

public class Board {
    int r,c;
    int[][] matrix, goalMatrix;
    int[][] blanks = new int[2][2]; //there are at most 2 blanks
    HashMap<String,Integer> lastMoves = new HashMap<>(); // save for each place the last value moved to there

    /**
     * Constructor
     * @param r amount of rows
     * @param c amount of columns
     * @param startMatrix the start board
     * @param goalMatrix the goal board
     */
    public Board(int r, int c, int[][] startMatrix, int[][] goalMatrix) {
        this.r = r;
        this.c = c;
        this.matrix = startMatrix;
        this.goalMatrix =goalMatrix;
        int countBlanks =0;
        for (int row=0; row < matrix.length; row++)
        {
            for (int col=0; col < matrix[row].length; col++)
            {
                if( matrix[row][col] == 0 ) { //blank
                    blanks[countBlanks][0]=row;
                    blanks[countBlanks][1]=col;
                    countBlanks++;
                }
            }
        }
        if(countBlanks==1){  // there is only one blank
            blanks[1][0]=-1;
            blanks[1][1]=-1;
        }
    }

    /**
     * Copy Constructor
     * @param other the other board to be copied
     */
    public Board(Board other) {
        this.r = other.r;
        this.c = other.c;
        this.matrix=new int[r][c];
        for (int i = 0; i < r; i++) {
                this.matrix[i]=other.matrix[i].clone();
            }

        this.goalMatrix=new int[r][c];
        for (int i = 0; i < r; i++) {
                this.goalMatrix[i]=other.goalMatrix[i].clone();
        }
        for(int i=0;i<other.blanks.length;i++){
            this.blanks[i]=other.blanks[i].clone();
        }
    }

    /**
     * swap two tiles on the board
     * @param x1 current row
     * @param y1 current columns
     * @param x2 blank row
     * @param y2 blank column
     */
    public void swap(int x1, int y1, int x2, int y2) {
        String key = x1+","+y1;
        lastMoves.put(key,this.matrix[x1][y1]); // update the last move on that blank
        int temp= this.matrix[x1][y1];
        this.matrix[x1][y1]=this.matrix[x2][y2]; // the updated blank
        this.matrix[x2][y2]=temp; // the new place of the tile
    }

    /**
     * Checks whether two boards are state-equals
     * @param other the other board to be compared to
     * @return true- equals , false- otherwise
     */
    public boolean equals(Board other) {
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                if(this.matrix[i][j] != other.matrix[i][j])
                    return false;
            }
        }
        System.out.println("check");
        return true;
    }
    public String toString() {
    String s="";
        for (int i = 0; i < r; i++) {
            s+="\n";
            for (int j = 0; j < c; j++) {
               s+=this.matrix[i][j]+",";
            }
        }
        return s;
    }

    /**
     * Move the tiles according to given move and blanks
     * @param blank the blank to be switch (if move both blank = null)
     * @param two true- move two tile, false- move only one
     * @param m the current move
     */
    public void moveTile(int[] blank, boolean two, Node.Moves m){
       int row =0  ,col = 0; // in how many steps to move the blank by rows and columns.
        switch(m){
            case L:
                col += 1;
                break;
            case U:
                row += 1;
                break;
            case R:
                col -= 1;
                break;
            case D:
                row -= 1;
                break;
        }
        if(two){ // Move 2 tiles
            for (int i =0 ;i< blanks.length;i++){
                this.swap(blanks[i][0]+row,blanks[i][1]+col,blanks[i][0],blanks[i][1]);
            }
        }
        else this.swap(blank[0]+row,blank[1]+col,blank[0],blank[1]);
    }

    /**
     *  Creates a string represents the state of the board- in one row.
     * @return
     */
    public String StateString(){
        String s="";
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                s=s+matrix[i][j]+",";
            }
        }
        return s;
    }

    /**
     * Sort the blank list by row and then by column (higher and lefter)
     */
    public void sortBlanks(){
        if(this.blanks[1][0] == -1) return; // there is only one blank
        else{
            if(this.blanks[0][0]>this.blanks[1][0] ||
                    (this.blanks[0][0]==this.blanks[1][0] && this.blanks[0][1]>this.blanks[1][1])){ //swap blanks
                int[] temp = this.blanks[0].clone();
                this.blanks[0]= this.blanks[1].clone();
                this.blanks[1]= temp;
                return;
            }
        }
    }
}
