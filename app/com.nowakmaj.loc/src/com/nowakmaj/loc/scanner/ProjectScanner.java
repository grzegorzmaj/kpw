package com.nowakmaj.loc.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ProjectScanner {
	
	private FileValidator validator_;
	
	public ProjectScanner()
	{
		ArrayList<String> ext = new ArrayList<String>();
		ext.add("project");
		validator_ = new FileValidator(ext);
	}
	
	public boolean isFileFromProject(File file, String projectName, String workspacePath)
	{
		while (file != null &&  file.getPath().compareTo(workspacePath) !=0 )
		{
			if (file.getName().compareTo(projectName)==0)
				return true;
			file = file.getParentFile();
		}
		return false;
	}
	
	public ArrayList<String> getProjectNames(File dir)
	{
		ArrayList<File> files = getFiles(dir);
		ArrayList<File> validatedFiles = validateFiles(files);
		
		ArrayList<String> projectNames = new ArrayList<String>();
		for (File file: validatedFiles)
		{
			try {
				projectNames.add(getProjectNameFromProjFile(file));
			} catch (ParserConfigurationException | SAXException
				| IOException e){
				
			}
		}
		return projectNames;
	}
	
	private String getProjectNameFromProjFile(File file)
		throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document databaseDoc = builder.parse(file);
		return databaseDoc.getFirstChild().getFirstChild().getNextSibling()
			.getTextContent();
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
