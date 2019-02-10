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

    protected ServerSocket             ss;
    protected Socket                   s;
    protected DataInputStream          dis;
    protected DataOutputStream         dos;
    protected BufferedReader           bfr;
    protected boolean                  run = true;
    protected ArrayList<ServerHandler> connections = new ArrayList<ServerHandler>();
    protected Map<String,String>       friendsRequest = new HashMap<String,String>();
    protected String                   liveNews="";
    protected String                   adminUser;
    protected SQLiteHandler            bd = new SQLiteHandler();
    protected DataBaseCatalog          dbh;
    protected LoggerHandle             lh = null;

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
