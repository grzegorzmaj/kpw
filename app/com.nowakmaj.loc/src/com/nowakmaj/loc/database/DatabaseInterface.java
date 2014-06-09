package com.nowakmaj.loc.database;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DatabaseInterface {

	String projName;
	DatabaseManager dbManager_;
	DatabaseReader dbReader_;
	WorkspaceScanner scanner_;
	File projectDir_;
	
	
	public static String retrieveTimeStamp()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		return sdf.format(date);
	}
	
	public DatabaseInterface(File dbFile, File projectDir, String Proj)
	{
		projName = Proj;
		projectDir_ = projectDir;
		dbManager_ = new DatabaseManager(dbFile);
		dbReader_ = new DatabaseReader(dbManager_.getDatabaseDocument());
		scanner_ = new WorkspaceScanner();
	}
	
	public String Name()
	{
		return projName;
	}
	
	public void updateDb()
	{
		dbManager_.updateFileInfoInDatabase(scanner_.scanDir(projectDir_));
	}
	
	public ArrayList<String> getLastChangesDates(int changesCnt)
	{
		return dbReader_.getLastChangesDates(changesCnt);
	}

	public HashMap<String, String> getLastChangesOfLOCForFile(
		String filename, int changesCnt)
	{
		return dbReader_.getLastChangesOfLOCForFile(filename, changesCnt);
	}
	
	public HashMap<String, String> getLastChangesOfLOCPF(int changesCnt)
	{
		return dbReader_.getLastChangesOfLOCPF(changesCnt);
	}
}