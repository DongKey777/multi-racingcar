package infrastructure.websocket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import infrastructure.websocket.protocol.WebSocketFrame;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class WebSocketFrameTest {

    @Test
    void readsMaskedTextFrame() throws Exception {
        byte[] frame = maskedFrame(0x1, "테스트".getBytes(StandardCharsets.UTF_8));

        assertEquals("테스트", WebSocketFrame.readText(new ByteArrayInputStream(frame)));
    }

    @Test
    void respondsToPingAndThenReadsText() throws Exception {
        byte[] ping = maskedFrame(0x9, "ok".getBytes(StandardCharsets.UTF_8));
        byte[] text = maskedFrame(0x1, "hello".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream input = new ByteArrayOutputStream();
        input.write(ping);
        input.write(text);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertEquals("hello", WebSocketFrame.readText(
                new ByteArrayInputStream(input.toByteArray()), output
        ));
        assertArrayEquals(new byte[]{(byte) 0x8A, 0x02, 'o', 'k'}, output.toByteArray());
    }

    @Test
    void echoesCloseFrame() throws Exception {
        byte[] close = maskedFrame(0x8, new byte[]{0x03, (byte) 0xE8});
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertNull(WebSocketFrame.readText(new ByteArrayInputStream(close), output));
        assertArrayEquals(new byte[]{(byte) 0x88, 0x02, 0x03, (byte) 0xE8}, output.toByteArray());
    }

    @Test
    void writesExtendedLengthTextFrame() throws Exception {
        String message = "a".repeat(130);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        WebSocketFrame.writeText(output, message);

        byte[] frame = output.toByteArray();
        assertEquals((byte) 0x81, frame[0]);
        assertEquals(126, frame[1]);
        assertEquals(130, ((frame[2] & 0xFF) << 8) | (frame[3] & 0xFF));
    }

    private byte[] maskedFrame(int opcode, byte[] payload) throws Exception {
        byte[] mask = new byte[]{1, 2, 3, 4};
        ByteArrayOutputStream frame = new ByteArrayOutputStream();
        frame.write(0x80 | opcode);
        frame.write(0x80 | payload.length);
        frame.write(mask);
        for (int i = 0; i < payload.length; i++) {
            frame.write(payload[i] ^ mask[i % mask.length]);
        }
        return frame.toByteArray();
    }
}
