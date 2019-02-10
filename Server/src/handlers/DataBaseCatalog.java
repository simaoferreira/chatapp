package handlers;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DataBaseCatalog {

    private SQLiteHandler sql;
    private LoggerHandle lh;

    /**
     * Handle the Database
     * @param lh - The Logger Handler 
     */
    public DataBaseCatalog(LoggerHandle lh) {
        sql = new SQLiteHandler();
        this.lh = lh;
    }

    public void initialize() {
        try {
            sql.initialize();
            lh.log("INFO", "Database initialized;");
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to initialize database!",e);
        }
    }
    
    /**
     * Returns a random salt to be used to hash a password.
     * @return a 16 bytes random salt
     */
    public byte[] getNextSalt() {
        SecureRandom sr;
        byte[] salt=new byte[16];
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            lh.log("WARNING", "Error while trying to generate salt!", e);
        }
        return salt;
    }
    
    /**
     * Returns a salted and hashed password using the provided hash.
     * @param password the password to be hashed
     * @param salt A 16 bytes salt
     * @return the hashed password with the salt
     */
    public String hash(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, 1000, 64 * 8);
            SecretKeyFactory skf;
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return toHex(hash);
        } catch (Exception e) {
            lh.log("WARNING","Error trying to hash password",e);
        }
        return null;
    }

    /**
     * Conversion byte[] to hex
     * 
     * @param array to be converted
     * @return the conversation in hex from byte[]
     * @throws NoSuchAlgorithmException
     */
    private String toHex(byte[] array) throws NoSuchAlgorithmException{
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        
        if(paddingLength > 0)
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        else
            return hex;
        
    }

    /**
     * Add new user
     * @param username - Username of user
     * @param password - Password of user
     */
    public void addUser(String username,String password) {
        try {
        	byte[] salt = getNextSalt();
            String passw = hash(password.toCharArray(),salt);
            sql.addUser(username,passw,toHex(salt));
            lh.log("INFO", "User "+username+" added successfully;");
        } catch (ClassNotFoundException | SQLException | NoSuchAlgorithmException e) {
            lh.log("WARNING", "Error while trying to add user", e);
        }
    }

    /**
     * Remove an user
     * @param id - ID of user
     */
    public void removeUser(int id) {
        try {
            sql.removeUser(id);
            lh.log("INFO", "User with id "+id+" removed successfully;");
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to remove user!", e);
        }
    }
    
    /**
     * Remove request friend
     * @param username - the user that request
     * @param nameRequestedFriend - the user that receive the invite
     */
    public void removeRequestFriend(String username,String nameRequestedFriend) {
        int user = getID(username);
        int friend = getID(nameRequestedFriend);
        try {
            sql.removeRequestFriend(user, friend);
            lh.log("INFO", "Request friendship sent by "+username+" to "+nameRequestedFriend+" removed successfully;");
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to remove the request friend!", e);
        }
    }
    
    /**
     * Remove friend
     * @param username - The first name
     * @param nameFriend - The second name
     */
    public void removeFriend(String username,String nameFriend) {
        int user = getID(username);
        int friend = getID(nameFriend);
        try {
            sql.removeFriend(user, friend);
            lh.log("INFO", "Friend "+nameFriend+" of "+username+" removed successfully;");
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to remove friend!", e);
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
            lh.log("INFO", "Length of words written by "+username+" successfully updated");
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error updating words written!", e);
        }
    }
    
    /**
     * Change first name of user
     * @param username - the user that wants to change first name
     * @param newFirstName -  the new first name
     */
    public void changeFirstName(String username,String newFirstName) {
    	try {
			sql.changeFirstName(username, newFirstName);
			lh.log("INFO", "User "+username+" changed successfully the first name to "+newFirstName+";");
		} catch (ClassNotFoundException | SQLException e) {
			lh.log("WARNING", "Error changing first name of user!", e);
		}
    }

    /**
     * Change last name of user
     * @param username - the user that wants to change last name
     * @param newFirstName -  the new last name
     */
    public void changeLastName(String username,String newLastName) {
    	try {
			sql.changeLastName(username, newLastName);
			lh.log("INFO", "User "+username+" changed successfully the last name to "+newLastName+";");
		} catch (ClassNotFoundException | SQLException e) {
			lh.log("WARNING", "Error changing last name of user!", e);
		}
    }
    
    /**
     * Change age of user
     * @param username - the user that wants to change age
     * @param age -  the new age
     */
    public void changeAge(String username,int age) {
    	try {
			sql.changeAge(username, age);
			lh.log("INFO", "User "+username+" changed successfully the the age to "+age+";");
		} catch (ClassNotFoundException | SQLException e) {
			lh.log("WARNING", "Error changing age of user!", e);
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
            lh.log("INFO", "Experience of user "+username+" updated and increased by "+n+";");
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error updating experience of user!", e);
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
            lh.log("WARNING", "Error while trying to see if user is registered!", e);
        }

        return false;
    }

    public boolean checkLogin(String username,String password) {
        try {
        	int user = getID(username);
        	String salt = sql.getSalt(user);
        	String hashedPass = sql.getHashedPassword(user);
            return isExpectedPassword(password,salt,hashedPass);
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to check if the login is correct or not!", e);
        }
        return false;
    }
    
    private boolean isExpectedPassword(String pass, String salt, String hashedPassword) {
        byte[] pwdHash;
        try {
            pwdHash = fromHex(hashedPassword);
            byte[] saltHex = fromHex(salt);
            PBEKeySpec spec = new PBEKeySpec(pass.toCharArray(), saltHex, 1000, pwdHash.length * 8);
            SecretKeyFactory skf;
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] calculatedHash = skf.generateSecret(spec).getEncoded();
            if (pwdHash.length != calculatedHash.length) 
                return false;
            
            for (int i = 0; i < calculatedHash.length; i++)
                if (calculatedHash[i] != pwdHash[i])
                    return false;
            
            return true;
        } catch (Exception e) {
            lh.log("WARNING", "Error while trying to check if passwords match or not!", e);
        }
        
        return false;

    }

    private byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        	bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        
        return bytes;
    }
    
    /**
     * Checks if already exists a request invite from username to userRequestedFriend
     * @param username - the username that send request
     * @param userRequestedFriend -  the username that got the request
     * @return true if already exists this request otherwise false
     */
    public boolean checkRequestInvite(String username,String userRequestedFriend) {
        int user = getID(username);
        int friend = getID(userRequestedFriend);
        try {
            return sql.checkRequestInvite(user, friend);
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to check if there is a friend invite!", e);
        }
        return false;
    }
    
    /**
     * Checks if a specific friendship exists
     * @param username - username one
     * @param userFriend - username two
     * @return true if already exists otherwise false
     */
    public boolean checksFriendship(String username, String userFriend) {
        int user = getID(username);
        int friend = getID(userFriend);
        try {
            return sql.checkFriendship(user, friend);
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to check frindship!", e);
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
            lh.log("WARNING", "Error while trying to get user id!", e);
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
            lh.log("INFO", "User "+friend+" added successfully to list of friends of "+username+";");
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to add new friend!", e);
        }
    }
    
    /**
     * Add new request friend
     * @param username - the user that request the friendship
     * @param nameFriend -  the target user of the request
     */
    public void addRequestFriend(String username,String nameFriend) {
        int user = getID(username);
        int friend = getID(nameFriend);
        try {
            sql.addRequestFriend(user, friend);
            lh.log("INFO", "Request friendship sent by "+username+" to "+nameFriend+" added successfully;");
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to add new request friend!", e);
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
            lh.log("WARNING", "Error while trying to get user level!", e);
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
            lh.log("WARNING", "Error while trying to get user experience!", e);
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
            lh.log("WARNING", "Error while trying to get sent messages from the user!", e);
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
            lh.log("WARNING", "Error while trying obtaining the amount of words written by the user!", e);
        }
        return -1;
    }
    
    /**
     * Get the Firstname of the user
     * @param id - the id of the user
     * @return the firstname of the user
     */
    public String getFirstName(int id) {
    	try {
            return sql.getFirstName(id);
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying obtaining the firstname of the user with id "+id+"!", e);
        }
        return null;
    }
    
    /**
     * Get the lastname of the user
     * @param id - the id of the user
     * @return the lastname of the user
     */
    public String getLastName(int id) {
    	try {
            return sql.getLastName(id);
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying obtaining the lastname of the user with id "+id+"!", e);
        }
        return null;
    }
    
    /**
     * Get the age of the user
     * @param id - the id of the user
     * @return the age of the user
     */
    public int getAge(int id) {
    	try {
            return sql.getAge(id);
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying obtaining the age of the user with id "+id+"!", e);
        }
        return -1;
    }
    
    /**
     * Get the username of the user
     * @param id - the id of the user
     * @return the username of the user
     */
    public String getUsername(int id) {
    	try {
            return sql.getUsername(id);
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while trying to obtaining the username of the user with id "+id+"!", e);
        }
        return null;
    }
    
    public String getFullName(int id) {
    	String fn = getFirstName(id);
		String ln = getLastName(id);
		return fn+" "+ln;
    }
    
    public ArrayList<Integer> getFriends(int id) {
    	ArrayList<Integer> friends = new ArrayList<Integer>();
    	
        try {
        	friends = sql.getFriendsOfID(id);
            return friends;
        } catch (ClassNotFoundException | SQLException e) {
            lh.log("WARNING", "Error while obtaining friends of the user!", e);
        }
        return friends;
    }
 
}
