package sample.server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SharingHandler implements Runnable
{
    private boolean isRunning = false;
    private DatagramSocket socket;
    private InetAddress address;
    private int clientPort;

    public void run()
    {
        try
        {
            while (isRunning)
            {
                BufferedImage bufferedImage = makeScreenShot();
                byte[] image = bufImageToByteArray(bufferedImage);
                sendImage(image);
            }
        }
        catch (AWTException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private BufferedImage makeScreenShot() throws AWTException
    {
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle captureRect = new Rectangle(0, 0, screenSize.width, screenSize.height);
        return resize(robot.createScreenCapture(captureRect), screenSize.width/3, screenSize.height/3);
    }

    private byte[] bufImageToByteArray(BufferedImage bufferedImage) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", out);
        out.flush();
        return out.toByteArray();
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    private void sendImage(byte[] image) throws IOException
    {
        DatagramPacket packet = new DatagramPacket(image, image.length, address, clientPort);
        socket.send(packet);
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public void setRunning(boolean running)
    {
        isRunning = running;
    }

    public void setSocket(DatagramSocket socket)
    {
        this.socket = socket;
    }

    public void setAddress(InetAddress address)
    {
        this.address = address;
    }

    public void setPort(int clientPort)
    {
        this.clientPort = clientPort;
    }
}
