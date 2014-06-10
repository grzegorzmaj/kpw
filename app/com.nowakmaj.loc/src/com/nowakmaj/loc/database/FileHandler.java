package database;
import java.io.File;
import java.io.IOException;

public class FileHandler {
	
	private FileValidator validator = new FileValidator();
	private String timestamp_;
	
	public FileHandler()
	{
		timestamp_ = DatabaseInterface.retrieveTimeStamp();
	}

	public FileInfo handleFile(File file)
	{
		if (validator.isFileValid(file))
		{
	        try {
	        	FileInfo fileInfo = new FileInfo();
	        	fileInfo.path = file.getAbsolutePath();
	        	fileInfo.linesOfCode =
	        		MetricCalculator.calculateLinesOfCode(file);
	        	fileInfo.timestamp = timestamp_;
	        	return fileInfo;     	
	        } 
	        catch (IOException e)
	        {
	        	System.err.println(e.toString());
	        	return null;
	        }
		}
		return null;
	}
	
}
