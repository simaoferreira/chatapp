package handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

public class SQLiteHandler {
    private static Connection con;
    private static boolean hasData = false;
    private static Statement state;
    private static final String DB_URL = "jdbc:sqlite:chatapp.db";  
    private static final String DRIVER = "org.sqlite.JDBC";  

    private static void getConnection() throws ClassNotFoundException {  
        Class.forName(DRIVER);  
        Connection connection = null;  
        try {  
            SQLiteConfig config = new SQLiteConfig();  
            config.enforceForeignKeys(true);  
            connection = DriverManager.getConnection(DB_URL,config.toProperties());  
        } catch (SQLException ex) {}  
        con = connection;  
    }

    protected void initialize() throws SQLException, ClassNotFoundException {

        if(con == null) {
            getConnection();
        }

        if(!hasData) {
            hasData=true;

            state = con.createStatement();
            ResultSet resTabelaUsersConfig = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'");

            if( !resTabelaUsersConfig.next()) {
                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE users(id integer,"
                        + "username varchar(12) unique,"
                        + "password varchar(12),"
                        + "primary key(id));");
            }

            ResultSet resTabelaUsersInfo = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='usersInfo'");
            if( !resTabelaUsersInfo.next()) {
                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE usersInfo(id integer,"
                        + "userLvl integer,"
                        + "userExp integer,"
                        + "messagesSent integer,"
                        + "wordsWritten integer,"
                        + "foreign key(id) references users(id),"
                        + "primary key(id));");
            }
            
            ResultSet resTabelaUsersFriends = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='usersFriends'");
            if( !resTabelaUsersFriends.next()) {
                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE usersFriends(id integer,"
                        + "idUser integer,"
                        + "idFriend integer,"
                        + "foreign key(idUser) references users(id),"
                        + "foreign key(idFriend) references users(id),"
                        + "primary key(id));");
            }
            
            ResultSet resTabelaUsersRequestsFriends = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='usersRequestsFriends'");
            if( !resTabelaUsersRequestsFriends.next()) {
                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE usersRequestsFriends(id integer,"
                        + "idUser integer,"
                        + "idRequestedFriend integer,"
                        + "foreign key(idUser) references users(id),"
                        + "foreign key(idRequestedFriend) references users(id),"
                        + "primary key(id));");
            }
        }

    }

    protected void addUser(String username,String password) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        if(!estaRegistado(username)){
            PreparedStatement prep = con.prepareStatement("INSERT INTO users values(?,?,?);");
            prep.setString(2, username);
            prep.setString(3, password);
            prep.execute();

            PreparedStatement prepInfo = con.prepareStatement("INSERT INTO usersInfo values(?,?,?,?,?)");
            prepInfo.setInt(2, 1);
            prepInfo.setInt(3, 0);
            prepInfo.setInt(4, 0);
            prepInfo.setInt(5, 0);
            prepInfo.execute();
        }else {
            System.err.println("já esta registado");
        }

    }
    
    protected void addRequestFriend(int username,int targetRequest) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }
        
        PreparedStatement prep = con.prepareStatement("INSERT INTO usersRequestsFriends values(?,?,?);");
        prep.setInt(2, username);
        prep.setInt(3, targetRequest);
        prep.execute();
    }
    
    protected void addFriend(int username,int intFriend) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }
        
        PreparedStatement prep = con.prepareStatement("INSERT INTO usersFriends values(?,?,?);");
        prep.setInt(2, username);
        prep.setInt(3, intFriend);
        prep.execute();
        
    }

    protected void removeUser(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        PreparedStatement prepInfo = con.prepareStatement("DELETE FROM usersInfo WHERE id = ?");
        prepInfo.setInt(1, id);
        prepInfo.execute();

        PreparedStatement prep = con.prepareStatement("DELETE FROM users WHERE id = ?");
        prep.setInt(1, id);
        prep.execute();

    }
    
    protected void removeRequestFriend(int idUser,int idRequestedFriend) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        PreparedStatement prepInfo = con.prepareStatement("DELETE FROM usersRequestsFriends WHERE idUser = ? AND idRequestedFriend = ?");
        prepInfo.setInt(1, idRequestedFriend);
        prepInfo.execute();

    }

    protected void updateMessagesSent(String username) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        int id = getID(username);
        int numMessages = getMessagesSent(id);
        PreparedStatement prep = con.prepareStatement("UPDATE usersInfo SET messagesSent = ? WHERE id = ?");
        prep.setInt(1, numMessages + 1);
        prep.setInt(2,id);
        prep.executeUpdate();
    }


    protected void updateWordsWritten(String username, int n) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        int id = getID(username);
        int lvlUser = getLvlUser(id);
        int numWords = getWordsWritten(id);
        int exp = getExpUser(id);
        int pontuacaoMensagem = Pontuation.calculatePontuation(n);
        int verifyLvl = Pontuation.verifyLvl(exp+pontuacaoMensagem);
        updateMessagesSent(username);
        if(verifyLvl > lvlUser) {
            updateLvlUser(username,verifyLvl);
        }
        PreparedStatement prep = con.prepareStatement("UPDATE usersInfo SET wordsWritten = ?,userExp = ? WHERE id = ?");
        prep.setInt(1, numWords + n);
        prep.setInt(2, exp+pontuacaoMensagem);
        prep.setInt(3,id);
        prep.executeUpdate();
    }

    protected void updateExpUser(String username, int n) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        int id = getID(username);
        int exp = getExpUser(id);
        PreparedStatement prep = con.prepareStatement("UPDATE usersInfo SET userExp = ? WHERE id = ?");
        prep.setInt(1, exp + n);
        prep.setInt(2,id);
        prep.executeUpdate();
    }

    private void updateLvlUser(String username, int lvl) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        int id = getID(username);
        PreparedStatement prep = con.prepareStatement("UPDATE usersInfo SET userLvl = ? WHERE id = ?");
        prep.setInt(1, lvl);
        prep.setInt(2,id);
        prep.executeUpdate();
    }


    protected void getUsers() throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM users");
        System.out.println("result Users:");
        while(res.next()) {
            System.out.println(res.getInt("id")+" "+res.getString("username")+" "+ res.getString("password"));
        }
    }
    
    protected void getFriends() throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM usersFriends");
        System.out.println("result Friends:");
        while(res.next()) {
            System.out.println(res.getInt("id")+" "+res.getInt("idUser")+" "+ res.getInt("idFriend"));
        }
    }

    protected void getInfoPlayers() throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT id,userLvl,userExp,messagesSent,wordsWritten FROM usersInfo");
        System.out.println("result Info (id,lvlUser,ExpUser,messages,words):");
        while(res.next()) {
            System.out.println(res.getInt("id")+" "+res.getInt("userLvl")+" "+ res.getInt("userExp")+" "+res.getInt("messagesSent")+" "+ res.getInt("wordsWritten"));
        }
    }

    protected boolean checkLogin(String username,String password) throws ClassNotFoundException, SQLException {

        boolean result = false;

        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT username,password FROM users WHERE username='" + username + "' AND password='"+ password +"'");

        if(res.next()) {
            result=true;

        }

        return result;
    }
    
    protected boolean checkRequestInvite(String username,String userRequestedFriend) throws ClassNotFoundException, SQLException {

        boolean result = false;

        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT idUser,idRequestedFriend FROM users WHERE idUser='" + username + "' AND idRequestedFriend='"+ userRequestedFriend +"'");

        if(res.next()) {
            result=true;

        }

        return result;
    }

    protected boolean estaRegistado(String username) throws ClassNotFoundException, SQLException {

        boolean result = false;

        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT username FROM users WHERE username='" + username + "'");

        if(res.next()) {
            result=true;

        }

        return result;
    }

    protected int getID(String username) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT id FROM users WHERE username='" + username + "'");
        return res.getInt("id");
    }

    protected int getLvlUser(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT userLvl FROM usersInfo WHERE id='" + id + "'");
        return res.getInt("userLvl");
    }

    protected int getExpUser(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT userExp FROM usersInfo WHERE id='" + id + "'");
        return res.getInt("userExp");
    }

    protected int getMessagesSent(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT messagesSent FROM usersInfo WHERE id='" + id + "'");
        return res.getInt("messagesSent");
    }

    protected int getWordsWritten(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT wordsWritten FROM usersInfo WHERE id='" + id + "'");
        return res.getInt("wordsWritten");
    }


    /**
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
			System.err.println("Erro a encontrar gems do user");
		}
		return result;
	}	
     */


}
