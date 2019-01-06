package handlers;

import java.sql.SQLException;

public class DataBaseCatalog {

    private SQLiteHandler sql;

    /**
     * Handle the Database
     */
    public DataBaseCatalog() {
        sql = new SQLiteHandler();
    }

    public void initialize() {
        try {
            sql.initialize();
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Add new user
     * @param username - Username of user
     * @param password - Password of user
     */
    public void addUser(String username,String password) {
        try {
            sql.addUser(username, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error adding new user!");
            e.printStackTrace();
        }
    }

    /**
     * Remove an user
     * @param id - ID of user
     */
    public void removeUser(int id) {
        try {
            sql.removeUser(id);
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Update words written of user
     * @param username - Username of user
     * @param length - Length of the message splitter by spaces
     */
    public void updateWordsWritten(String username,int length) {
        try {
            sql.updateWordsWritten(username, length);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error updating words written!");
            e.printStackTrace();
        }
    }

    /**
     * Update user experience
     * @param username - Username of user
     * @param length - The amount of experience to add
     */
    public void updateExpUser(String username, int n) {
        try {
            sql.updateExpUser(username, n);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error updating experience of user!");
            e.printStackTrace();
        }
    }

    /**
     * Checks if user is registered in the system or not
     * @param username - Username of user
     * @param password -  Password of user
     * @return true if is registered otherwise false
     */
    public boolean estaRegistado(String username) {
        try {
            return sql.estaRegistado(username);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while trying to see if user is registered!");
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkLogin(String username,String password) {
        try {
            return sql.checkLogin(username, password);
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get user id in system
     * @param username - Username of user
     * @return user ID
     */
    public int getID(String username) {
        try {
            return sql.getID(username);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while trying to get user id!");
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Add new friend
     * @param username - The username
     * @param nameFriend - The friend of username
     */
    public void addFriend(String username,String nameFriend) {
        int user = getID(username);
        int friend = getID(nameFriend);
        try {
            sql.addFriend(user, friend);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while trying to add new friend!");
            e.printStackTrace();
        }
    }

    /**
     * Get user level
     * @param id - ID of user
     * @return The level of user
     */
    public int getLvlUser(int id) {
        try {
            return sql.getLvlUser(id);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while trying to get user level!");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get user experience
     * @param id - ID of user
     * @return The experience of user
     */
    public int getExpUser(int id) {
        try {
            return sql.getExpUser(id);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while trying to get user experience!");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Obtain the amount of messages written by the user
     * @param id - ID of user
     * @return The amount of sent messages from the users
     */
    public int getMessagesSent(int id) {
        try {
            return sql.getMessagesSent(id);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while trying to get sent messages from the user!");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Obtain the amount of words written by the user
     * @param id - ID of user
     * @return The amount of words written by the user
     */
    public int getWordsWritten(int id) {
        try {
            return sql.getWordsWritten(id);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while obtaining the amount of words written by the user!");
            e.printStackTrace();
        }
        return -1;
    }    
}
