package DataHandler;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;

public class Notifications {
	
	public void displayTray(String username) throws AWTException, MalformedURLException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        ImageIcon icon = new ImageIcon("logo.png");
        Image image = icon.getImage();
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("Power Of Gems");
        tray.add(trayIcon);

        trayIcon.displayMessage("Welcome!", "Welcome to our chat " + username, MessageType.INFO);
    }
}
