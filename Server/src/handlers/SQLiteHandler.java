package handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
                        + "username varchar(16) unique,"
                        + "password varchar(16),"
                        + "salt varchar(100),"
                        + "primary key(id));");
            }

            ResultSet resTabelaUsersInfo = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='usersInfo'");
            if( !resTabelaUsersInfo.next()) {
                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE usersInfo(id integer,"
                		+ "firstName varchar(16),"
                		+ "lastName varchar(16),"
                		+ "age integer,"
                		+ "email varchar(60) unique,"
                        + "userLvl integer,"
                        + "userExp integer,"
                        + "userParcialExp integer,"
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

    protected void addUser(String username,String password, String email, String salt, String firstname,String lastname) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        if(!estaRegistado(username)){
            PreparedStatement prep = con.prepareStatement("INSERT INTO users values(?,?,?,?);");
            prep.setString(2, username);
            prep.setString(3, password);
            prep.setString(4, salt);
            prep.execute();

            PreparedStatement prepInfo = con.prepareStatement("INSERT INTO usersInfo values(?,?,?,?,?,?,?,?,?,?)");
            prepInfo.setString(2, firstname);
            prepInfo.setString(3, lastname);
            prepInfo.setInt(4, 21);
            prepInfo.setString(5, email);
            prepInfo.setInt(6, 1);
            prepInfo.setInt(7, 0);
            prepInfo.setInt(8, 0);
            prepInfo.setInt(9, 0);
            prepInfo.setInt(10, 0);
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
        prepInfo.setInt(1, idUser);
        prepInfo.setInt(2, idRequestedFriend);
        prepInfo.execute();

    }
    
    protected void removeFriend(int idUser,int idFriend) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        PreparedStatement prepInfo = con.prepareStatement("DELETE FROM usersFriends WHERE (idUser = ? AND idFriend = ? ) OR (idUser = ? AND idFriend = ? )");
        prepInfo.setInt(1, idUser);
        prepInfo.setInt(2, idFriend);
        prepInfo.setInt(3, idFriend);
        prepInfo.setInt(4, idUser);
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
    
    protected void changeFirstName(String username,String newFirstName) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        int id = getID(username);
        PreparedStatement prep = con.prepareStatement("UPDATE usersInfo SET firstName = ? WHERE id = ?");
        prep.setString(1, newFirstName);
        prep.setInt(2,id);
        prep.executeUpdate();
    }
    
    protected void changeLastName(String username,String newLastName) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        int id = getID(username);
        PreparedStatement prep = con.prepareStatement("UPDATE usersInfo SET lastName = ? WHERE id = ?");
        prep.setString(1, newLastName);
        prep.setInt(2,id);
        prep.executeUpdate();
    }
    
    protected void changeAge(String username,int age) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        int id = getID(username);
        PreparedStatement prep = con.prepareStatement("UPDATE usersInfo SET age = ? WHERE id = ?");
        prep.setInt(1,age);
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
        int expTotal = getExpUser(id);
        int expParcial = getParcialExpUser(id);
        int pontuacaoMensagem = Pontuation.calculatePontuation(n);
        int calculatedExpParcial = Pontuation.calculateParcialExp(expParcial+pontuacaoMensagem,lvlUser);
        updateMessagesSent(username);
        if(calculatedExpParcial <= expParcial) {
            updateLvlUser(username,lvlUser+1);
        }
        PreparedStatement prep = con.prepareStatement("UPDATE usersInfo SET wordsWritten = ?,userExp = ?,userParcialExp = ? WHERE id = ?");
        prep.setInt(1, numWords + n);
        prep.setInt(2, expTotal+pontuacaoMensagem);
        prep.setInt(3, calculatedExpParcial);
        prep.setInt(4, id);
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
            System.out.println(res.getInt("id")+" "+res.getString("username")+" "+ res.getString("password")+" "+res.getString("salt"));
        }
        System.out.println("Empty");
    }
    
    protected void getFriends() throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM usersFriends");
        System.out.println("result Friends:");
        while(res.next()) {
            System.out.println(res.getInt("idUser")+" "+ res.getInt("idFriend"));
        }
        System.out.println("Empty");
    }
    
    protected void getRequestsFriends() throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM usersRequestsFriends");
        System.out.println("result Requests Friends:");
        while(res.next()) {
            System.out.println(res.getInt("idUser")+" "+ res.getInt("idRequestedFriend"));
        }
        System.out.println("Empty");
    }

    protected void getInfoPlayers() throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT id,firstName,lastName,age,email,userLvl,userExp,userParcialExp,messagesSent,wordsWritten FROM usersInfo");
        System.out.println("result Info (id,lvlUser,ExpUser,messages,words):");
        while(res.next()) {
            System.out.println(res.getInt("id")+" "+res.getString("firstName")+" "+res.getString("lastName")+" "+res.getInt("age")+" "+res.getString("email")+" "+res.getInt("userLvl")+" "+ res.getInt("userExp")+" "+res.getInt("userParcialExp")+" "+res.getInt("messagesSent")+" "+ res.getInt("wordsWritten"));
        }
        System.out.println("Empty");
    }

    protected boolean checkUserExists(String username) throws ClassNotFoundException, SQLException {

        boolean result = false;

        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT username,password FROM users WHERE username='" + username + "'");

        if(res.next()) {
            result=true;

        }

        return result;
    }
    
    protected boolean checkRequestInvite(int username,int userRequestedFriend) throws ClassNotFoundException, SQLException {

        boolean result = false;

        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT idUser,idRequestedFriend FROM usersRequestsFriends WHERE idUser='" + username + "' AND idRequestedFriend='"+ userRequestedFriend +"'");

        if(res.next()) {
            result=true;

        }

        return result;
    }
    
    protected boolean checkFriendship(int username,int userFriend) throws ClassNotFoundException, SQLException {

        boolean result = false;

        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT idUser,idFriend FROM usersFriends WHERE (idUser='" + username + "' AND idFriend='"+ userFriend +"') OR (idUser='" + userFriend + "' AND idFriend='"+ username +"')");

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
    
    protected ArrayList<String> getRequestsInvite(int username,int userRequestedFriend) throws ClassNotFoundException, SQLException {

    	ArrayList<String> requests = new ArrayList<String>();

        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT idUser FROM usersRequestsFriends WHERE idUser='" + username + "' AND idRequestedFriend='"+ userRequestedFriend +"'");

        if(res.next()) {
        	int id = res.getInt("idUser");
            requests.add(getFirstName(id)+" " + getLastName(id));
        }

        return requests;
    }

    protected int getID(String username) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT id FROM users WHERE username='" + username + "'");
        return res.getInt("id");
    }
    
    protected String getSalt(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT salt FROM users WHERE id='" + id + "'");
        return res.getString("salt");
    }
    
    protected String getHashedPassword(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT password FROM users WHERE id='" + id + "'");
        return res.getString("password");
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
    
    protected int getParcialExpUser(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT userParcialExp FROM usersInfo WHERE id='" + id + "'");
        return res.getInt("userParcialExp");
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
    
    protected String getFirstName(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT firstName FROM usersInfo WHERE id='" + id + "'");
        return res.getString("firstName");
    }
    
    protected String getLastName(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT lastName FROM usersInfo WHERE id='" + id + "'");
        return res.getString("lastName");
    }
    
    protected int getAge(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT age FROM usersInfo WHERE id='" + id + "'");
        return res.getInt("age");
    }
    
    protected String getEmail(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT email FROM usersInfo WHERE id='" + id + "'");
        return res.getString("email");
    }
    
    protected String getUsername(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT username FROM users WHERE id='" + id + "'");
        return res.getString("username");
    }
    
    

     protected ArrayList<Integer> getFriendsOfID(int id) throws ClassNotFoundException, SQLException {
        if(con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT users.id FROM users INNER JOIN usersFriends ON users.id = usersFriends.idFriend WHERE usersFriends.idUser='"+id+"'");
        ArrayList<Integer> friends = new ArrayList<Integer>();

        while(res.next()) {
            friends.add(res.getInt("id"));
        }
        
        ResultSet res2 = state.executeQuery("SELECT users.id FROM users INNER JOIN usersFriends ON users.id = usersFriends.idUser WHERE usersFriends.idFriend='"+id+"'");
        while(res2.next()) {
            friends.add(res.getInt("id"));
        }
        
        return friends;
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
