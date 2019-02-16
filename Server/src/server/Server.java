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
import handlers.LoggerHandle;
import handlers.SQLiteHandler;


public class Server {

    private static final int PORT = 32456;

    protected ServerSocket             ss = null;
    protected Socket                   s = null;
    protected DataInputStream          dis = null;
    protected DataOutputStream         dos = null;
    protected BufferedReader           bfr = null;
    protected boolean                  run = true;
    protected ArrayList<ServerHandler> connections = new ArrayList<ServerHandler>();
    protected DataBaseCatalog          dbh = null;
    protected LoggerHandle             lh = null;
    protected String                   version = "0.5.5";

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
    	lh = new LoggerHandle();
    	lh.log("INFO", "Server restarted;");
    	
        try {
        	//criar server socket
            ss = new ServerSocket(PORT);
            
            //inicializar catalogo da base de dados
            dbh = new DataBaseCatalog(lh);
            dbh.initialize();
            lh.log("INFO", "Server initialized;");
            lh.log("INFO", "Server started to wait for new connections;");
            while(run) {
                Socket s = ss.accept();
                lh.log("INFO", "New connection was accepted: "+ s.getRemoteSocketAddress().toString());
                ServerHandler sh = new ServerHandler(dbh,lh,s,this);
                sh.start();
            }
            
        } catch (Exception e) {
            lh.log("WARNING","Could not initialize server!",e);
        }
    }

}
