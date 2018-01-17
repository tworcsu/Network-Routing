package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 2/10/17.
 */
public class NodeMessage implements Event, Protocol{
    private int messageType = NODE_MESSAGE;
    private String route;
    private int payload;
    private byte lastHop;

    public NodeMessage(String route, int payload) {
        this.route = route;
        this.payload = payload;
    }
    public NodeMessage(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        assert(type == messageType);

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);
        route = new String(ipBytes);

        payload = din.readInt();
        baInputStream.close();
        din.close();
    }
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);

        byte[] ipBytes = route.getBytes();
        int ipLength = ipBytes.length;
        dout.writeInt(ipLength);
        dout.write(ipBytes);

        dout.writeInt(payload);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    @Override
    public int getType() {
        return messageType;
    }

    public int getPayload() {
        return payload;
    }

    public String getRoute() {
        return route;
    }
}
