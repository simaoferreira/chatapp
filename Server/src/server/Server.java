package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import handlers.DataBaseCatalog;
import handlers.SQLiteHandler;


public class Server {
	
	private static final int PORT = 32456;
	

	ServerSocket ss;
	Socket s;
	DataInputStream dis;
	DataOutputStream dos;
	BufferedReader bfr;
	boolean run = true;
	ArrayList<ServerHandler> connections = new ArrayList<ServerHandler>();
	String liveNews="";
	String adminUser;
	SQLiteHandler bd = new SQLiteHandler();
	DataBaseCatalog dbh;
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
