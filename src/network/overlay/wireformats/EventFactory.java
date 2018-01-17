package network.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by toddw on 1/23/17.
 */
public class EventFactory implements Protocol {
    private static EventFactory instance = null;
    private Event e;

    protected EventFactory() {}

    public static EventFactory getInstance() {
        if (instance == null)
            instance = new EventFactory();
        return instance;
    }

    public synchronized Event createEvent(byte[] message) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        switch (type) {
            case (REGISTER_REQUEST):
                e = new RegisterRequest(message);
                break;
            case (REGISTER_RESPONSE):
                e = new RegisterResponse(message);
                break;
            case (DEREGISTER_REQUEST):
                e = new DeregisterRequest(message);
                break;
            case (DEREGISTER_RESPONSE):
                e = new DeregisterResponse(message);
                break;
            case (MESSAGING_NODES_LIST):
                e = new MessagingNodesList(message);
                break;
            case (LINK_WEIGHTS):
                e = new LinkWeights(message);
                break;
            case (TASK_INITIATE):
                e = new TaskInitiate(message);
                break;
            case (TASK_COMPLETE):
                e = new TaskComplete(message);
                break;
            case (PULL_TRAFFIC_SUMMARY):
                e = new PullTrafficSummary(message);
                break;
            case (TRAFFIC_SUMMARY):
                e = new TrafficSummary(message);
                break;
            case (NODE_MESSAGE):
                e = new NodeMessage(message);
                break;
            case (CONNECTION_INITIATE):
                e = new ConnectionInitiate(message);
                break;
            default:
                System.out.println("Unknown event type: " + type);
                break;
        }

        return e;
    }
}
