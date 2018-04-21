import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

class Logging {
	private static Logger LOGGER = null;
	
	private Logging() {
	}
	
	public static Logger getInstance() {
		if(LOGGER == null) {
			setLoggging();
		}
		return LOGGER;
	}
	
	public static void setLoggging() {
		LOGGER = Logger.getLogger(GABasic.class.getName());
		
		FileHandler fh;  
		try {  
			// This block configure the logger with handler and formatter  
			fh = new FileHandler("logFile.log");  
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

		} catch (Exception e) {  
			e.printStackTrace();  
		} 
	}
}