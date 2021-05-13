import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The class represents the A* algorithm used to find the cheapest goal Node (using PriorityQueue)
 * This the Uniform Cost Search algorithm , with Heuristic function (Manhattan distance)
 * F(n) = G(n) + H(n)
 */
public class Astar implements Algorithm {
    private long time = 0;
    private int nodeCounter = 1;

    /**
     * After finding the goal node, save the result to a file.
     *
     * @param addTime true- save the time. false- otherwise
     * @param goal    the goal node
     * @param Num     amount of created nodes
     */
    public void saveToFile(boolean addTime, Node goal, int Num) {
        try {
            PrintWriter pw = new PrintWriter(outputfile);
            StringBuilder sb = new StringBuilder();
            if (goal != null) { //a path was found
                sb.append(goal.path(), 0, goal.path().length() - 1);
                sb.append("\nNum: ").append(Num);
                sb.append("\nCost: ").append(goal.getCost());
            } else { //no path was found
                sb.append("no path");
                sb.append("\nNum: ").append(Num);
            }
            if (addTime) {
                String timeTemp = String.format("%.3f", (time * Math.pow(10, -3)));
                sb.append("\n").append(timeTemp).append(" seconds");
            }
            pw.write(sb.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find a path to the goal state using A* algorithm
     *
     * @param root          root node
     * @param addTime       true- save the time. false- otherwise
     * @param openListPrint true- print open list. false-otherwise.
     * @return a string represents the algorithm results.
     */
    @Override
    public String solve(Node root, boolean addTime, boolean openListPrint) {
        long start = System.currentTimeMillis();
        HashMap<String, Node> closedList = new HashMap<>();
        PriorityQueue<Node> Q = new PriorityQueue<>();
        HashMap<String, Node> openList = new HashMap<>();
        //add initial state to open list- queue and hash
        Q.add(root);
        openList.put(root.getState().StateString(), root);
        Node curr;
        while (!Q.isEmpty()) {
            if (openListPrint) {
                System.out.println("*********************************************************");
                for (Node node : openList.values()) {
                    System.out.println(node.getState().toString());
                    System.out.println("            ----------------------");
                }
                System.out.println("*********************************************************");
            }
            //remove the cheapest node from the open list- queue and hash
            curr = Q.poll();
            openList.remove(curr.getStateString());
            //found the goal - save results and return.
            if (curr.isGoal()) {
                time = System.currentTimeMillis() - start;
                String totalTime = String.format("%.3f", (time * Math.pow(10, -3)));
                saveToFile(addTime, curr, nodeCounter);
                return "A* result is\n" + curr.path().substring(0, curr.path().length() - 1) + "\n" +
                        "num:" + nodeCounter + "\ncost: " + curr.getCost() + "\n" + totalTime + " seconds";
            }
            //Add the current node to close list (and expands the sons).
            closedList.put(curr.getState().StateString(), curr);
            List<Node> sons = curr.nextMoves();
            for (Node son : sons) {
                nodeCounter++;
                String sonStr = son.getState().StateString();
                //new node
                if (!openList.containsKey(sonStr) && !closedList.containsKey(sonStr)) {
                    Q.add(son); //add state to the open list- queue and hash
                    openList.put(sonStr, son);
                } else {
                    //if the state is exist in the open list with higher path cost - replace with the current son.
                    if (openList.containsKey(sonStr) && openList.get(sonStr).getF() > son.getF()) {
                        Node prev = openList.get(sonStr);
                        Q.remove(prev);
                        Q.add(son);
                        openList.remove(sonStr);
                        openList.put(sonStr, son);
                    }
                }
            }
        }
        //no Goal Node was found
        saveToFile(addTime, null, nodeCounter);
        String totalTime = String.format("%.3f", (time * Math.pow(10, -3)));
        return "no path\n" + totalTime + " second";
    }
}
