package network.overlay.node;

import network.overlay.util.LinkInfo;
import network.overlay.util.NodeInfo;
import network.overlay.util.OverlayCreator;
import network.overlay.transport.TCPSender;
import network.overlay.transport.TCPServer;
import network.overlay.util.StatisticsAndDisplay;
import network.overlay.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static network.overlay.wireformats.Protocol.*;

/**
 * Created by toddw on 1/22/17.
 */
public class Registry extends Node {
    public HashMap<String, TCPSender> nodeRegistry;
    private ArrayList<LinkInfo> links;
    private ArrayList<String> completedNodes;
    private StatisticsAndDisplay stats;

    public Registry(int portNumber) {
        this.portNumber = portNumber;
        nodeRegistry = new HashMap<String, TCPSender>() {
        };
        links = new ArrayList<>();
        completedNodes = new ArrayList<>();
        try {
            server = new TCPServer(this, this.portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearCompleted() {
        this.completedNodes.clear();
    }

    public void onEvent(Event e, Socket socket) throws IOException {
        int type = e.getType();
        switch (type) {
            case REGISTER_REQUEST:
                synchronized (this) {
                    RegisterRequest request = (RegisterRequest) e;
                    String idString = request.getHostName() + ":" + Integer.toString(request.getPortNumber());
                    byte statusCode;
                    String infoString;
                    if (this.nodeRegistry.containsKey(idString)) {
                        statusCode = FAILURE;
                        infoString = "This node has already registered.";
                    }
                    else {
                        statusCode = SUCCESS;
                        this.nodeRegistry.put(idString, new TCPSender(socket));
                        int overlaySize = this.nodeRegistry.size();
                        infoString = String.format("The number of messaging nodes currently constituting the overlay is (%d)", overlaySize);
                    }
                    RegisterResponse response = new RegisterResponse(statusCode, infoString);
                    TCPSender out = nodeRegistry.get(idString);
                    try {
                        out.sendData(response.getBytes());
                    } catch (IOException e1) {
                        System.out.println("Connection failed. Removing node from registry.");
                        this.nodeRegistry.remove(idString);
                    }
                }
                break;
            case DEREGISTER_REQUEST:
                DeregisterRequest dRequest = (DeregisterRequest) e;
                String dIdString = dRequest.getHostName() + ":" + Integer.toString(dRequest.getPortNumber());
                byte dStatusCode;
                String dInfoString;
                if (!this.nodeRegistry.containsKey(dIdString)) {
                    dStatusCode = FAILURE;
                    dInfoString = "This node was not registered.";
                }
                else {
                    dStatusCode = SUCCESS;
                    int overlaySize = this.nodeRegistry.size() - 1;
                    dInfoString = String.format("Node successfully removed. " +
                            "The number of messaging nodes currently constituting the overlay is (%d)", overlaySize);
                }
                DeregisterResponse dResponse = new DeregisterResponse(dStatusCode, dInfoString);
                TCPSender dOut = nodeRegistry.get(dIdString);
                dOut.sendData(dResponse.getBytes());
                this.nodeRegistry.remove(dIdString);
                if (dStatusCode == SUCCESS) {
                    dOut.closeSocket();
                }
                break;
            case TASK_COMPLETE:
                synchronized (this) {
                    TaskComplete complete = (TaskComplete) e;
                    System.out.println("Task complete: " + complete.getHostName() + ":" + complete.getPortNumber());
                    this.completedNodes.add(complete.getHostName() + ":" + complete.getPortNumber());
                    if (this.completedNodes.size() == this.nodeRegistry.size()) {
                        this.stats = new StatisticsAndDisplay(this);
                        Thread thread = new Thread(this.stats);
                        thread.start();
                    }
                }
                break;
            case TRAFFIC_SUMMARY:
                synchronized (this) {
                    TrafficSummary summary = (TrafficSummary) e;
                    this.stats.add(summary);
                    if (this.stats.size() == this.nodeRegistry.size()) {
                        this.stats.printStats();
                        this.clearCompleted();
                    }
                }
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: Registry [portnum]");
            System.exit(1);
        }
        Registry r = new Registry(Integer.parseInt(args[0]));
        Thread t = new Thread(r.server);
        t.start();

        // foreground commands
        Scanner userInput = new Scanner(System.in);
        String input;
        try {
            while ((input = userInput.next()) != null) {
                switch (input) {
                    case "list-messaging-nodes":
                        for (String node : r.nodeRegistry.keySet())
                            System.out.println(node);
                        break;
                    case "list-weights":
                        for (LinkInfo link : r.links) {
                            System.out.println(link);
                        }
                        break;
                    case "setup-overlay":
                        int connections = userInput.nextInt();
                        OverlayCreator o = new OverlayCreator();
                        ArrayList<NodeInfo> overlay = o.createOverlay(r.nodeRegistry.keySet(), connections);
                        for (NodeInfo nodeInfo : overlay) {
                            MessagingNodesList message = new MessagingNodesList(connections, nodeInfo.getOutConnections());
                            TCPSender out = r.nodeRegistry.get(nodeInfo.getIdString());
                            out.sendData(message.getBytes());
                            ArrayList<String> nodeConnections =
                                    new ArrayList<>(Arrays.asList(nodeInfo.getOutConnections().split(" ")));
                            for (String c : nodeConnections) {
                                r.links.add(new LinkInfo(nodeInfo.getIdString(), c));
                            }
                        }
                        break;
                    case "send-overlay-link-weights":
                        String links = "";
                        for (LinkInfo link : r.links) {
                            links += link + "\n";
                        }
                        LinkWeights message = new LinkWeights(r.links.size(), links.substring(0, links.length() - 1));
                        for (TCPSender out : r.nodeRegistry.values()) {
                            out.sendData(message.getBytes());
                        }
                        break;
                    case "start":
                        int rounds = userInput.nextInt();
                        TaskInitiate startMessage = new TaskInitiate(rounds);
                        for (TCPSender out : r.nodeRegistry.values()) {
                            out.sendData(startMessage.getBytes());
                        }
                        break;
                    default:
                        userInput.nextLine();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
