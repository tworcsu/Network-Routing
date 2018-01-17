package network.overlay.util;

import java.util.Random;

/**
 * Created by toddw on 2/8/17.
 */
public class LinkInfo {
    private String nodeA;
    private String nodeB;
    private int weight;
    private static Random rand = new Random();

    public LinkInfo(String a, String b) {
        nodeA = a;
        nodeB = b;
        weight = rand.nextInt(10) + 1;
    }

    @Override
    public String toString() {
        return nodeA + " " + nodeB + " " + weight;
    }
}
