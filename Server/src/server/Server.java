package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import handlers.DataBaseCatalog;
import handlers.SQLiteHandler;


public class Server {

    private static final int PORT = 32456;

    protected ServerSocket ss;
    protected SSLServerSocket connsSocket;
    protected Socket s;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected BufferedReader bfr;
    protected boolean run = true;
    protected ArrayList<ServerHandler> connections = new ArrayList<ServerHandler>();
    protected String liveNews="";
    protected String adminUser;
    protected SQLiteHandler bd = new SQLiteHandler();
    protected DataBaseCatalog dbh;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        try {
            ss = new ServerSocket(PORT);
            dbh = new DataBaseCatalog();
            dbh.initialize();

            while(run) {
                Socket s = ss.accept();
                ServerHandler sh = new ServerHandler(dbh,s,this);
                sh.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Configures serverSocket port to listen to new conn requests
     */
    private void initializeRequestsSocket() {
        try {
            //Security.addProvider(new Provider());
            System.setProperty("javax.net.ssl.KeyStore","photoshare.store");
            System.setProperty("javax.net.ssl.keyStorePassword", "server");

            SSLServerSocketFactory sslSrvFact =(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            connsSocket =(SSLServerSocket)sslSrvFact.createServerSocket(PORT);

            String[] availableCiphers = sslSrvFact.getSupportedCipherSuites();
            //connsSocket.setNeedClientAuth(true);
            connsSocket.setEnabledCipherSuites(availableCiphers);

        } catch (Exception e) {
            //stopServer("Unable to open serverSocket: " + e.getMessage(), true);
        }
    }
}
