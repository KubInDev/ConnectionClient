import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    public Client(String host) {
        super("Client mofo!");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent event) {
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }

    public void startRunning() {
        try {
            connectToServer();
            setUpStreams();
            whileChatting();
        }catch (EOFException eofException) {
            showMessage("\n Client terminated connection");
        }catch(IOException ioException) {
            ioException.printStackTrace();
        }finally {
            closeCrap();
        }
    }

    private void connectToServer() throws IOException {
        showMessage("Attempting connection...");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());
    }

    private void setUpStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are set up ! \n");
    }

    private void whileChatting() throws IOException {
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n " + message);
            }catch(ClassNotFoundException classNotFoundException) {
                showMessage("\n Idk this shity object");
            }
        } while(!message.equals(("SERVER - END")));
    }

    private void closeCrap() {
        showMessage("\n closing crap down...");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void sendMessage(String message){
        try{
            output.writeObject("Kuba -" + message);
            output.flush();
            showMessage("\nKuba - " + message);
        }catch(IOException ioException){
            chatWindow.append(("\n something fucked up while sending"));
        }

    }

    private void showMessage(final String m) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        chatWindow.append(m);
                    }
                }
        );
    }

    private void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );
    }
}
