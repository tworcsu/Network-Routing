package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 2/10/17.
 */
public class TaskComplete implements Event, Protocol {
    private int messageType = TASK_COMPLETE;
    public String hostName;
    public int portNumber;

    public TaskComplete(String ip, int port) {
        hostName = ip;
        portNumber = port;
    }
    public TaskComplete(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        assert(type == messageType);

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);
        hostName = new String(ipBytes);

        portNumber = din.readInt();
        baInputStream.close();
        din.close();
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);

        byte[] ipBytes = hostName.getBytes();
        int ipLength = ipBytes.length;
        dout.writeInt(ipLength);
        dout.write(ipBytes);

        dout.writeInt(portNumber);

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
    public String getHostName() {
        return this.hostName;
    }

    public int getPortNumber() {
        return this.portNumber;
    }
}
