package handlers;

import java.sql.SQLException;

public class ConnectDB {

    public static void main(String[] args) throws ClassNotFoundException{
        // TODO Auto-generated method stub
        
        SQLiteHandler bd = new SQLiteHandler();
        
        try {
            //bd.updateWordsWritten("berunoxxx",14);
            bd.removeFriend(1, 2);
            //bd.removeRequestFriend(3,2);
        	//bd.removeFriend(3, 1);
        	//bd.changeFirstName("simon", "Sim�o");
        	//bd.changeLastName("simon", "Ferreira");
        	//bd.changeFirstName("berunoxxx", "Bruno");
        	//bd.changeLastName("berunoxxx", "Ribeiro");
        	//bd.addFriend(1, 2);
        	//bd.removeRequestFriend(3, 1);
        	//bd.updateWordsWritten("simon", 50);
            bd.getInfoPlayers();
            bd.getUsers();
            bd.getFriends();
            bd.getRequestsFriends();
            System.out.println(bd.getFriendsOfID(1).toString());
            //bd.getID("berunoxxx");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
