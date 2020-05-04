package sample.server;

import sample.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server
{
    private static DatagramSocket socket;
    private static boolean running = true;
    private static final byte[] buf = new byte[256];
    private static SharingHandler sharingHandler;
    private static Thread sharingThread;
    private static InetAddress address;
    private static int clientPort;

    public static void main(String[] args)
    {
        try
        {
            socket = new DatagramSocket(Constants.PORT);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        sharingHandler = new SharingHandler();
        while (running)
        {
            try
            {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                String received = receiveMessage(packet);
                if (received.equals("START SHARING") && !sharingHandler.isRunning())
                {
                    startSharing();
                    continue;
                }
                if (received.equals("STOP SHARING") && sharingHandler.isRunning())
                {
                    stopSharing();
                    continue;
                }
                if (received.equals("END"))
                {
                    running = false;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    private static String receiveMessage(DatagramPacket packet) throws IOException
    {
        socket.receive(packet);
        address = packet.getAddress();
        clientPort = packet.getPort();
        return new String(packet.getData(), 0, packet.getLength());
    }

    private static void startSharing()
    {
        sharingHandler.setSocket(socket);
        sharingHandler.setAddress(address);
        sharingHandler.setPort(clientPort);
        sharingHandler.setRunning(true);
        sharingThread = new Thread(sharingHandler);
        sharingThread.start();
    }

    private static void stopSharing()
    {
        sharingHandler.setRunning(false);
        sharingThread.interrupt();
    }
}
