package Pegas;

import java.io.*;
import java.net.Socket;

public class TCPConnection {
    private final Socket socket;
    private final TCPConnectionObserver eventListener;
    private Thread thread = null;
    private final BufferedReader br;
    private final BufferedWriter bw;
    public TCPConnection(Socket socket, TCPConnectionObserver eventListener) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        thread = new Thread(()->{
            try{
                eventListener.onConnectionReady(TCPConnection.this);
                while(!thread.isInterrupted()){
                    eventListener.onReceiveString(TCPConnection.this, br.readLine());
                }
            } catch (IOException e) {
                eventListener.onException(TCPConnection.this, e);
            } finally {
                eventListener.onDisconnect(TCPConnection.this);
            }
        });
        this.thread.start();
    }
    public TCPConnection(TCPConnectionObserver eventListener, String ip, int port)throws IOException{
        this(new Socket(ip, port), eventListener);
    }
    public synchronized void sendString(String value){
        try {
            bw.write(value + "\r\n");
            bw.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }
    public synchronized void disconnect(){
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection{" +
                "socket=" + socket.getInetAddress() +" : "+ socket.getInetAddress()+ socket.getPort()+
                ", thread=" + thread +
                '}';
    }
}
