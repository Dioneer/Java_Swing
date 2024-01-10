package Pegas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionObserver {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8081;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            try {
                new ClientWindow();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Fox");
    private final JTextField fieldInput = new JTextField();
    private TCPConnection connection;
    private ClientWindow() throws IOException {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);
        setVisible(true);
        connection = new TCPConnection(this, IP, PORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals(""))return;
        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText()+" : "+ msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception...");
    }
    private synchronized void printMessage(String msg){
        SwingUtilities.invokeLater(()->{
            log.append(msg+"\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
