package network.overlay.transport;

import network.overlay.node.Node;
import network.overlay.wireformats.Event;
import network.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;

/**
 * Created by toddw on 1/22/17.
 */
public class TCPReceiverThread extends Observable implements Runnable {
    private Socket socket;
    private Node node;
    private DataInputStream din;

    public TCPReceiverThread(Socket socket, Node node) throws IOException {
        this.socket = socket;
        this.node = node;
        din = new DataInputStream(socket.getInputStream());
    }

    public void run() {
        int dataLength;
        while (socket != null) {
            try {
                dataLength = din.readInt();
                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
                // create an event
                EventFactory factory = EventFactory.getInstance();
                Event e = factory.createEvent(data);
                node.onEvent(e,socket);

            } catch (SocketException se) {
                System.out.println(se.getMessage());
                break;
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                break;
            }
        }
    }

    public Socket getSocket() {
        return this.socket;
    }
}
