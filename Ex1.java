import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


public class Ex1 {
    static String fileName="input4.txt";

    public static void main(String[] args) {
        play(fileName);
    }

    /**
     * This method reads the data from a file and starts solving using the matching algorithm
     * @param pathFile
     * 1. algorithm
     * 2.with time /no time
     * 3.with open/no open (open list)
     * 4. nXm (n rows & m columns)
     * The board (n rows) , empty block= '_'
     * 4+n+1. Goal state:
     * Final Order (n rows)
     *
     * Example:
     * BFS
     * with time
     * no open
     * 3x4
     * 1,2,3,4
     * 5,6,11,7
     * 9,10,8,_
     * Goal state:
     * 1,2,3,4
     * 5,6,7,8
     * 9,10,11,_
     */
    public static  void play(String pathFile) {
        ArrayList<String> lines= new ArrayList<>();
        //reading all lines
        try {
            FileInputStream fstream = new FileInputStream(pathFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                lines.add(strLine);
            }
            br.close();
        }
        catch (FileNotFoundException e){

            System.err.println("Error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean time=false, openList=false;
        int m,n;
        if(lines.get(1).contains("with time") )   //time
            time=true;
        if(lines.get(2).contains("with open")) //open list
            openList=true;
        String [] line3= lines.get(3).split("x"); //matrix size

        n=Integer.parseInt(line3[0]);
        m=Integer.parseInt(line3[1]);
        int[][] startMatrix = new int[n][m];
        for(int i=0;i<n;i++){
            String fixed=lines.get(4+i).replace('_', '0'); // replace blank places with 0
            int[] row = Arrays.stream(fixed.split(",")).mapToInt(Integer::parseInt).toArray();
            System.arraycopy(row, 0, startMatrix[i], 0, m);
        }
        int[][] goalMatrix = new int[n][m];
        for(int i=0;i<n;i++){
            String fixed=lines.get(5+n+i).replace('_', '0'); // replace blank places with 0
            int[] row = Arrays.stream(fixed.split(",")).mapToInt(Integer::parseInt).toArray();
            System.arraycopy(row, 0, goalMatrix[i], 0, m);

        }

        Board b= new Board(n, m,startMatrix, goalMatrix); //creates a board using all info- number and colors
        Algorithm algo=null;
        //finds an algorithm to use
        switch(lines.get(0)){
            case "BFS":
                algo=new BFS();
                break;
            case "DFID":
                algo= new DFID();
                break;
            case "A*":
                algo= new Astar();
                break;
            case "IDA*":
                algo= new IDAstar();
                break;
            case "DFBnB":
                algo= new DFBnB();
                break;
        }
        Node root= new Node(b, null, "init", 0);
        System.out.println(algo.solve(root, time, openList));

    }
}
