/**
 * com.nowakmaj.loc.database.DatabaseCreator
 */
package com.nowakmaj.loc.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Klasa ta odpowida za tworzenie bazy danych w formacie xml. Plik jest tworzony zawsze w takiej
 * samej formie:
 *		initialDbContentBegin_ + timestamp + initialDbContentEnd_
 */
public class DatabaseCreator {
	
	/**
	 * Poczatek pliku przez timestamp'em.
	 */
	private static String initialDbContentBegin_ =
	"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
	"<database timestamp=\"";
	
	/**
	 * Koniec pliku po timestamp'ie.
	 */		
	private static String initialDbContentEnd_ ="\">\n"+
	"  <header>\n"+
	"    <description>This is database file for Programming Matric Calculator Plug-in for Eclipse.</description>\n"+
	"  </header>\n"+
	"</database>\n";

	/**
	 * Tworzy swieza baze danych w podanym katalogu.
	 *
	 *	@param katalog projektu
	 */
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
