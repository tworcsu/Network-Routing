package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 2/8/17.
 */
public class LinkWeights implements Event, Protocol {
    private int messageType = LINK_WEIGHTS;
    private int numberOfLinks;
    private String links;

    public LinkWeights(int n, String links) {
        numberOfLinks = n;
        this.links = links;
    }
    public LinkWeights(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        assert (type == messageType);
        numberOfLinks = din.readInt();

        int infoLength = din.readInt();
        byte[] infoBytes = new byte[infoLength];
        din.readFully(infoBytes);
        links = new String(infoBytes);

        baInputStream.close();

    }
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeInt(numberOfLinks);

        byte[] infoBytes = links.getBytes();
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

    public String getLinks() {
        return this.links;
    }
}
