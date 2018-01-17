package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 2/10/17.
 */
public class TrafficSummary implements Event, Protocol {
    private int messageType = TRAFFIC_SUMMARY;
    public String hostName;
    public int portNumber;
    public int numberSent;
    public long sumSent;
    public int numberReceived;
    public long sumReceived;
    public int numberRelayed;

    public TrafficSummary(String ip, int port, int numSent, long sumSent, int numRec, long sumRec, int numRelay) {
       this.hostName = ip;
       this.portNumber = port;
       this.numberSent = numSent;
       this.sumSent = sumSent;
       this.numberReceived = numRec;
       this.sumReceived = sumRec;
       this.numberRelayed = numRelay;
    }
    public TrafficSummary(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        assert(type == messageType);

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);
        hostName = new String(ipBytes);

        portNumber = din.readInt();
        numberSent = din.readInt();
        sumSent = din.readLong();
        numberReceived = din.readInt();
        sumReceived = din.readLong();
        numberRelayed = din.readInt();
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
        dout.writeInt(numberSent);
        dout.writeLong(sumSent);
        dout.writeInt(numberReceived);
        dout.writeLong(sumReceived);
        dout.writeInt(numberRelayed);

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
}
