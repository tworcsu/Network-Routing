package network.overlay.transport;


import network.overlay.node.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by toddw on 1/23/17.
 */
public class TCPServer implements Runnable {
    private int portNumber;
    private Node node;
    private ServerSocket serverSocket;

    public TCPServer(Node node) throws IOException {
        serverSocket = new ServerSocket(0);
        portNumber = serverSocket.getLocalPort();
        this.node = node;
        node.setPortNumber(serverSocket.getLocalPort());

    }

    public TCPServer(Node node, int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        this.portNumber = portNumber;
        this.node = node;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket connection = serverSocket.accept();
                TCPReceiverThread receiver = new TCPReceiverThread(connection, node);
                Thread t = new Thread(receiver);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
