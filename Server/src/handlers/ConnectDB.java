package handlers;

import java.sql.SQLException;

public class ConnectDB {

    public static void main(String[] args) throws ClassNotFoundException{
        // TODO Auto-generated method stub
        
        SQLiteHandler bd = new SQLiteHandler();
        
        try {
            //bd.updateWordsWritten("berunoxxx",14);
            //Sbd.removeUser(3);
            bd.getInfoPlayers();
            bd.getUsers();
            //bd.getID("berunoxxx");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
