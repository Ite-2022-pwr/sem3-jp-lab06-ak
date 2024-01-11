package ite.jp.ak.lab06.utils.network;


import ite.jp.ak.lab06.utils.model.Packet;
import ite.jp.ak.lab06.utils.model.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Sender {
    public void sendTo(User receiver, Packet packet) throws IOException {
        var host = receiver.getHost();
        var port = receiver.getPort();

        var client = SocketChannel.open();
        client.connect(new InetSocketAddress(host, port));

        var data = ByteBuffer.wrap(packet.encode().getBytes(StandardCharsets.UTF_8));
        client.write(data);
        client.close();
    }
}
