package net.gotzi.minestrum.server.connection;

import lombok.Getter;
import net.gotzi.minestrum.Minestrum;
import net.gotzi.minestrum.api.error.ErrorView;
import net.gotzi.minestrum.api.logging.LogLevel;
import net.gotzi.minestrum.data.packet.Packet;
import net.gotzi.minestrum.data.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public class SocketPipe {

    @Getter
    private final Socket socket;
    private final Logger logger;

    private final InputStream input;
    private final OutputStream output;

    public SocketPipe(Socket socket, Logger logger) throws IOException {
        this.socket = socket;
        this.logger = logger;

        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();

    }

    public <P extends Packet> void write(P packet) throws IOException {
        byte[] bytes = packet.toBytes();
        output.write(packet.getPacketType().size());
        output.write(bytes);
    }

    public Queue<Packet> read() throws IOException {
        Queue<Packet> queue = new LinkedList<>();
        int b;

        while ((b = input.read()) != -1) {
            PacketType packetType = PacketType.getById(b);
            byte[] bytes = new byte[packetType.size()];
            input.read(bytes);

            Packet p = packetType.getConstructor().construct(
                    ByteBuffer.wrap(bytes)
            );

            queue.add(p);
        }

        return queue;
    }

    public void write(byte[] buf) {
        if (socket.isClosed()) return;

        try {
            output.write(buf);
        } catch (IOException e) {
            this.logger.log(LogLevel.ERROR, "Error while writing to channel");
            ErrorView view = new ErrorView("writing channel", e);
            Minestrum.getInstance().getErrorHandler().registerError(view);
        }
    }

    public int read(byte[] buf) {
        if (socket.isClosed()) return -1;

        try {
            return input.read(buf);
        } catch (IOException e) {
            this.logger.log(LogLevel.ERROR, "Error while reading from channel");
            ErrorView view = new ErrorView("reading channel", e);
            Minestrum.getInstance().getErrorHandler().registerError(view);
            return -1;
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
