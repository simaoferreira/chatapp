package dataHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.logging.*;
import java.util.Date;

public class LoggerHandle {

	private final static Logger logr = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final SimpleDateFormat ft = new SimpleDateFormat ("E dd.MM.yyyy 'at' hh:mm:ss");

	public LoggerHandle() {
		logr.setLevel(Level.ALL);

		try {
			FileHandler fh = new FileHandler("clientLoggerDetailed.log",true);
			fh.setLevel(Level.FINE);
			logr.addHandler(fh);
		} catch (SecurityException | IOException e) {
			logr.log(Level.SEVERE, "File logger not working", e);
		}
	}
	
	public void log(String level, String text) {		
		Level lvl =  Level.parse(level);
		logr.log(lvl, text);
		printToFile("clientLogger.log",level,text);
	}
	
	public void log(String level, String text,Exception e) {
		Level lvl =  Level.parse(level);
		logr.log(lvl, text,e);
		printToFile("clientLogger.log",level,text);
	}
	
	
	private void printToFile(String fileName,String level,String text) {
		FileWriter fl;
		Date date = new Date();
		try {
			fl = new FileWriter(fileName,true);
			PrintWriter pw = new PrintWriter(fl);
	        if(!isFileEmpty(fileName))
	            pw.printf("\n");
	        
	        pw.printf("--"+level+" ("+ft.format(date)+") : "+text);
	        pw.close();
	        fl.close();
		} catch (IOException e) {
			logr.log(Level.SEVERE, "Error while trying to print to file the log info", e);
		}
        
	}
	
	/**
     * See if the file corresponding to filePath is empty or not
     * @param filePath the location of file to see if is empty or not
     * @return true if file is empty otherwise false
     */
    public boolean isFileEmpty(String filePath) {
        boolean res = false;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            if (br.readLine() == null)
                res = true;

            br.close();	
            
        } catch (Exception e) {
        	logr.log(Level.SEVERE, "Error while trying to check if file is empty or not", e);
        } 
        return res;   
    }

}
