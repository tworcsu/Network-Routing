package network.overlay.wireformats;

import java.io.IOException;

/**
 * Created by toddw on 1/23/17.
 */
public interface Event {
    byte[] getBytes() throws IOException;

    int getType();
}
