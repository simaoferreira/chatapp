package handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLWebServerHandler {
	private static Connection con;
	private static boolean hasData = false;
	private static String url = "jdbc:mysql://212.1.211.88:3306/risingse_pog_home";  
	private static String user = "risingse_user";//Username of database  
	private static String pass = "1b2c3d4e1b2c3d4e";//Password of database  
	
	public ResultSet displayUsers() throws ClassNotFoundException, SQLException {
		if(con == null) {
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT username,password, exp, lvl, money, gems FROM user");
		return res;
	}

	private void getConnection() throws ClassNotFoundException, SQLException {
				
	    try{  
	        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();  
	      }catch(ClassNotFoundException cnfe){  
	        System.err.println("Error: "+cnfe.getMessage());  
	      }catch(InstantiationException ie){  
	        System.err.println("Error: "+ie.getMessage());  
	      }catch(IllegalAccessException iae){  
	        System.err.println("Error: "+iae.getMessage());  
	      }  
	      con = DriverManager.getConnection(url,user,pass);  
	}

	
	public boolean estaRegistado(String username,String password) throws ClassNotFoundException, SQLException {
		
		boolean result = false;
		
		if(con == null) {
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT username,password FROM players WHERE BINARY username='" + username + "' AND BINARY password='"+ password +"'");
		
		
		if(res.next()) {
			result=true;
			
		}
		
		return result;

		
	}

	public void getInfo(String user,String password) throws ClassNotFoundException, SQLException {
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT * FROM players WHERE username='" + user + "'");
		ResultSetMetaData rsmd = res.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (res.next()) {
		    for (int i = 1; i <= columnsNumber; i++) {
		        if (i > 1) System.out.print(",  ");
		        String columnValue = res.getString(i);
		        System.out.print(columnValue);
		    }

		}
		
	}
	
	public String getLvlUser(String user) {
		
		String result = null;
		
		try {
			Statement state = con.createStatement();
			ResultSet res = state.executeQuery("SELECT lvl FROM players WHERE BINARY username='" + user + "'");
			ResultSetMetaData rsmd = res.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (res.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
			         result= res.getString(i);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Erro a encontrar LVL do user");
		}
		return result;
	}
	
	public String getMoneyUser(String user) {
		
		String result = null;
		
		try {
			Statement state = con.createStatement();
			ResultSet res = state.executeQuery("SELECT money FROM players WHERE BINARY username='" + user + "'");
			ResultSetMetaData rsmd = res.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (res.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
			         result= res.getString(i);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Erro a encontrar tickets do user");
		}
		return result;
	}
	
	public String getGemsUser(String user) {
		
		String result = null;
		
		try {
			Statement state = con.createStatement();
			ResultSet res = state.executeQuery("SELECT gems FROM players WHERE BINARY username='" + user + "'");
			ResultSetMetaData rsmd = res.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (res.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
			         result= res.getString(i);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Erro a encontrar gems do user");
		}
		return result;
	}	
	
	
}
