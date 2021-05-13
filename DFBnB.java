import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * The class represents the DFBnB algorithm used to find the cheapest goal Node -Depth First Branch and Bound
 */
public class DFBnB implements Algorithm{
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
     * Find a path to the goal state using DFBnB algorithm (using loop avoidance)
     *
     * @param root          root node
     * @param addTime       true- save the time. false- otherwise
     * @param openListPrint true- print open list. false-otherwise.
     * @return a string represents the algorithm results.
     */
    @Override
    public String solve(Node root, boolean addTime, boolean openListPrint) {
        long start=System.currentTimeMillis();
        Stack<Node> L= new Stack<>(); //open list stack
        HashMap<String, Node> openListHash= new HashMap<>();
        L.add(root);
        openListHash.put(root.getStateString(), root);
        Node result=null;
        int t = Integer.MAX_VALUE;
        while(!L.isEmpty()) {
            if(openListPrint) {
                System.out.println("*********************************************************");
                for (Node node : openListHash.values()) {
                    System.out.println(node.getState().toString());
                    System.out.println("            ----------------------");
                }
                System.out.println("*********************************************************");
            }
            Node curr=L.pop();
            //if this node is on the current path and was explored
            if(openListHash.containsKey(curr.getStateString())
                    && openListHash.get(curr.getStateString()).getmark().contentEquals("out"))
                openListHash.remove(curr.getStateString()); //remove from hash.
            else {
                curr.setmark("out"); //mark as explored
                L.add(curr);
                List<Node> sons = curr.nextMoves();
                for (Node son : sons) {
                    nodeCounter++;
                }
                Collections.sort(sons); //sort sons by f value h(son)+g(son)
                Node [] sonsArr= new Node[sons.size()];
                for (int i = 0; i < sons.size(); i++) {//initiate sons array
                    sonsArr[i]=sons.get(i);
                }
                for (int i = 0; i < sons.size(); i++) {
                   Node son=sonsArr[i];
                    if(son!=null) { //if son's f is greater than threshold, the branch needs to be cut
                        if(son.fCalc()>= t) {
                            for (int j = i; j < sonsArr.length; j++) {
                                sonsArr[j]=null;
                            }
                        }
                        else { //loop avoidance
                            if(openListHash.containsKey(son.getStateString())) { //the node was already discovered
                                Node prev=openListHash.get(son.getStateString());
                                if(prev.getmark().contentEquals("out")) //the previous son was explored
                                    sonsArr[i]=null;
                                else {
                                        if(prev.fCalc() <= son.fCalc() ) //the previous son is cheaper - then remove current son
                                            sonsArr[i]=null;
                                        else {
                                            openListHash.remove(prev.getStateString());
                                            L.remove(prev);
                                        }
                                }
                            }
                            else {
                                if(son.isGoal()) { //the goal was found- f(g)<t
                                    t=son.fCalc(); //update threshold
                                    result=son;
                                    for (int j = i; j < sonsArr.length; j++) { //cut the rest of the branch
                                        sonsArr[j]=null;
                                    }
                                }
                            }
                        }
                    }
                }
                for (int k=sons.size()-1; k>=0 ; k--) { //insert sons in a reverse order to L and the Hash
                    if(sonsArr[k]!=null) {
                        openListHash.put(sonsArr[k].getStateString(), sonsArr[k]);
                        L.add(sonsArr[k]);
                    }
                }
            }
        }
        //save result to a file
        time= System.currentTimeMillis()-start;
        String totalTime=String.format("%.3f",(time *Math.pow(10, -3)));
        saveToFile(addTime, result, nodeCounter);
        return "DFBnB result is\n"+ result.path().substring(0, result.path().length()-1)+"\n"+"num:"
                +nodeCounter+"\ncost: "+result.getCost()+"\n"+totalTime+" milliseconds";
    }
}