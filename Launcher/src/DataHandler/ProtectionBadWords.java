package DataHandler;

public class ProtectionBadWords {
	
	String[] palavroes = {"merda","cabr�o","cabrao","puta","bitch","foda-se","foda se","fodase", "caralho","p�nis","penis","vagina","fuder","foder","sexo","sex",
			"ot�rio","otario","idiota","fuck","shit"};
	
	public String findAnDeleteBadWords(String text) {
		
		String newText = text;
		
		for(int i=0;i<palavroes.length;i++) {
			
			if(newText.contains(palavroes[i])){
				newText = newText.replaceAll(palavroes[i], "****");
			}
		}
		
		return newText;
	}
}
