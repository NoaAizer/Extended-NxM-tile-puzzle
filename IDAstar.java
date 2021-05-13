import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * The class represents the IDA* algorithm used to find the cheapest goal node - iterative deepening A* (using loop avoidance)
 */
public class IDAstar implements Algorithm {
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
     * Find a path to the goal state using IDA* algorithm (using loop avoidance)
     *
     * @param root          root node
     * @param addTime       true- save the time. false- otherwise
     * @param openListPrint true- print open list. false-otherwise.
     * @return a string represents the algorithm results.
     */
    @Override
    public String solve(Node root, boolean addTime, boolean openListPrint) {
        long start = System.currentTimeMillis();
        Stack<Node> L = new Stack<>();
        HashMap<String, Node> H = new HashMap<>();
        int t = root.heuristic(); // t= h(root)
        while (t != Integer.MAX_VALUE) {
            int minF = Integer.MAX_VALUE;
            //add root to open list- stack and hash map
            L.add(root);
            H.put(root.getStateString(), root);
            while (!L.isEmpty()) {
                if (openListPrint) {
                    System.out.println("*********************************************************");
                    for (Node curr : H.values()) {
                        System.out.println(curr.getState().toString());
                        System.out.println("            ----------------------");
                    }
                    System.out.println("*********************************************************");
                }
                Node curr = L.pop(); //get current node from stack
                String currState = curr.getStateString();
                //if this state is exist and on the current path("out")
                if (H.containsKey(currState) && H.get(currState).getmark().contentEquals("out"))
                    H.remove(currState, curr); //remove from the hash
                else {
                    curr.setmark("out"); //on the current path
                    L.add(curr); // add again to the stack as "out"
                    List<Node> sons = curr.nextMoves();
                    for (Node son : sons) {
                        nodeCounter++;
                        String sonState = son.getStateString();
                        if (son.fCalc() > t) { //if f(son)=h(son)+g(son) is greater than the threshold
                            minF = Math.min(minF, son.fCalc()); //update threshold and continue with the next son
                        }
                        else {
                            if (H.containsKey(sonState)) { //such state is in the open list
                                Node prev = H.get(sonState);
                                if (!prev.getmark().contentEquals("out")) //and mark as out- ignore
                                    if (prev.fCalc() > son.fCalc()) { //if current son is cheaper replace
                                        H.remove(prev.getStateString());
                                        L.remove(prev);
                                        //       prev = son;
                                    }
                            }
                            else {
                                if (son.isGoal()) { //goal node was found, return result
                                    time = System.currentTimeMillis() - start;
                                    String totalTime = String.format("%.3f", (time * Math.pow(10, -3)));
                                    saveToFile(addTime, son, nodeCounter);
                                    return "IDA* result is\n" + son.path().substring(0, son.path().length() - 1) + "\n" + "num: "
                                            + nodeCounter + "\ncost: " + son.getCost() + "\n" + totalTime + " milliseconds";
                                }
                                L.add(son);
                                H.put(sonState, son);
                            }
                        }

                    }
                }
            }
            t = minF;
            root.setmark("");

        }
        //no goal Node was found
        time = System.currentTimeMillis() - start;
        saveToFile(addTime, null, nodeCounter);
        String timeTemp = String.format("%.3f", (time * Math.pow(10, -3)));
        return "no path\n" + timeTemp + " seconds";

    }
}
