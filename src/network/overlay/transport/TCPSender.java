package network.overlay.transport;

import network.overlay.wireformats.NodeMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by toddw on 1/22/17.
 */
public class TCPSender implements Runnable{
    private Socket socket;
    private DataOutputStream dout;
    private NodeMessage data;

    public TCPSender(Socket socket) throws IOException {
        this.socket = socket;
        dout = new DataOutputStream(socket.getOutputStream());
        data = null;
    }

    public TCPSender(Socket socket, NodeMessage m) throws IOException {
        this.socket = socket;
        dout = new DataOutputStream(socket.getOutputStream());
        this.data = data;
    }

    public String getHost () {
        return socket.getInetAddress().toString();
    }

    public int getPort() {
        return socket.getPort();
    }

    public void closeSocket() throws IOException {
        socket.close();
    }

    public synchronized void sendData(byte[] dataToSend) throws IOException {
        int dataLength = dataToSend.length; dout.writeInt(dataLength);
        dout.write(dataToSend, 0, dataLength);
        dout.flush();
    }

    @Override
    public void run() {
        try {
            sendData(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Socket getSocket() {
        return socket;
    }
}
