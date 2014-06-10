package com.nowakmaj.loc.scanner;

import java.io.File;

public class DatabaseScanner {
	
	private File projectDir_;
	
	public File findDatabaseForProject(File workspaceDir, String projectName)
	{
		findProjectDirectoryFromWorkspaceDirectory(workspaceDir, projectName);
		for (File file: projectDir_.listFiles())
			if (file.getName().compareTo(projectName+".locdb") == 0)
				return file;
		return null;
	}
	
	public File getProjectDir()
	{
		return projectDir_;
	}

	private void findProjectDirectoryFromWorkspaceDirectory(
			File workspaceDir, String projectName)
	{
		findProjectDirectoryFromWorkspaceDirectoryRecursive(
			workspaceDir, projectName);
	}

	private void findProjectDirectoryFromWorkspaceDirectoryRecursive(File dir,
			String projectName) {
		for (File file: dir.listFiles())
		{
			System.out.println(file.getPath());
			if (file.isDirectory() &&
				file.getName().compareTo(projectName) == 0)
			{
				System.out.println("FOUND!!");
				projectDir_ = file;
			}
			else if (file.isDirectory())
				findProjectDirectoryFromWorkspaceDirectoryRecursive(
					file, projectName);
		}
	}

}
