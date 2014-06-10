package com.nowakmaj.loc.scanner;

import java.io.File;
import java.util.ArrayList;

public class FileScanner {
	
	private FileValidator validator_;
	
	public FileScanner(ArrayList<String> acceptedExtensions)
	{
		validator_ = new FileValidator(acceptedExtensions);
	}
	
	public ArrayList<String> getFileNames(File dir)
	{
		ArrayList<File> files = getFiles(dir);
		ArrayList<File> validatedFiles = validateFiles(files);
		ArrayList<String> fileNames = new ArrayList<String>();
		for (File file: validatedFiles)
			fileNames.add(file.getPath());
		return fileNames;
	}

	private ArrayList<File> validateFiles(ArrayList<File> files)
	{
		ArrayList<File> validatedFiles = new ArrayList<File>();
		for (File file: files)
			if (validator_.isFileValid(file))
				validatedFiles.add(file);
		return validatedFiles;
	}

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