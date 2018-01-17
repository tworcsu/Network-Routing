package network.overlay.node;

import network.overlay.transport.TCPSender;
import network.overlay.transport.TCPServer;
import network.overlay.wireformats.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by toddw on 1/22/17.
 */
public abstract class Node {
    public HashMap<String,TCPSender> connectionList;
    protected TCPServer server;
    protected int portNumber;
    protected String hostName;

    public abstract void onEvent(Event e, Socket socket) throws IOException;

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
}
