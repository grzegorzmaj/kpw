package com.nowakmaj.loc.database;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DeltaCreator {
	
	private Element delta_;
	private Document databaseDoc_;
	private Integer changedCnt_= 0;
	private boolean deltaNeeded_ = false;

	public DeltaCreator(Document databaseDoc)
	{
		databaseDoc_ = databaseDoc;
	}
	
	public void addFileNodeToDelta(Node fileNode) {
		if (false == deltaNeeded_)
		{
			delta_ = databaseDoc_.createElement("delta");
			delta_.setAttribute("timestamp", findTimeStampOfFile(fileNode));
			deltaNeeded_ = true;
		}
		delta_.appendChild(fileNode);
		changedCnt_ += 1;
	}
	
	private String findTimeStampOfFile(Node fileNode)
	{
		NodeList nodes = fileNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i)
		{
			if (nodes.item(i).getNodeName() == "timestamp")
				return nodes.item(i).getTextContent();
		}
		return "";
	}
	
	public void appendDeltaIfNeeded()
	{
		if (true == deltaNeeded_)
		{
			delta_.setAttribute("changedFilesCount", changedCnt_.toString());
			databaseDoc_.getFirstChild().insertBefore(
				delta_, findLastFileNode());
		}
	}
	
	public void setTrackedCnt(Integer cnt)
	{
		if (true == deltaNeeded_)
			delta_.setAttribute("trackedFilesCount", cnt.toString());
	}
	
	public void setLOCPF(Float locpf)
	{
		if (true == deltaNeeded_)
			delta_.setAttribute("locpf", locpf.toString());
	}

	private Node findLastFileNode() {
		NodeList nodes = databaseDoc_.getFirstChild().getChildNodes();
		Node lastNodeBeforeDelta = nodes.item(0);
		int i = 1;
		for (; i < nodes.getLength(); ++i)
		{
			if (nodes.item(i).getNodeName() == "header" ||
				nodes.item(i).getNodeName() == "file")
				lastNodeBeforeDelta = nodes.item(i);
			if (nodes.item(i).getNodeName() == "delta")
				break;
		}
		return lastNodeBeforeDelta.getNextSibling();
	}

}
