
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * The class represents the BFS algorithm used to find goal Node with breadth search
 */
public class BFS implements Algorithm{
    long time=0;
    int nodeCounter=1;

    /**
     * After finding the goal node, save the result to a file.
     * @param addTime true- save the time. false- otherwise
     * @param goal the goal node
     * @param Num amount of created nodes
     */
    public  void saveToFile( boolean addTime,  Node goal , int Num)  {
        try
        {
            PrintWriter pw = new PrintWriter(outputfile);
            StringBuilder sb = new StringBuilder();
            if(goal!=null) { //a path was found
                sb.append(goal.path(), 0, goal.path().length() - 1);
                sb.append("\nNum: ").append(Num);
                sb.append("\nCost: ").append(goal.getCost());
            }
            else { //no path was found
                sb.append("no path");
                sb.append("\nNum: ").append(Num);
            }
            if(addTime) {
                String timeTemp=String.format("%.3f",(time *Math.pow(10, -3)));
                sb.append("\n").append(timeTemp).append(" seconds");
            }
            pw.write(sb.toString());
            pw.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Find a path to the goal state using BFS algorithm (with loop avoidance)
     * @param root root node
     * @param addTime  true- save the time. false- otherwise
     * @param openListPrint true- print open list. false-otherwise.
     * @return a string represents the algorithm results.
     */
    public  String solve(Node root, boolean addTime, boolean openListPrint) {
        Node goal=null;
        long start= System.currentTimeMillis();
        Queue<Node> Q = new LinkedList<>();
        Q.add(root);
        HashMap<String, Node> openList= new HashMap<>();
        openList.put(root.getStateString(), root);
        HashMap<String, Node> closeList= new HashMap<>();
        while(!Q.isEmpty()) {
            if(openListPrint) {
                System.out.println("*********************************************************");
                for (Node node : openList.values()) {
                    System.out.println(node.getState());
                    System.out.println("            ----------------------");
                }
                System.out.println("*********************************************************");
            }
            Node current=Q.poll(); //remove the first Node from open list queue and open list hash
            openList.remove(current.getStateString(), current);
            closeList.put(current.getState().StateString(), current); //add it to close list
            ArrayList<Node> sons = current.nextMoves();
            for (Node son: sons){
                nodeCounter++;
                if(!openList.containsKey(son.getStateString())
                        && !closeList.containsKey(son.getState().StateString())) { // check if this node is a new node
                    if(son.isGoal()) {  //if this is the goal- save to file and end searching
                        goal=son;
                        Q.add(son);
                        long end=System.currentTimeMillis();
                        time=end-start;
                        String totalTime=String.format("%.3f",(time *Math.pow(10, -3)));
                        saveToFile(addTime, goal, nodeCounter);
                        return "BFS results:\n"+ goal.path().substring(0, goal.path().length()-1)+"\n"+
                                "num:"+nodeCounter+"\ncost: "+goal.getCost()+"\n"+totalTime+" seconds";
                    }
                    //add new node to open list- queue and hash
                    Q.add(son);
                    openList.put(son.getStateString(), son);
                }
            }

        }
        //if a goal node wasn't found
        long end=System.currentTimeMillis();
        time=end-start;
        saveToFile(addTime, goal, nodeCounter);
        String totalTime=String.format("%.3f",(time *Math.pow(10, -3)));
        return "no path\n"+totalTime+" seconds";
    }


}
