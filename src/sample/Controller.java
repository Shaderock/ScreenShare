package sample;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import sample.client.Client;
import sample.client.OnReceiveImageListener;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable, OnReceiveImageListener
{
    public Button disableConnectionBtn;
    private Client client;
    public ImageView image;
    public ToggleButton receiveScreenShareBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        client = new Client();
    }

    public void toggleSharing(MouseEvent mouseEvent)
    {
        if (receiveScreenShareBtn.isSelected())
        {
            client.setListener(this);
        }
        else
        {
            client.setListener(null);
        }
        try
        {
            client.switchSharing(receiveScreenShareBtn.isSelected());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Image screenshot)
    {
        image.setImage(screenshot);
    }

    public void disableConnection(MouseEvent mouseEvent)
    {
        try
        {
            client.disableConnection();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
