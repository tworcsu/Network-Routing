package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 1/23/17.
 */
public class RegisterRequest implements Protocol, Event {
    private int messageType = REGISTER_REQUEST;
    public String hostName;
    public int portNumber;

    public RegisterRequest(String ip, int port) {
        hostName = ip;
        portNumber = port;
    }

    public RegisterRequest(byte[] marshalledBytes) throws IOException {
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
        return messageType;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getPortNumber() {
        return this.portNumber;
    }
}
