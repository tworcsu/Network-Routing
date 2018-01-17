package network.overlay.util;

import java.util.ArrayList;

/**
 * Created by toddw on 1/29/17.
 */
public class NodeInfo {
    public String idString;
    public int numIncoming;
    public int numOutgoing;
    private ArrayList<String> outConnections;

    public NodeInfo(String id) {
        this.idString = id;
        numIncoming = 0;
        numOutgoing = 0;
        outConnections = new ArrayList<>();
    }

    public String getOutConnections() {
        String output = "";
        for (String s : outConnections)
            output += s + " ";
        return output.substring(0,output.length() - 1);
    }

    public String getIdString() {
        return idString;
    }

    public void addOutConnection(String s) {
        outConnections.add(s);
    }
}
