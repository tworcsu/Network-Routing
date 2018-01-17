package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 2/1/17.
 */
public class MessagingNodesList implements Event, Protocol {
    private int messageType = MESSAGING_NODES_LIST;
    private int numConnections;
    private String connections;

    public MessagingNodesList(int numConnections, String connections) {
        this.numConnections = numConnections;
        this.connections = connections;
    }

    public MessagingNodesList(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        assert (type == messageType);
        numConnections = din.readInt();

        int infoLength = din.readInt();
        byte[] infoBytes = new byte[infoLength];
        din.readFully(infoBytes);
        connections = new String(infoBytes);

        baInputStream.close();

    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeInt(numConnections);

        byte[] infoBytes = connections.getBytes();
        int infoLength = infoBytes.length;
        dout.writeInt(infoLength);
        dout.write(infoBytes);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    @Override
    public int getType() {
        return this.messageType;
    }

    public String getConnections() {
        return connections;
    }

    public int getNumConnections() {return numConnections;}
}
