/**
 * com.nowakmaj.loc.scanner.DatabaseScanner
 */
package com.nowakmaj.loc.scanner;

import java.io.File;

/**
 * Skanuje workspace w poszukiwaniu plikow z bazami danych.
 */
public class DatabaseScanner {
	
    /**
     * Plik z projektem.
     */
	private File projectDir_;
	
    /**
     * Szuka plik z baza danych dla podanego projektu.
     *
     *  @param katalog z workspace'm
     *  @param nazwa projektu
     *  @return plik bazy danych
     */
	public File findDatabaseForProject(File workspaceDir, String projectName)
	{
		findProjectDirectoryFromWorkspaceDirectory(workspaceDir, projectName);
		for (File file: projectDir_.listFiles())
			if (file.getName().compareTo(projectName+".locdb") == 0)
				return file;
		return null;
	}
	
    /**
     * Szuka plik z baza danych dla podanego projektu.
     *
     *  @return plik projetku
     */
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
			if (file.isDirectory() &&
				file.getName().compareTo(projectName) == 0)
				projectDir_ = file;
			else if (file.isDirectory())
				findProjectDirectoryFromWorkspaceDirectoryRecursive(
					file, projectName);
		}
	}

}
