package handlers;

public class TesteBD {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		LoggerHandle lh = new LoggerHandle();

		DataBaseCatalog db = new DataBaseCatalog(lh);
		
		db.initialize();
		//db.addUser("simon", "mastir","mastirofgames@gmail.com","Simão","Ferreira");
		//db.addUser("berunoxxx", "bruno","berunoxxx@gmail.com","Bruno","Ribeiro");
		//db.addUser("bruno", "cruz","brunocruz@gmail.com",21,"Bruno","Cruz");
		//bd.addRequestFriend(1, 2);
	}

}
