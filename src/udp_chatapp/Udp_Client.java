package udp_chatapp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.SwingUtilities;

public class Udp_Client {
    private  byte[] data;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private boolean islistening = false;  

    public Udp_Client() {
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setServer(String address, int port) {
        try {
            this.serverAddress = InetAddress.getByName(address);
            this.serverPort = port;
        } catch (UnknownHostException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void sendMessage(String message) {
        Runnable sendTask = () -> {
            try {
                data = message.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
                socket.send(packet);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        };
        new Thread(sendTask).start();
    }

    public void listen() {
    islistening = true;
    Thread listenThread = new Thread(() -> {
        while (islistening) {
            byte[] incomingMsg = new byte[1024];
            DatagramPacket packet = new DatagramPacket(incomingMsg, incomingMsg.length);
            try {
                socket.receive(packet);//blocking
                String receivedMessage = new String(packet.getData(), 0, packet.getLength()).trim();
                ClientFrame.lst_messagesFromServer_model.addElement("server: " + receivedMessage);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                islistening = false;
                close();
            }
        }
    });
    listenThread.start();
}


    public void stopListening() {
        islistening = false;
    }

    public void close() {
        stopListening();
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
