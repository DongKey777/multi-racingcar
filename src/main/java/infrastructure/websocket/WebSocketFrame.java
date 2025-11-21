package infrastructure.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class WebSocketFrame {
    private static final byte TEXT_FRAME_OPCODE = (byte) 0x81;
    private static final int PAYLOAD_LENGTH_MASK = 0x7F;
    private static final int MASKING_KEY_LENGTH = 4;

    public static String readText(InputStream in) throws IOException {
        int firstByte = in.read();
        int secondByte = in.read();

        int payloadLength = secondByte & PAYLOAD_LENGTH_MASK;

        byte[] maskingKey = new byte[MASKING_KEY_LENGTH];
        in.read(maskingKey);

        byte[] payload = new byte[payloadLength];
        in.read(payload);

        for (int i = 0; i < payloadLength; i++) {
            payload[i] ^= maskingKey[i % MASKING_KEY_LENGTH];
        }

        return new String(payload, StandardCharsets.UTF_8);
    }

    public static void writeText(OutputStream out, String message) throws IOException {
        byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        int payloadLength = payload.length;

        out.write(TEXT_FRAME_OPCODE);
        out.write(payloadLength);
        out.write(payload);
        out.flush();
    }
}