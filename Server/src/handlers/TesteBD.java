package handlers;

public class TesteBD {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		LoggerHandle lh = new LoggerHandle();

		DataBaseCatalog db = new DataBaseCatalog(lh);
		
		db.initialize();
		db.addUser("simon", "mastir");
		db.addUser("berunoxxx", "bruno");
		//bd.addRequestFriend(1, 2);
	}

}
