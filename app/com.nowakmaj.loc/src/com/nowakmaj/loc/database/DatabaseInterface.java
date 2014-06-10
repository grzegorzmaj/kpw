package com.nowakmaj.loc.database;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DatabaseInterface {

	DatabaseManager dbManager_;
	DatabaseReader dbReader_;
	DatabaseCreator dbCreator_;
	WorkspaceScanner scanner_;
	File projectDir_;
	String projectName_;
	
	public static String retrieveTimeStamp()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		return sdf.format(date);
	}
	
	public DatabaseInterface(File dbFile, File projectDir, String projectName)
	{
		projectDir_ = projectDir;
		projectName_ = projectName; 
		dbManager_ = new DatabaseManager(dbFile);
		dbReader_ = new DatabaseReader(dbManager_.getDatabaseDocument());
		scanner_ = new WorkspaceScanner();
		dbCreator_ = new DatabaseCreator();
	}
	
	public String getProjectName()
	{
		return projectName_;
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

	public static void initializeDatabase(File projectDir)
	{
		DatabaseCreator.createDatabaseInProjectDir(projectDir);
	}
}
