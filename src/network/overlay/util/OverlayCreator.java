package network.overlay.util;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by toddw on 2/1/17.
 */
public class OverlayCreator {

    public OverlayCreator() {}

    public ArrayList<NodeInfo> createOverlay(Set<String> nodes, int connections) {
        ArrayList<NodeInfo> overlay = new ArrayList<>();
        for (String idString : nodes) {
            overlay.add(new NodeInfo(idString));
        }

        for (int i = 0; i < overlay.size(); i++) {
           overlay.get(i).addOutConnection(overlay.get((i+1)% overlay.size()).getIdString());
           overlay.get(i).numOutgoing++;
           overlay.get((i+1) % overlay.size()).numIncoming++;
        }

        for (int j = 0; j < overlay.size(); j++) {
            overlay.get(j).addOutConnection(overlay.get((j+2) % overlay.size()).getIdString());
            overlay.get(j).numOutgoing++;
            overlay.get((j+2) % overlay.size()).numIncoming++;
        }
        return overlay;
    }
}
