/**
 * this interface is implemented by all algorithm classes
 * @author Noa
 *
 */
public interface Algorithm {
     String outputfile="output.txt";
    /**
     * save result to file
     */
     void saveToFile( boolean toTime,  Node g , int Num);
    /**
     * search goal Node starting with root-initial state
     * @param root
     */
     String solve(Node root, boolean toTime, boolean openList);

}
