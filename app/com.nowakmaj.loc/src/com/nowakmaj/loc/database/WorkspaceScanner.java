package com.nowakmaj.loc.database;

import java.io.File;
import java.util.ArrayList;
 
public class WorkspaceScanner
{
	private FileHandler fileHandler;
	private ArrayList<FileInfo> fileInfoList;
	
	public WorkspaceScanner()
	{
		fileHandler = new FileHandler();
		fileInfoList = new ArrayList<FileInfo>();
	}
    
    public ArrayList<FileInfo> scanDir(File dir)
    {
    	fileHandler.resetTimestamp();
    	fileInfoList = new ArrayList<FileInfo>();
    	if (dir.isDirectory())
    	{
	    	scanDirRecursively(dir);
//	    	System.out.println(
//	    		"WorkspaceScanner: " + fileInfoList.size() +
//	    		" valid files scanned.");
	    	return fileInfoList;
    	}
    	else
    	{
    		System.err.println(
    			"Path " + dir.getPath() + " is not a directory.");
    		return null;
    	}
    }
    
    private void scanDirRecursively(File dir)
    {
    	File[] files = dir.listFiles();
    	FileInfo fileInfo;
    	for(File file: files)
    	{
    		if (file.isFile())
    		{
    			fileInfo = fileHandler.handleFile(file);
    			if (fileInfo != null)
    			{
    				fileInfoList.add(fileInfo);
    			}
    		}
    		else
    		{
	            scanDirRecursively(file);
    		}
    	}
    }
 
}
