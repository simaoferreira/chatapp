package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import handlers.DataBaseCatalog;
import handlers.SQLiteHandler;


public class Server {

    private static final int PORT = 32456;

    protected ServerSocket ss;
    protected Socket s;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected BufferedReader bfr;
    protected boolean run = true;
    protected ArrayList<ServerHandler> connections = new ArrayList<ServerHandler>();
    protected Map<String,String> friendsRequest = new HashMap<String,String>();
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
                Iterator<ServerHandler> iter = connections.iterator();
                while(iter.hasNext()){
                    ServerHandler sh = iter.next();
                    try {
                        sh.sendText("");
                    }catch (IOException e) {
                        sh.updateInfoConnections();
                        iter.remove();
                    }

                }
                Socket s = ss.accept();
                ServerHandler sh = new ServerHandler(dbh,s,this);
                sh.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
