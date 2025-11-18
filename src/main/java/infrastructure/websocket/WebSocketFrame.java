package infrastructure.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class WebSocketFrame {

    public static String readText(InputStream in) throws IOException {
        int b1 = in.read();
        int b2 = in.read();

        int len = b2 & 0x7F;

        byte[] key = new byte[4];
        in.read(key);

        byte[] data = new byte[len];
        in.read(data);

        for (int i = 0; i < len; i++) {
            data[i] ^= key[i % 4];
        }

        return new String(data, StandardCharsets.UTF_8);
    }

    public static void writeText(OutputStream out, String message) throws IOException {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        int len = data.length;

        out.write(0x81);
        out.write(len);
        out.write(data);
        out.flush();
    }
}