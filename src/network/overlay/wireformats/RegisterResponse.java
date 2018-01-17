package network.overlay.wireformats;

import java.io.*;

/**
 * Created by toddw on 1/23/17.
 */
public class RegisterResponse implements Protocol, Event {
    private int messageType = REGISTER_RESPONSE;
    private byte statusCode;
    private String additionalInfo;

    public RegisterResponse(byte status, String info) {
        statusCode = status;
        additionalInfo = info;
    }

    public RegisterResponse(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        assert (type == messageType);
        statusCode = din.readByte();

        int infoLength = din.readInt();
        byte[] infoBytes = new byte[infoLength];
        din.readFully(infoBytes);
        additionalInfo = new String(infoBytes);

        baInputStream.close();
        din.close();
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeByte(statusCode);

        byte[] infoBytes = additionalInfo.getBytes();
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
        return messageType;
    }

    public String getInfo() {
        return additionalInfo;
    }
}
