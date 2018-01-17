package network.overlay.node;

import network.overlay.dijkstra.Graph;
import network.overlay.dijkstra.ShortestPaths;
import network.overlay.dijkstra.Vertex;
import network.overlay.transport.TCPReceiverThread;
import network.overlay.transport.TCPSender;
import network.overlay.transport.TCPServer;
import network.overlay.wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

import static network.overlay.wireformats.Protocol.*;

/**
 * Created by toddw on 1/22/17.
 */

public class MessagingNode extends Node {
    private static final int MESSAGES_PER_ROUND = 5;

    private Socket registrySocket;
    private TCPSender registrySender;
    private Random rand;

    private int sendTracker;
    private int receiveTracker;
    private int relayTracker;
    private long sendSummation;
    private long receiveSummation;
    private ArrayList<String> routingPaths;
    private ShortestPaths shortest;

    public MessagingNode(String hostName, int portNumber) {
        connectionList = new HashMap<String, TCPSender>();
        routingPaths = new ArrayList<String>();
        rand = new Random();
        try {
            server = new TCPServer(this);
            this.hostName = InetAddress.getLocalHost().getHostAddress();
            registrySocket = new Socket(hostName, portNumber);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        this.resetTrackers();

    }

    private void resetTrackers() {
        this.sendTracker = 0;
        this.receiveTracker = 0;
        this.relayTracker = 0;
        this.sendSummation = 0;
        this.receiveSummation = 0;
    }

    public void addConnection(String connection) throws IOException {
        if (!connectionList.containsKey(connection) && connection != null) {
            String[] components = connection.split(":");
            String hostname = components[0];
            int portNumber = Integer.parseInt(components[1]);
            Socket s = new Socket(hostname, portNumber);
            this.connectionList.put(connection, new TCPSender(s));
        }

    }


    public void onEvent(Event e, Socket socket) throws IOException{
        int type = e.getType();
        switch (type) {
            case REGISTER_RESPONSE:
                RegisterResponse response = (RegisterResponse) e;
                System.out.println(response.getInfo());
                break;
            case DEREGISTER_RESPONSE:
                DeregisterResponse dResponse = (DeregisterResponse) e;
                System.out.println(dResponse.getInfo());
                if (dResponse.getStatusCode() == SUCCESS) {
                    registrySocket.close();
                    System.exit(0);
                    break;
                }
                break;
            case MESSAGING_NODES_LIST:
                MessagingNodesList connectionList = (MessagingNodesList) e;
                ArrayList<String> connections = new ArrayList<>(Arrays.asList(connectionList.getConnections().split(" ")));
                for (String c : connections) {
                    this.addConnection(c);
                    ConnectionInitiate conn = new ConnectionInitiate(this.hostName + ":" + this.portNumber);
                    TCPSender out = this.connectionList.get(c);
                    out.sendData(conn.getBytes());
                }
                int n = connectionList.getNumConnections();
                System.out.printf("All connections are established. Number of connections: %d\n", n);
                break;
            case CONNECTION_INITIATE:
                ConnectionInitiate toAdd = (ConnectionInitiate) e;
                this.addConnection(toAdd.getIdString());
                break;
            case LINK_WEIGHTS:
                LinkWeights weightList = (LinkWeights) e;
                System.out.println(weightList.getLinks());
                this.shortest = new ShortestPaths(new Graph(weightList.getLinks()));
                shortest.computePaths(new Vertex(this.hostName + ":"+ this.portNumber));
                System.out.println("Link weights are received and processed. Ready to send messages.");
                this.routingPaths = shortest.getPaths();

                break;
            case TASK_INITIATE:
                    TaskInitiate initiate = (TaskInitiate) e;
                    for (int i = 0; i < initiate.getRounds(); i++) {
                        String randomRoute = this.routingPaths.get(rand.nextInt(this.routingPaths.size()));
                        int index = randomRoute.indexOf(' ');
                        String nextHop;
                        if (index < 0) {
                            nextHop = randomRoute;
                            randomRoute = "";
                        }
                        else {
                            nextHop = randomRoute.substring(0, index);
                            randomRoute = randomRoute.substring(index + 1);
                        }
                        for (int j = 0; j < MESSAGES_PER_ROUND; j++) {
                            int payload = this.rand.nextInt();
                            NodeMessage mess = new NodeMessage(randomRoute, payload);
                            this.connectionList.get(nextHop).sendData(mess.getBytes());
                            this.updateSend(payload);
                        }
                    }
                System.out.println("Total sent: " + this.sendTracker);
                System.out.println("Total sum sent: " + this.sendSummation);
                TaskComplete done = new TaskComplete(this.hostName, this.portNumber);
                this.registrySender.sendData(done.getBytes());
                break;
            case NODE_MESSAGE:
                    NodeMessage rec = (NodeMessage) e;
                    String route = rec.getRoute();
                    int payload = rec.getPayload();
                    //if route is empty string we are destination
                    if (route.length() == 0)
                        this.updateReceive(payload);
                        // else relay it
                    else {
                        this.relayTracker++;
                        int index = route.indexOf(' ');
                        String nextHop;
                        if (index < 0) {
                            nextHop = route;
                            route = "";
                        } else {
                            nextHop = route.substring(0, index);
                            route = route.substring(index + 1);
                        }
                        NodeMessage relayMessage = new NodeMessage(route, payload);
                        this.connectionList.get(nextHop).sendData(relayMessage.getBytes());
                    }
                    break;
            case PULL_TRAFFIC_SUMMARY:
                System.out.println("Number received: " + this.receiveTracker);
                System.out.println("Sum received: " + this.receiveSummation);
                TrafficSummary summary = new TrafficSummary(this.hostName, this.portNumber, sendTracker,
                        sendSummation, receiveTracker, receiveSummation, relayTracker);
                this.registrySender.sendData(summary.getBytes());
                this.resetTrackers();
                break;
            default:
                System.out.printf("Received an event, but not the one I wanted. Event type: %d\n", type);
                break;
        }
    }

    private void updateSend(int payload) {
        this.sendTracker++;
        this.sendSummation += payload;
    }

    public synchronized void updateReceive(int payload) {
        this.receiveTracker++;
        this.receiveSummation += payload;
    }

    public synchronized void updateRelay() {
        this.relayTracker++;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: MessagingNode [hostName] [portNumber]");
            System.exit(1);
        }
        try {
            MessagingNode m = new MessagingNode(args[0], Integer.parseInt(args[1]));
            Thread t = new Thread(m.server);
            t.start();
            RegisterRequest request = new RegisterRequest(m.hostName, m.portNumber);
            m.registrySender = new TCPSender(m.registrySocket);
            TCPReceiverThread registryReceiver = new TCPReceiverThread(m.registrySocket, m);
            Thread registryReceiverThread = new Thread(registryReceiver);
            registryReceiverThread.start();
            m.registrySender.sendData(request.getBytes());

            // foreground commands
            Scanner userInput = new Scanner(System.in);
            String input;
            while ((input = userInput.nextLine()) != null) {
                switch (input) {
                    // specified commands
                    case "print-shortest-path":
                        System.out.println("Here are the paths");
                        m.shortest.printPaths();
                        break;
                    case "exit-overlay":
                        DeregisterRequest deregisterRequest = new DeregisterRequest(m.hostName, m.portNumber);
                        m.registrySender.sendData(deregisterRequest.getBytes());
                        break;

                    // debug commands
                    case "register": //Attempts to re-register
                        m.registrySender.sendData(request.getBytes());
                        System.out.println("Attempting to re-register");
                        break;
                    case "deregister": //Test deregistering twice
                        DeregisterRequest deregisterRequest2 = new DeregisterRequest(m.hostName, m.portNumber);
                        m.registrySender.sendData(deregisterRequest2.getBytes());
                        break;
                    case "list-connections":
                        System.out.println(m.connectionList);
                        break;
                    case "send-round":
                        for (TCPSender out : m.connectionList.values()) {
                            int payload = m.rand.nextInt();
                            NodeMessage mess = new NodeMessage("", payload);
                            out.sendData(mess.getBytes());
                            m.updateSend(payload);
                        }
                        break;
                    default:

                        break;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
