/**
 * com.nowakmaj.loc.database.DatabaseManager
 */
package com.nowakmaj.loc.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Oczytuje informacje z bazy danych.
 */
public class DatabaseReader {
    
    /**
     * Dokument DOM XML z baza danych.
     */
    private Document databaseDoc_;
    
    /**
     * Konstruktor.
     *
     *  @param dokument DOM XML z baza danych
     */
    public DatabaseReader(Document dbDoc)
    {
        databaseDoc_ = dbDoc;
    }
    
    /**
     * Setter dla databaseDoc_.
     *
     *  @param dokument DOM XML z baza danych
     */
    public void setDatabaseDoc(Document dbDoc)
    {
        databaseDoc_ = dbDoc;
    }
    
    /**
     * Zwraca ostatnie changesCnt dat zmian plikow.
     *
     *  @param ilosc dat, ktore maja byc odczytane
     *  @return changesCnt ostatnich zmian plikow (dowolnych) lub mniej jesli nie bylo tyle ile
     *            jest wymagane
     */
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
    
    /**
     * Zwraca ostatnie changesCnt dat zmian dla konkretnego pliku.
     *
     *  @param nazwa pliku
     *  @param ilosc dat, ktore maja byc odczytane
     *  @return changesCnt ostatnich zmian pliku lub mniej jesli nie bylo tyle ile jest wymagane
     */
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
    
    /**
     * Zwraca ostatnie changesCnt zmian LOC dla konkretnego pliku.
     *
     *  @param nazwa pliku
     *  @param ilosc LOC, ktore maja byc odczytane
     *  @return changesCnt ostatnich zmian pliku lub mniej jesli nie bylo tyle ile jest wymagane
     *          w postaci mapy String (data) -> String (LOC)
     */
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

    /**
     * Zwraca ostatnie changesCnt zmian LOCPF.
     *
     *  @param ilosc LOCPF, ktore maja byc odczytane
     *  @return changesCnt ostatnich zmian LOCPF lub mniej jesli nie bylo tyle ile jest wymagane
     *          w postaci mapy String (data) -> String (LOCPF)
     */
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
    
    private String getTimestampFromFile(Node file)
    {
        NodeList children = file.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i)
            if (children.item(i).getNodeName().compareTo("timestamp") == 0)
                return ((Element) children.item(i)).getTextContent();
        return "";
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
                for (int j = 0; j < deltaChildren.getLength(); ++j)
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
