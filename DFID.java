import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;


/**
 * The class represents the DFID algorithm used to find the cheapest goal Node -Iterative Deepening DFS
 * Recursive DFID with loop avoidance.
 */
public class DFID implements Algorithm{
    private long time=0;
    private int nodeCounter=1;

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
     * Find a path to the goal state using DFID algorithm (using loop avoidance)
     *
     * @param root          root node
     * @param addTime       true- save the time. false- otherwise
     * @param openListPrint true- print open list. false-otherwise.
     * @return a string represents the algorithm results.
     */
    @Override
    public String solve(Node root, boolean addTime, boolean openListPrint) {
        long start=System.currentTimeMillis();
        for (int depth=1 ; depth < Integer.MAX_VALUE; depth++) {
            if(openListPrint)
                System.out.println("Current depth= "+depth);
            HashMap<String, Node> H= new HashMap<>();
            Object[] result=this.limitedDFS(root, depth, H, openListPrint);
            if(!(result[0].toString()).equalsIgnoreCase("cutOff")) {
                Node goal=(Node) result[1];
                time=System.currentTimeMillis()-start;
                saveToFile(addTime,goal , nodeCounter);
                String totalTime=String.format("%.3f",(time *Math.pow(10, -3)));
                return "DFID result is:\n"+ goal.path().substring(0, goal.path().length()-1)+"\n"+"num: "
                        +nodeCounter+"\ncost: "+goal.getCost()+"\n"+totalTime+" seconds";
            }
        }
        time=System.currentTimeMillis()-start;
        saveToFile(addTime,null , nodeCounter);
        String totalTime=String.format("%.3f",(time *Math.pow(10, -3)));
        return "no path"+totalTime;
    }

    /**
     * Recursive limited DFS- Search the goal node from the current node till the cutoff
     *
     * @param current the current node
     * @param cutoff the limited depth
     * @param h HashMap for saving the nodes
     * @param openListPrint true- print open list. false-otherwise.
     * @return Object representing the result:
     * Object[0]- a String- "fail"/"cutOff"/if the goal was found-the path.
     * Object[1] the goal Node/null if the goal wasn't found
     */
    private Object[] limitedDFS(Node current, int cutoff, HashMap<String, Node> h, boolean openListPrint) {
        if (current.isGoal()) {
            Object[] ans = new Object[2];
            ans[0] = current.path(); //the path from root to current
            ans[1] = current; //goal Node
            return ans;
        }
        if (cutoff == 0) { //reach to the limit depth
            return new Object[]{"cutOff", null};
        }
        h.put(current.getState().StateString(), current);
        boolean isCutOff = false;
        List<Node> sons = current.nextMoves();
        for (Node son : sons) {
                nodeCounter++;
                if (!h.containsKey(son.getState().StateString())) {//not on the current path
                    Object[] result = limitedDFS(son, cutoff - 1, h, openListPrint);
                    if ((result[0].toString()).equalsIgnoreCase("cutOff"))
                        isCutOff = true;
                    else {
                        if (!(result[0].toString()).equalsIgnoreCase("fail"))
                            return result;
                    }
                }
            }
            if (openListPrint && !h.isEmpty()) { //prints openlist
                System.out.println("*********************************************************");
                for (Node node : h.values()) {
                    System.out.println(node.getState().toString());
                    System.out.println("            ----------------------");
                }
                System.out.println("*********************************************************");
            }
            h.remove(current.getState().StateString());
            if (isCutOff) { //one of the sons was cut off during search
                return new Object[]{"cutOff", null};
            } else {
                return new Object[]{"fail", null};
            }
        }
    }


