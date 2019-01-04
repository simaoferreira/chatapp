package handlers;

public class Pontuation {

    protected static int calculatePontuation(int n) {
        int valueWord = getValueWord(n);
        return valueWord;
    }
    
    protected static int verifyLvl(int n) {
        int lvl;
        if(n>=1 && n<100) {
            lvl = 1;
        }else if(n>=100 && n<1000) {
            lvl = 2;
        }else if(n>=1000 && n<2500) {
            lvl = 3;
        }else if(n>=2500 && n<5000) {
            lvl = 4;
        }else if(n>=5000 && n<7500) {
            lvl = 5;
        }else if(n>=7500 && n<10000) {
            lvl = 6;
        }else if(n>=10000 && n<15000) {
            lvl = 7;
        }else if(n>=15000 && n<22000) {
            lvl = 8;
        }else if(n>=22000 && n<30000) {
            lvl = 9;
        }else if(n>=30000 && n<40000) {
            lvl = 10;
        }else {
            lvl = 10;
        }
        
        return lvl;
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
