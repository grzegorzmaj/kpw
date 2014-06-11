package com.nowakmaj.loc.database;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DatabaseManager {
	
	private File databaseFile_;
	private Document databaseDoc_;
	private ArrayList<Node> fileNodes_;
	
	public DatabaseManager(File databaseFile)
	{
		databaseFile_ = databaseFile;
		fileNodes_ = new ArrayList<Node>();
		try {
			openDatabase();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public Document getDatabaseDocument()
	{
		return databaseDoc_;
	}
	
	public void updateFileInfoInDatabase(ArrayList<FileInfo> fileInfoList)
	{
		getFileNodesFromDocument();
		DeltaCreator deltaCreator = new DeltaCreator(databaseDoc_);
		for(FileInfo file: fileInfoList)
		{
			Node fileNode = findTrackedFile(file);
			if (null != fileNode)
				handleTrackedFile(deltaCreator, file, fileNode);
			else
				addNewFileToDatabase(file);
		}
		deltaCreator.appendDeltaIfNeeded();
		deltaCreator.setTrackedCnt(fileNodes_.size());
		deltaCreator.setLOCPF(
			MetricCalculator.calculateLinesOfCodePerFile(fileInfoList));
		setDatabaseTimestamp();
		updateDatabaseFile();
	}

	private void setDatabaseTimestamp() {
		((Element) databaseDoc_.getFirstChild()).setAttribute(
			"timestamp", DatabaseInterface.retrieveTimeStamp());
	}

	private void handleTrackedFile(DeltaCreator deltaCreator, FileInfo file,
			Node fileNode) {
		if (fileHasChanged(fileNode, file))
		{
//			System.out.println("File " + file.path + " has changed!");
			deltaCreator.addFileNodeToDelta(
				databaseDoc_.getFirstChild().removeChild(fileNode));
			addNewFileToDatabase(file);					
		}
		else
		{
			NodeList children = fileNode.getChildNodes();
			for (int i = 0; i < children.getLength(); ++i)
				if (children.item(i).getNodeName().compareTo("timestamp") == 0)
					children.item(i).setTextContent(file.timestamp);
		}
	}

	private boolean fileHasChanged(Node fileNode, FileInfo file) {
		String loc = "";
		NodeList fileChildren = fileNode.getChildNodes();
		for (int i = 0; i < fileChildren.getLength(); ++i)
			if (fileChildren.item(i).getNodeName().compareTo("loc") == 0)
			{
				loc = fileChildren.item(i).getTextContent();
				break;
			}
		
		if (file.linesOfCode.toString().compareTo(loc) == 0)
			return false;
		return true;
	}

	private void updateDatabaseFile()
	{
		try {
//			NodeList children = databaseDoc_.getFirstChild().getChildNodes();
//			for (int i = 0; i < children.getLength(); ++i)
//				if (children.item(i).getNodeName().compareTo("#text") == 0)
//					children.item(i).setTextContent("");
			Transformer transformer = createXMLTransformer();
			StreamResult result = new StreamResult(
				new FileOutputStream(databaseFile_.getAbsolutePath()));
			transformer.transform(new DOMSource(databaseDoc_), result);
		} catch (FileNotFoundException | TransformerFactoryConfigurationError
			| TransformerException e) {
			e.printStackTrace();
		}		
		clearNewLines();
	}

	public void clearNewLines() {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(databaseFile_.getPath()),
				Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String content = "";
		for (String line: lines)
			if (line.compareTo("  ") != 0)
				content += line + "\n";
//		content = content.replaceAll("^[ |\t]*\n$", "");
		
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter(databaseFile_.getPath()));
		    writer.write(content);

		}
		catch ( IOException e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( IOException e)
		    {
		    }
		}
	}

	private Transformer createXMLTransformer()
			throws TransformerConfigurationException,
			TransformerFactoryConfigurationError {
		Transformer transformer	=
			TransformerFactory.newInstance().newTransformer();
		setTransformerDoctypeSettings(transformer);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
			"{http://xml.apache.org/xslt}indent-amount", "2");
		return transformer;
	}

	private void setTransformerDoctypeSettings(Transformer transformer) {
		if (databaseDoc_.getDoctype() != null)
		{
			String systemValue = (new File(databaseDoc_.getDoctype()
				.getSystemId())).getName();
			transformer.setOutputProperty(
				OutputKeys.DOCTYPE_SYSTEM, systemValue);
		}
	}

	private void addNewFileToDatabase(FileInfo file)
	{
		Element locNode = databaseDoc_.createElement("loc");
		locNode.setTextContent(file.linesOfCode.toString());
		Element timestampNode = databaseDoc_.createElement("timestamp");
		timestampNode.setTextContent(file.timestamp);
		Element fileNode = databaseDoc_.createElement("file");
		fileNode.setAttribute("path", file.path);
		fileNode.appendChild(locNode);
		fileNode.appendChild(timestampNode);
		Node headerNode = databaseDoc_
			.getFirstChild().getFirstChild().getNextSibling();
		databaseDoc_.getLastChild().insertBefore(fileNode, headerNode.getNextSibling());
	}

	private Node findTrackedFile(FileInfo file)
	{
		for (Node fileNode: fileNodes_)
		{
			NamedNodeMap attrMap = fileNode.getAttributes();
			if (0 == file.path.compareTo(
				attrMap.getNamedItem("path").getNodeValue()))
			{
//				System.out.println("File: " + file.path + " tracked.");
				return fileNode;
			}
		}
//		System.out.println("File: " + file.path + " not tracked.");
		return null;
	}

	private void openDatabase() 
		throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		databaseDoc_ = builder.parse(databaseFile_);
		getFileNodesFromDocument();
	}
	
	private void getFileNodesFromDocument()
	{
		fileNodes_ = new ArrayList<Node>();
		NodeList filesList = databaseDoc_.getFirstChild().getChildNodes();
		for (int i = 0; i < filesList.getLength(); ++i)
			if (filesList.item(i).getNodeName().compareTo("file") == 0)
				fileNodes_.add(filesList.item(i));
	}
	
	public static void printDocument(Document doc, OutputStream out)
		throws IOException, TransformerException
	{
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty(
	    	"{http://xml.apache.org/xslt}indent-amount", "4");
	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
}
