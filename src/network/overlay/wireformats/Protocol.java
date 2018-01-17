package network.overlay.wireformats;

/**
 * Created by toddw on 1/23/17.
 */
public interface Protocol {
    public static final int REGISTER_REQUEST = 1;
    public static final int REGISTER_RESPONSE = 2;
    public static final int DEREGISTER_REQUEST = 3;
    public static final int DEREGISTER_RESPONSE = 4;
    public static final int MESSAGING_NODES_LIST = 5;
    public static final int LINK_WEIGHTS = 6;
    public static final int TASK_INITIATE = 7;
    public static final int TASK_COMPLETE = 8;
    public static final int PULL_TRAFFIC_SUMMARY = 9;
    public static final int TRAFFIC_SUMMARY = 10;
    public static final int NODE_MESSAGE = 11;
    public static final int CONNECTION_INITIATE = 12;


    public static final byte SUCCESS = 0;
    public static final byte FAILURE = 1;
}
