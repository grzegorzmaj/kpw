package com.nowakmaj.loc.database;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class DatabaseReader {
	
	private Document databaseDoc_;
	
	public DatabaseReader(Document dbDoc)
	{
		databaseDoc_ = dbDoc;
	}
	
	public ArrayList<String> getLastChangesDates(int changesCnt)
	{
		ArrayList<String> dateList = new ArrayList<String>();
		dateList.add(databaseDoc_.getFirstChild().getAttributes()
			.getNamedItem("timestamp").getNodeValue());
		NodeList deltasList = databaseDoc_.getElementsByTagName("delta");
		changesCnt = (changesCnt < deltasList.getLength() ?
			changesCnt : deltasList.getLength());
		for (int i = 0; i < changesCnt-1; ++i)
			dateList.add(deltasList.item(i).getAttributes()
				.getNamedItem("timestamp").getNodeValue());
		Collections.sort(dateList, Collections.reverseOrder());
		return dateList;
	}
	
	public ArrayList<String> getLastChangesDatesForFile(
		String filename, int changesCnt)
	{
		ArrayList<String> dateList = new ArrayList<String>();
		ArrayList<Node> files = findAllFilesForFilename(filename);
		for (Node file: files)
		{
			if (dateList.size() == changesCnt) break;
			dateList.add(getTimestampFromFile(file));
		}
		Collections.sort(dateList, Collections.reverseOrder());
		return dateList;
	}
	
	public HashMap<String, String> getLastChangesOfLOCForFile(
			String filename, int changesCnt)
	{
		HashMap<String, String> changesMap = new HashMap<String, String>();
		ArrayList<String> dates = getLastChangesDatesForFile(
				filename, changesCnt);
		Collections.sort(dates);
		ArrayList<Node> files = findAllFilesForFilename(filename);
		String lastLoc = "-1";
		for (String date: dates)
		{
			Node file = findFileThatMatchesDate(files, date);
			if (file != null)
				lastLoc = getLinesOfCodeFromFile(file);				
			changesMap.put(date, lastLoc);
		}		
		return changesMap;
	}
	
	private String getTimestampFromFile(Node file)
	{
		return ((Element) file.getLastChild().getPreviousSibling())
			.getTextContent();
	}
	
	private String getLinesOfCodeFromFile(Node file)
	{
		NodeList children = file.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i)
			if (children.item(i).getNodeName().compareTo("loc") == 0)
				return ((Element) children.item(i)).getTextContent();
		return "";
	}

	private Node findFileThatMatchesDate(ArrayList<Node> files, String date)
	{
		for (Node file: files)
			if (((Element) file.getLastChild().getPreviousSibling())
					.getTextContent().compareTo(date) == 0)
				return file;
		return null;
	}

	private ArrayList<Node> findAllFilesForFilename(String filename)
	{
		ArrayList<Node> files = findAllFiles();
		ArrayList<Node> matchingFiles = new ArrayList<Node>();
		for (Node file: files)
			if (fileMatchFilename(file, filename))
				matchingFiles.add(file);
		return matchingFiles;
	}

	private boolean fileMatchFilename(Node file, String filename) {
		return ((Element) file).getAttribute("path").compareTo(filename) == 0;
	}

	private ArrayList<Node> findAllFiles() {
		ArrayList<Node> files = new ArrayList<Node>();
		NodeList nodes = databaseDoc_.getFirstChild().getChildNodes();
		for (int i = 0; i < nodes.getLength()-1; ++i)
		{
			if (nodes.item(i).getNodeName() == "file")
				files.add(nodes.item(i));
			if (nodes.item(i).getNodeName() == "delta")
			{
				NodeList deltaChildren = nodes.item(i).getChildNodes();
				for (int j = 0; j < deltaChildren.getLength()-1; ++j)
				{
					if (deltaChildren.item(j).getNodeName() == "file")
						files.add(deltaChildren.item(j));
				}
			}
		}
		return files;
	}
	
	private ArrayList<Node> findAllNodesInRoot(String name)
	{
		ArrayList<Node> files = new ArrayList<Node>();
		NodeList nodes = databaseDoc_.getFirstChild().getChildNodes();
		for (int i = 0; i < nodes.getLength()-1; ++i)
			if (nodes.item(i).getNodeName() == name)
				files.add(nodes.item(i));
		return files;
	}

	public HashMap<String, String> getLastChangesOfLOCPF(int changesCnt)
	{
		HashMap<String, String> locpfMap = new HashMap<String, String>();
		ArrayList<String> dates = getLastChangesDates(changesCnt);
		ArrayList<FileInfo> fileInfoList = createFileInfoList();
		locpfMap.put(dates.get(0), MetricCalculator
			.calculateLinesOfCodePerFile(fileInfoList).toString());
		for (int i = 1; i < dates.size(); ++i)
		{
			Node delta = getDeltaForTimestamp(dates.get(i));
			locpfMap.put(dates.get(i), ((Element) delta).getAttribute("locpf"));
		}
		return locpfMap;
	}

	private Node getDeltaForTimestamp(String date)
	{
		ArrayList<Node> deltas = findAllNodesInRoot("delta");
		for (Node delta: deltas)
			if (((Element) delta).getAttribute("timestamp").compareTo(date)==0)
				return delta;
		return null;
	}

	private ArrayList<FileInfo> createFileInfoList()
	{
		ArrayList<Node> files = findAllNodesInRoot("file");
		ArrayList<FileInfo> fileInfos = new ArrayList<FileInfo>();
		for (Node file: files)
		{
			FileInfo fileInfo = new FileInfo();
			fileInfo.linesOfCode =
				Integer.parseInt(getLinesOfCodeFromFile(file));
			fileInfos.add(fileInfo);
		}
		return fileInfos;
	}
}
