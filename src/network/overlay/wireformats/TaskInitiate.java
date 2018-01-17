package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 2/10/17.
 */
public class TaskInitiate implements Event, Protocol {
    private int messageType = TASK_INITIATE;
    private int rounds;
    public TaskInitiate(int rounds) {
        this.rounds = rounds;
    }
    public TaskInitiate(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        assert (type == messageType);
        rounds = din.readInt();

        baInputStream.close();
    }


    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeInt(rounds);

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

    public int getRounds() {
        return rounds;
    }
}
