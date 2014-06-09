package com.nowakmaj.loc.database;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MetricCalculator {
	
	public static Integer calculateLinesOfCode(File file) throws IOException 
	{
	    InputStream is = new BufferedInputStream(new FileInputStream(file));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	public static Float calculateLinesOfCodePerFile(
		ArrayList<FileInfo> fileInfoList)
	{
		Float average = (float) 0;
		for (FileInfo file: fileInfoList)
			average += (float) file.linesOfCode;
		return average / fileInfoList.size();
	}
	
}
