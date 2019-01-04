package handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TesteBD {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		SQLiteHandler bd = new SQLiteHandler();
		ResultSet rs;
		
		try {
			bd.initialize();
			bd.addUser("simon", "mastir");
			bd.addUser("berunoxxx", "bruno");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
