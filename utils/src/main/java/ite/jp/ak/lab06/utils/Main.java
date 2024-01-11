package ite.jp.ak.lab06.utils;

import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.enums.Role;
import ite.jp.ak.lab06.utils.model.Packet;
import ite.jp.ak.lab06.utils.model.Payload;
import ite.jp.ak.lab06.utils.model.User;
import ite.jp.ak.lab06.utils.network.Sender;

import java.io.IOException;
import java.net.UnknownHostException;

/*
- wybór które produkty z zamówienia zwrócić

 */

public class Main {

    private static final int PORT = 1337;
    private static final String SERVER = "localhost";

    public static void main(String[] args) {
        try {

            var user = new User();
            user.setHost("localhost");
            user.setPort(2137);
            user.setRole(Role.Customer);

            Payload payload = Payload.fromObject(user);

            Packet pkt = new Packet();
            pkt.setType(PacketType.RegisterRequest);
            pkt.setSender(user);
            pkt.setPayload(payload);

            var keeperUser = new User();
            keeperUser.setHost(SERVER);
            keeperUser.setPort(PORT);

            var sender = new Sender();
            sender.sendTo(keeperUser, pkt);

//            var packet = Packet.fromString(response);
//            System.out.println();
//            System.out.println(packet);
//            System.out.println(packet.getPayload().decodeData(packet.getPayload().getType()));

//            clientSocket.close();
        } catch (UnknownHostException ex) {
            System.out.println("Unable to connect" + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }
}
