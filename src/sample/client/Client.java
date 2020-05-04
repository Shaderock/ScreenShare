package sample.client;

import sample.Constants;

import java.io.IOException;
import java.net.*;

public class Client
{
    private DatagramSocket socket;
    private InetAddress address;
    private Thread receiverThread;
    private Receiver receiver;
    private byte[] buf;

    public Client()
    {
        try
        {
            socket = new DatagramSocket();
            receiver = new Receiver(socket);
            address = InetAddress.getByName("localhost");
        }
        catch (SocketException | UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    public void switchSharing(boolean sharing) throws IOException
    {
        if (sharing)
        {
            startSharing();
        }
        else
        {
            stopSharing();
        }
    }

    private void startSharing() throws IOException
    {
        buf = "START SHARING".getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Constants.PORT);
        socket.send(packet);

        receiver.setWorking(true);
        receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    private void stopSharing() throws IOException
    {
        receiver.setWorking(false);
        buf = "STOP SHARING".getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Constants.PORT);
        socket.send(packet);
        receiverThread.interrupt();
    }

    public void setListener(OnReceiveImageListener listener)
    {
        this.receiver.setListener(listener);
    }

    public void disableConnection() throws IOException
    {
        buf = "END".getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Constants.PORT);
        socket.send(packet);
        receiver.setWorking(false);
        receiverThread.interrupt();
        socket.close();
    }
}
