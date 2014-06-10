package com.nowakmaj.loc.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseCreator {
	
	private static String initialDbContentBegin_ =
	"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
	"<database timestamp=\"";
			
	private static String initialDbContentEnd_ ="\">\n"+
	"  <header>\n"+
	"    <description>This is database file for Programming Matric Calculator Plug-in for Eclipse.</description>\n"+
	"  </header>\n"+
	"</database>\n";

	public static void createDatabaseInProjectDir(File projectDir)
	{
		Path path = Paths.get(projectDir.getPath() + File.separator +
        	projectDir.getName() + ".locdb");
		try {
            Files.createFile(path);
        } catch (IOException e) {
            System.err.println("already exists: " + e.getMessage());
        }
		
		try {
			File file = new File(path.toString());
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(initialDbContentBegin_ +
				DatabaseInterface.retrieveTimeStamp() + initialDbContentEnd_);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
