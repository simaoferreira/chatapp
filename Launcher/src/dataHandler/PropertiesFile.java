package dataHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFile {
	
	private LoggerHandle lh;
	
	private String propertiesFileName;
	
	private Properties properties;
	
	public PropertiesFile(String propertiesFileName, LoggerHandle lh) {
		this.propertiesFileName = propertiesFileName;
		this.lh = lh;
		this.properties = new Properties();
		
		try {
			this.properties.load(new FileInputStream(propertiesFileName));
			
			lh.log("INFO", "Properties file '"+this.propertiesFileName+"' loaded");
		}catch(IOException e) {
			String message = "Excpetion while reading  properties file '" + this.propertiesFileName + "': "+e.getLocalizedMessage();
			lh.log("SEVERE", message, e);
		}
	}
	
	public String getProperty(String propertyName) {	
		String res = "";
		lh.log("FINE", "Looking for property '"+propertyName+"'...");
		
		res = properties.getProperty(propertyName);
		lh.log("INFO", propertyName+": "+res);
		
		return res;
	}
	
	public void puProperty(String propertyName, String propertyValue) {
		properties.put(propertyName, propertyValue);		
	}
	
	public void save(boolean makeBackup) {
		String outputFileName = this.propertiesFileName + ((makeBackup) ? ".bak" : "");
		
		try {
			String classpathRoot = getClass().getResource("/").getPath();
			FileOutputStream fos = new FileOutputStream(classpathRoot + File.separator+ outputFileName);
			
			properties.store(fos, "Created for configurations of system");
		}catch(IOException e) {
			String message = "Exception occured: "+ e.getLocalizedMessage();
			lh.log("SEVERE", message,e);
		}
	}

}
