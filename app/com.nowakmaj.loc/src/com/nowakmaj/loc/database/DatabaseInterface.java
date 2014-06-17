/**
 * com.nowakmaj.loc.database.DatabaseInterface
 */
package com.nowakmaj.loc.database;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Jest to fasada do bazy danych, ktora laczy interfejsy wszystkich klas sluzacych do zarzadzania
 * baza danych w xml'u.
 */
public class DatabaseInterface {

    /**
     * Glowny zarzadca bazy danych.
     */
    DatabaseManager dbManager_;
    /**
     * Do odczytywania informacji z bazy danych.
     */
    DatabaseReader dbReader_;
    /**
     * Do inicjalizacji bazy danych.
     */
    DatabaseCreator dbCreator_;
    /**
     * Do skanowania srodowiska w celu znalezienia sledzonych plikow.
     */
    WorkspaceScanner scanner_;
    /**
     * Katalog z projektem.
     */
    File projectDir_;
    /**
     * Plik z baza danych.
     */
    File dbFile_;
    /**
     * Nazwa projektu.
     */
    String projectName_;
    
    /**
     * Pobiera timestamp.
     *
     *  @return String zawierajacy timestamp w formacie MM/dd/yyyy h:mm:ss a
     */
    public static String retrieveTimeStamp()
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        return sdf.format(date);
    }
    
    /**
     * Konstruktor.
     *
     *  @param plik z baza danych
     *  @param katalog z projektem
     *  @param nazwa projektu
     */
    public DatabaseInterface(File dbFile, File projectDir, String projectName)
    {
        projectDir_ = projectDir;
        projectName_ = projectName;
        dbFile_ = dbFile;
        scanner_ = new WorkspaceScanner();
        dbCreator_ = new DatabaseCreator();
    }
    
    /**
     * Zwraca nazwe projektu.
     *
     *  @return nazwa projektu
     */
    public String getProjectName()
    {
        return projectName_;
    }
    
    /**
     * Aktualizauje baze danych. Ta funkcja powinna byc podpieta do triggera w Eclipse przy
     * zapisaniu pliku (Ctrl + S).
     */
    public void updateDb()
    {
        dbManager_ = new DatabaseManager(dbFile_);
        dbReader_ = new DatabaseReader(dbManager_.getDatabaseDocument());
        dbManager_.updateFileInfoInDatabase(scanner_.scanDir(projectDir_));
    }
    
    /**
     * Zwraca ostatnie changesCnt dat zmian plikow.
     *
     *  @param ilosc dat, ktore maja byc odczytane
     *  @return changesCnt ostatnich zmian plikow (dowolnych) lub mniej jesli nie bylo tyle ile
     *            jest wymagane
     */
    public ArrayList<String> getLastChangesDates(int changesCnt) {
        return dbReader_.getLastChangesDates(changesCnt);
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
        return dbReader_.getLastChangesDatesForFile(filename, changesCnt);
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
        return dbReader_.getLastChangesOfLOCForFile(filename, changesCnt);
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
        return dbReader_.getLastChangesOfLOCPF(changesCnt);
    }

    /**
     * Inicjalizuje baze danych dla projektu.
     *
     *  @param katalog z projektem
     */
    public static void initializeDatabase(File projectDir)
    {
        DatabaseCreator.createDatabaseInProjectDir(projectDir);
    }
    
    /**
     * Czysci puste linie z pliku bazy danych.
     */
    public void clearNewLines()
    {
        dbManager_.clearNewLines();
    }
}
