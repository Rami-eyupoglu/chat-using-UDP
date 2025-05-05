package udp_chatapp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Udp_Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private List<ClientList> clients; // List of clients.

    public Udp_Server(int port) throws SocketException {
        socket = new DatagramSocket(port);
        clients = new ArrayList<>();
    }

    public void run() {
        running = true;
        while (running) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                ClientList client = new ClientList(address, port);
                if (!clients.contains(client)) {
                    clients.add(client);
                }

                String received = new String(packet.getData(), 0, packet.getLength());
                String message = "From " + address.toString() + ":" + port + " - " + received;
                ServerFrame.lst_messagesFromClient_model.addElement(message);
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
        socket.close();
    }

    // function to send a message to all clients.
    public void broadcastMessage(String message) {
        byte[] buf = message.getBytes();
        for (ClientList client : clients) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, client.address, client.port);
            try {
                socket.send(packet);
                System.out.println("Broadcast message sent to " + client.address + ":" + client.port);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        ServerFrame.lst_messagesFromClient_model.addElement("You: " + message);
    }

    public void stopServer() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    // class to keep the clients in a list.
    private static class ClientList {

        InetAddress address;
        int port;

        ClientList(InetAddress addr, int prt) {
            address = addr;
            port = prt;
        }
    }
}
