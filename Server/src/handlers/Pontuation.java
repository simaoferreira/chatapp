package handlers;

public class Pontuation {

    protected static int calculatePontuation(int n) {
        int valueWord = getValueWord(n);
        return valueWord;
    }

    protected static int calculateParcialExp(int exp,int lvl) {
    	if(lvl == 1) {
    		if(exp>=0 && exp<100) {
    			return exp;
    		}else {
    			return 0 + (exp-100);
    		}
    	}else if(lvl == 2) {
    		if(exp>=0 && exp<1000) {
    			return exp;
    		}else {
    			return 0 + (exp-1000);
    		}
    	}else if(lvl == 3) {
    		if(exp>=0 && exp<1500) {
    			return exp;
    		}else {
    			return 0 + (exp-1500);
    		}
    	}else if(lvl == 4) {
    		if(exp>=0 && exp<2000) {
    			return exp;
    		}else {
    			return 0 + (exp-2000);
    		}
    	}else if(lvl == 5) {
    		if(exp>=0 && exp<2500) {
    			return exp;
    		}else {
    			return 0 + (exp-2500);
    		}
    	}else if(lvl == 6) {
    		if(exp>=0 && exp<3500) {
    			return exp;
    		}else {
    			return 0 + (exp-3500);
    		}
    	}else if(lvl == 7) {
    		if(exp>=0 && exp<5000) {
    			return exp;
    		}else {
    			return 0 + (exp-5000);
    		}
    	}else if(lvl == 8) {
    		if(exp>=0 && exp<7000) {
    			return exp;
    		}else {
    			return 0 + (exp-7000);
    		}
    	}else if(lvl == 9) {
    		if(exp>=0 && exp<9000) {
    			return exp;
    		}else {
    			return 0 + (exp-1000);
    		}
    	}else {
    		if(exp>=0 && exp<10000) {
    			return exp;
    		}else {
    			return 0 + (exp-10000);
    		}
    	}
    }

    private static int getValueWord(int n) {
        int finalValue = 0;
        for(int i=1;i<=n;i++) {
            int value;

            if(n>=1 && n<=10) {
                value = 10;
            }else if(n>10 && n<=20) {
                value = 11;
            }else if(n>20 && n<=30) {
                value = 12;
            }else if(n>30 && n<=40) {
                value = 13;
            }else if(n>40 && n<=50) {
                value = 14;
            }else {
                value = 15;
            }
            finalValue+=value;
        }

        return finalValue;
    }
}
