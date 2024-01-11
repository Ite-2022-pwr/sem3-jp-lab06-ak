package ite.jp.ak.lab06.utils.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import ite.jp.ak.lab06.utils.model.Packet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Listener {
    private final String host;
    private final Integer port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void start() throws IOException {
        try(var selector = Selector.open()){
            try(var listenerChannel = ServerSocketChannel.open()) {
                listenerChannel.bind(new InetSocketAddress(host, port));
                listenerChannel.configureBlocking(false);
                listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
                System.out.println("Listening on " + host + ":" + port);

                while (true) {
                    try {
                        selector.select();

                        for (SelectionKey selectedChannelKey : selector.selectedKeys()) {
                            // akceptowanie nowego połączenia
                            if (selectedChannelKey.isAcceptable()) {
                                var client = listenerChannel.accept();
                                System.out.println("New connection");
                                if (client != null) {
                                    client.configureBlocking(false);
                                    client.register(selector, SelectionKey.OP_READ);
                                }
                            }

                            // odczyt danych z gniazda
                            if (selectedChannelKey.isReadable()) {
                                try (SocketChannel clientChannel = (SocketChannel) selectedChannelKey.channel()) {
                                    ByteBuffer buffer = ByteBuffer.allocate(4096);

                                    if (clientChannel.read(buffer) == -1) {
                                        selectedChannelKey.cancel();
                                        continue;
                                    }

                                    var data = new String(buffer.array(), StandardCharsets.UTF_8).trim();
                                    Packet packet = Packet.fromString(data);
                                    try {
                                        // przetwarzanie pakietu
                                        processPacket(packet);
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public abstract void processPacket(Packet packet);

}
