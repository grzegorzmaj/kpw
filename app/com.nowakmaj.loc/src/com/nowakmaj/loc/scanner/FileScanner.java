/**
 * com.nowakmaj.loc.scanner.FileScanner
 */
package com.nowakmaj.loc.scanner;

import java.io.File;
import java.util.ArrayList;

/**
 * Skanuje workspace w poszukiwaniu plikow z z rozszerzeniem.
 */
public class FileScanner {
	
	/**
	 * Waliduje przeszukiwane pliki czy zgadzaja sie z porzadanym rozszerzeniem.
	 */
	private FileValidator validator_;
	
	/**
	 * Konstruktor.
	 *
	 *  @param lista akceptownych rozrszerzen
	 */
	public FileScanner(ArrayList<String> acceptedExtensions)
	{
		validator_ = new FileValidator(acceptedExtensions);
	}
	
	/**
	 * Zwraca nazwy plikow dla danego katalogu, ktore sa zgodne z rozszerzeniami.
	 *
	 *	@param katalog skanowania
	 *	@return lista nazw plikow
	 */
	public ArrayList<String> getFileNames(File dir)
	{
		ArrayList<File> files = getFiles(dir);
		ArrayList<File> validatedFiles = validateFiles(files);
		ArrayList<String> fileNames = new ArrayList<String>();
		for (File file: validatedFiles)
			fileNames.add(file.getPath());
		return fileNames;
	}

	/**
	 * Zwraca pliki dla danego katalogu, ktore sa zgodne z rozszerzeniami.
	 *
	 *	@param katalog skanowania
	 *	@return lista plikow
	 */
	public ArrayList<File> getFiles(File dir)
    {
		ArrayList<File> files = new ArrayList<File>();
    	if (dir.isDirectory())
    	{
    		getFilesRecursively(dir, files);
	    	return files;
    	}
    	else
    	{
    		System.err.println(
    			"Path " + dir.getPath() + " is not a directory.");
    		return null;
    	}
    }

	private ArrayList<File> validateFiles(ArrayList<File> files)
	{
		ArrayList<File> validatedFiles = new ArrayList<File>();
		for (File file: files)
			if (validator_.isFileValid(file))
				validatedFiles.add(file);
		return validatedFiles;
	}
    
    private void getFilesRecursively(File dir, ArrayList<File> files)
    {
    	File[] filesInDir = dir.listFiles();
    	for(File file: filesInDir)
    	{
    		if (file.isFile())
    			files.add(file);
    		else
    		{
    			getFilesRecursively(file, files);
    		}
    	}
    }
}