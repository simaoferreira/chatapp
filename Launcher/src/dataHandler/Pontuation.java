package dataHandler;

public class Pontuation {

    public static double getProgressBarValue(int exp,int lvl) {
    	if(lvl == 1) {
    		return exp/100f;
    	}else if(lvl == 2) {
    		return exp/1000f;
    	}else if(lvl == 3) {
    		return exp/1500f;
    	}else if(lvl == 4) {
    		return exp/2000f;
    	}else if(lvl == 5) {
    		return exp/2500f;
    	}else if(lvl == 6) {
    		return exp/3500f;
    	}else if(lvl == 7) {
    		return exp/5000f;
    	}else if(lvl == 8) {
    		return exp/7000f;
    	}else if(lvl == 9) {
    		return exp/9000f;
    	}else {
    		return exp/10000f;
    	}
    }
}
