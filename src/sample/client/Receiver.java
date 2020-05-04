package sample.client;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver implements Runnable
{
    private final DatagramSocket socket;
    private boolean isWorking;
    private OnReceiveImageListener listener;
    private byte[] image = new byte[70000];

    public Receiver(DatagramSocket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        while (isWorking)
        {
            try
            {
                receiveImage();
            }
            catch (IOException e)
            {
                System.out.println("Socket closed, receiver is not working");
            }
        }
    }

    private void receiveImage() throws IOException
    {
        DatagramPacket packet = new DatagramPacket(image, image.length);
        socket.receive(packet);
        image = packet.getData();
        BufferedImage bufferedImage = createImageFromBytes(image);
        javafx.scene.image.Image screenshot = SwingFXUtils.toFXImage(bufferedImage, null);
        if (listener != null)
        {
            listener.onReceive(screenshot);
        }
    }

    private BufferedImage createImageFromBytes(byte[] imageData)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(imageData);
        try
        {
            return ImageIO.read(in);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setWorking(boolean working)
    {
        isWorking = working;
    }

    public void setListener(OnReceiveImageListener listener)
    {
        this.listener = listener;
    }
}
