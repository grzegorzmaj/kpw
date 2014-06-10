package com.nowakmaj.loc.views;




import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.HashMap;

import com.nowakmaj.loc.database.*;
import com.nowakmaj.loc.scanner.*;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.*;
import org.eclipse.core.internal.resources.projectvariables.ParentVariableResolver;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;




public class LocView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.nowakmaj.loc.views.LocView";

	private IResourceChangeListener listener;
	private TabFolder tabs;
	Composite _parent;

	ArrayList<String> projectNames;
	ArrayList<String> fileNames;
	ArrayList<String> ext = new ArrayList<String>();
	ProjectScanner projScan = new ProjectScanner();
	FileScanner fileScan;

	java.io.File workspace;
	String workspaceName;

	ArrayList<java.io.File> projectDir = new ArrayList<java.io.File>();
	ArrayList<java.io.File> databaseFile = new ArrayList<java.io.File>();
	ArrayList<DatabaseInterface> dbInterface = new ArrayList<DatabaseInterface>();

	//	ArrayList<String> dates = dbInterface.getLastChangesDates(5);
	//	HashMap<String, String> changes =
	//    	dbInterface.getLastChangesOfLOCForFile("D:\\testDir\\asd.c", 5);
	//	HashMap<String, String> locpfs =
	//        dbInterface.getLastChangesOfLOCPF(5);


	class ViewLabelProvider extends LabelProvider implements ILabelProvider{
		public String getColumnText(Object obj, int index){
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index){
			return getImage(obj);
		}

		public Image getImage(Object obj){
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The constructor.x
	 */
	public LocView() {

		ext.add("java");
		fileScan = new FileScanner(ext);
		workspaceName = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		workspace  = new java.io.File(workspaceName);
		projectNames =  projScan.getProjectNames(workspace);
		fileNames =  fileScan.getFileNames(workspace);

		for(String name: projectNames) 
		{
			String newDir = workspaceName + "/" + name;
			projectDir.add(new java.io.File(newDir));
			newDir += "/testDb.xml"; 
			databaseFile.add(new java.io.File(newDir));
			dbInterface.add(new DatabaseInterface(databaseFile.get(databaseFile.size()-1), projectDir.get(projectDir.size()-1),name));

		}

		//		System.out.println(workspaceName);
	}



	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createTabs(Composite parent)
	{
		tabs = new TabFolder(parent, SWT.BOTTOM);


		for(String name: projectNames) 
		{
			TabItem it = new TabItem(tabs, SWT.NONE);
			it.setText(name);
			Composite c = new Composite(tabs, SWT.SINGLE);
			c.setLayout(new FillLayout());

			Tree addressTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			addressTree.setHeaderVisible(true);
			final TreeViewer m_treeViewer = new TreeViewer(addressTree);

			TreeColumn column1 = new TreeColumn(addressTree, SWT.LEFT);
			addressTree.setLinesVisible(true);
			column1.setAlignment(SWT.LEFT);
			column1.setText("Package/File");
			column1.setWidth(160);
			TreeColumn column2 = new TreeColumn(addressTree, SWT.RIGHT);
			column2.setAlignment(SWT.LEFT);
			column2.setText("num of lines");
			column2.setWidth(100);
			TreeColumn column3 = new TreeColumn(addressTree, SWT.RIGHT);
			column3.setAlignment(SWT.LEFT);
			column3.setText("num of lines");
			column3.setWidth(100);
			TreeColumn column4 = new TreeColumn(addressTree, SWT.RIGHT);
			column4.setAlignment(SWT.LEFT);
			column4.setText("num of lines");
			column4.setWidth(100);
			TreeColumn column5 = new TreeColumn(addressTree, SWT.RIGHT);
			column5.setAlignment(SWT.LEFT);
			column5.setText("num of lines");
			column5.setWidth(100);
			TreeColumn column6 = new TreeColumn(addressTree, SWT.RIGHT);
			column6.setAlignment(SWT.LEFT);
			column6.setText("num of lines");
			column6.setWidth(100);

			m_treeViewer.setContentProvider(new AddressContentProvider());
			m_treeViewer.setLabelProvider(new TableLabelProvider());
			List<Project> projects = new ArrayList<Project>();
			Project newProject = new Project(name, fileNames); 


			projects.add(newProject);
			m_treeViewer.setInput(projects);
			m_treeViewer.expandAll();
			it.setControl(c);
			m_treeViewer.addDoubleClickListener(new IDoubleClickListener() {

				@Override
				public void doubleClick(DoubleClickEvent event) {
					IStructuredSelection selection = (IStructuredSelection) m_treeViewer.getSelection();
					if (selection.isEmpty()) return;
					

					Object selectedElement = selection.getFirstElement();
					
					Item sel = (Item) selectedElement;
					if(sel.isFile())
					{
						File selectedFile = (File) selectedElement;
						showMessage(selectedFile);
					}
				}
			});

		}
		tabs.setSelection(0);
	}

	public void createPartControl(Composite parent) {
		_parent = parent;

		createTabs(parent);


		hookSave();
	}



	private void showMessage(File selectedFile) 
	{
		ArrayList<String> dates = new ArrayList<String>();;
		HashMap<String, String> changes = new HashMap<String, String>();
		HashMap<String, String> changesLocpf = new HashMap<String, String>();
		for (DatabaseInterface data: dbInterface)
		{
			if(data.Name().compareTo(selectedFile.getProject().toString())==0)
			{
				dates = data.getLastChangesDates(100);
				changes = data.getLastChangesOfLOCForFile(selectedFile.getPath(), 100);
				changesLocpf = data.getLastChangesOfLOCPF(100);
			}
		}
		mydialog dialog = new mydialog(tabs.getShell(), dates, changes, changesLocpf);
		dialog.open();
	}

	private void hookSave(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// Example resource listener
		listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				for (DatabaseInterface data: dbInterface)
				{
					data.updateDb();
				}
				tabs.dispose();
				createTabs(_parent);
				_parent.layout(true);
			}

		};

		workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tabs.isFocusControl();
	}
	
	public interface Item
	{
		abstract boolean isProject();
		abstract boolean isFile();
	}
	

	class Project implements Item{
		File[]	files;
		ArrayList<File> files2 = new ArrayList<File>();
		private String projName;

		public Project(String name_proj, ArrayList<String> fileNames){
			projName = name_proj;

			for (String name: fileNames)
			{
				java.io.File newFile = new java.io.File(name);
				if (projScan.isFileFromProject(newFile, projName, workspaceName))
					files2.add(new File( this, newFile.getParentFile().getName()+"/"+newFile.getName(), newFile.getPath()));
			}
			
			files = files2.toArray(new File[files2.size()]);
			
		}

		public File[] getFiles(){
			return files;
		}

		public String toString(){
			return projName;
		}

		@Override
		public boolean isProject() {
			return true;
		}

		@Override
		public boolean isFile() {
			return false;
		}
	}



	class File implements Item{
		Project project;
		private String name;
		String fullPath;


		public File(Project pro, String name_file, String full){
			name = name_file;
			this.project = pro;
			fullPath = full;
		}

		public String getPath()
		{
			return fullPath;
		}

		public Project getProject()
		{
			return project;
		}

		public String toString(){
			return name;
		}

		public String getLines(int i){
			ArrayList<String> dates = new ArrayList<String>();;
			HashMap<String, String> changes = new HashMap<String, String>();
			for (DatabaseInterface data: dbInterface)
			{
				if(data.Name().compareTo(project.toString())==0)
				{
					dates = data.getLastChangesDates(5);
					changes = data.getLastChangesOfLOCForFile(fullPath, 5);
				}
			}

			switch(i){
			case 1: 
				if(dates.size()>0)
				{
					if(changes.get(dates.get(0))!="-1")
						return changes.get(dates.get(0));
				}
				return "x";
			case 2: 
				if(dates.size()>1)
					if(changes.get(dates.get(1))!="-1")
						return changes.get(dates.get(1));
				return "x";
			case 3: 
				if(dates.size()>2)
					if(changes.get(dates.get(2))!="-1")
						return changes.get(dates.get(2));
				return "x";
			case 4: 
				if(dates.size()>3)
					if(changes.get(dates.get(3))!="-1")
						return changes.get(dates.get(3));
				return "x";
			case 5: 
				if(dates.size()>4)
					if(changes.get(dates.get(4))!="-1")
						return changes.get(dates.get(4));
				return "x";
			}
			return null;
		}

		@Override
		public boolean isProject() {
			return false;
		}

		@Override
		public boolean isFile() {
			return true;
		}
	}


	class AddressContentProvider implements ITreeContentProvider{
		public Object[] getChildren(Object parentElement){
			if (parentElement instanceof List)
				return ((List<?>) parentElement).toArray();
			if (parentElement instanceof Project)
				return ((Project) parentElement).getFiles();
			return new Object[0];
		}

		public Object getParent(Object element){
			if (element instanceof File)
				return ((File) element).project;
			return null;
		}

		public boolean hasChildren(Object element){
			if (element instanceof List)
				return ((List<?>) element).size() > 0;
				if (element instanceof Project)
					return ((Project) element).getFiles().length > 0;
					return false;
		}

		public Object[] getElements(Object projects){
			return getChildren(projects);
		}

		public void dispose(){
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		}
	}

	class TableLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex){
			return null;
		}

		public String getColumnText(Object element, int columnIndex){
			switch (columnIndex){
			case 0: return element.toString();
			case 1:
				if (element instanceof File)
					return ((File)element).getLines(1);
			case 2: 
				if (element instanceof File)
					return ((File)element).getLines(2);
			case 3: 
				if (element instanceof File)
					return ((File)element).getLines(3);
			case 4: 
				if (element instanceof File)
					return ((File)element).getLines(4);
			case 5: 
				if (element instanceof File)
					return ((File)element).getLines(5);

			}
			return null;
		}

		public void addListener(ILabelProviderListener listener){
		}

		public void dispose(){
		}

		public boolean isLabelProperty(Object element, String property){
			return false;
		}

		public void removeListener(ILabelProviderListener listener){
		}
	}	

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		super.dispose();
	}
}