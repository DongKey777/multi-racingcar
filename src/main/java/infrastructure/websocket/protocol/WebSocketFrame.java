package infrastructure.websocket.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class WebSocketFrame {
    private static final int TEXT_OPCODE = 0x1;
    private static final int CLOSE_OPCODE = 0x8;
    private static final int PING_OPCODE = 0x9;
    private static final int PONG_OPCODE = 0xA;
    private static final int FIN_AND_OPCODE_MASK = 0x8F;
    private static final int MASK_FLAG = 0x80;
    private static final int PAYLOAD_LENGTH_MASK = 0x7F;
    private static final int MASKING_KEY_LENGTH = 4;
    private static final int MAX_CLIENT_PAYLOAD_LENGTH = 64 * 1024;

    public static String readText(InputStream in) throws IOException {
        return readText(in, null);
    }

    public static String readText(InputStream in, OutputStream out) throws IOException {
        while (true) {
            Frame frame = readFrame(in);
            if (frame == null) {
                return null;
            }

            if (frame.opcode == TEXT_OPCODE) {
                return new String(frame.payload, StandardCharsets.UTF_8);
            }
            if (frame.opcode == CLOSE_OPCODE) {
                if (out != null) {
                    writeFrame(out, CLOSE_OPCODE, frame.payload);
                }
                return null;
            }
            if (frame.opcode == PING_OPCODE && out != null) {
                writeFrame(out, PONG_OPCODE, frame.payload);
            }
        }
    }

    private static Frame readFrame(InputStream in) throws IOException {
        int firstByte = in.read();
        if (firstByte == -1) {
            return null;
        }
        int secondByte = readRequiredByte(in);
        int opcode = firstByte & 0x0F;
        boolean masked = (secondByte & MASK_FLAG) != 0;
        long payloadLength = secondByte & PAYLOAD_LENGTH_MASK;

        if (payloadLength == 126) {
            payloadLength = ((long) readRequiredByte(in) << 8) | readRequiredByte(in);
        } else if (payloadLength == 127) {
            payloadLength = 0;
            for (int i = 0; i < 8; i++) {
                payloadLength = (payloadLength << 8) | readRequiredByte(in);
            }
        }
        if (payloadLength > MAX_CLIENT_PAYLOAD_LENGTH) {
            throw new IOException("WebSocket payload too large");
        }

        byte[] maskingKey = masked ? readExactly(in, MASKING_KEY_LENGTH) : new byte[0];
        byte[] payload = readExactly(in, (int) payloadLength);
        if (masked) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= maskingKey[i % MASKING_KEY_LENGTH];
            }
        }
        return new Frame(opcode, payload);
    }

    public static void writeText(OutputStream out, String message) throws IOException {
        byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        writeFrame(out, TEXT_OPCODE, payload);
    }

    private static void writeFrame(OutputStream out, int opcode, byte[] payload) throws IOException {
        out.write(0x80 | (opcode & FIN_AND_OPCODE_MASK));
        if (payload.length <= 125) {
            out.write(payload.length);
        } else if (payload.length <= 0xFFFF) {
            out.write(126);
            out.write((payload.length >>> 8) & 0xFF);
            out.write(payload.length & 0xFF);
        } else {
            out.write(127);
            long payloadLength = payload.length;
            for (int shift = 56; shift >= 0; shift -= 8) {
                out.write((int) ((payloadLength >>> shift) & 0xFF));
            }
        }
        out.write(payload);
        out.flush();
    }

    private static int readRequiredByte(InputStream in) throws IOException {
        int value = in.read();
        if (value == -1) {
            throw new IOException("Unexpected end of WebSocket frame");
        }
        return value;
    }

    private static byte[] readExactly(InputStream in, int length) throws IOException {
        byte[] bytes = in.readNBytes(length);
        if (bytes.length != length) {
            throw new IOException("Unexpected end of WebSocket frame");
        }
        return bytes;
    }

    private record Frame(int opcode, byte[] payload) {
    }
}
