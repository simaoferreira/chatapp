package dataHandler;

public class Pontuation {

    public static double getProgressBarValue(int exp,int lvl) {
    	if(lvl == 1) {
    		return exp/100f;
    	}else if(lvl == 2) {
    		return exp/200f;
    	}else if(lvl == 3) {
    		return exp/300f;
    	}else if(lvl == 4) {
    		return exp/400f;
    	}else if(lvl == 5) {
    		return exp/500f;
    	}else if(lvl == 6) {
    		return exp/600f;
    	}else if(lvl == 7) {
    		return exp/700f;
    	}else if(lvl == 8) {
    		return exp/800f;
    	}else if(lvl == 9) {
    		return exp/900f;
    	}else {
    		return exp/1000f;
    	}
    }
}
