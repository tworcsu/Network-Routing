package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 2/11/17.
 */
public class ConnectionInitiate implements Protocol, Event{
    private int messageType = CONNECTION_INITIATE;
    private String idString;
    
    public ConnectionInitiate(String idString){
        this.idString = idString;
    }
    
    public ConnectionInitiate(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        assert(type == messageType);

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);
        idString = new String(ipBytes);

        baInputStream.close();
        din.close();
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);

        byte[] ipBytes = idString.getBytes();
        int ipLength = ipBytes.length;
        dout.writeInt(ipLength);
        dout.write(ipBytes);


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

    public String getIdString() {
        return idString;
    }
}
