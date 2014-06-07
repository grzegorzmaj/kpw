package com.nowakmaj.loc.views;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.*;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
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
	private Action doubleClickAction;


	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */

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
	 * The constructor.
	 */
	public LocView() {
	}



	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		tabs = new TabFolder(parent, SWT.BOTTOM);


		for(int l=0; l<2; l++) 
		{
			TabItem it = new TabItem(tabs, SWT.NONE);
			it.setText("Tab " + l);
			Composite c = new Composite(tabs, SWT.SINGLE);
			c.setLayout(new FillLayout());

			Tree addressTree = new Tree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			addressTree.setHeaderVisible(true);
			TreeViewer m_treeViewer = new TreeViewer(addressTree);

			TreeColumn column1 = new TreeColumn(addressTree, SWT.LEFT);
			addressTree.setLinesVisible(true);
			column1.setAlignment(SWT.LEFT);
			column1.setText("Folder/File");
			column1.setWidth(160);
			TreeColumn column2 = new TreeColumn(addressTree, SWT.RIGHT);
			column2.setAlignment(SWT.LEFT);
			column2.setText("num of lines");
			column2.setWidth(50);
			TreeColumn column3 = new TreeColumn(addressTree, SWT.RIGHT);
			column3.setAlignment(SWT.LEFT);
			column3.setText("num of lines");
			column3.setWidth(50);
			TreeColumn column4 = new TreeColumn(addressTree, SWT.RIGHT);
			column4.setAlignment(SWT.LEFT);
			column4.setText("num of lines");
			column4.setWidth(50);
			TreeColumn column5 = new TreeColumn(addressTree, SWT.RIGHT);
			column5.setAlignment(SWT.LEFT);
			column5.setText("num of lines");
			column5.setWidth(50);
			TreeColumn column6 = new TreeColumn(addressTree, SWT.RIGHT);
			column6.setAlignment(SWT.LEFT);
			column6.setText("num of lines");
			column6.setWidth(50);

			m_treeViewer.setContentProvider(new AddressContentProvider());
			m_treeViewer.setLabelProvider(new TableLabelProvider());
			List<Project> cities = new ArrayList<Project>();
			cities.add(new Project(2, "MyProject" + l));
			m_treeViewer.setInput(cities);
			m_treeViewer.expandAll();
			it.setControl(c);
			m_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
				
				@Override
				public void doubleClick(DoubleClickEvent event) {
					// TODO Auto-generated method stub
					showMessage("Double-click detected");
				}
			});
					
		}
		tabs.setSelection(0);


		//makeActions();
		hookSave();
	}

	//	private void makeActions() {
	//		doubleClickAction = new Action() {
	//			public void run() {
	//				TabItem [] items = tabs.getSelection();
	//				ISelection selection = 
	//				Object obj = ((IStructuredSelection)selection).getFirstElement();
	//				showMessage("Double-click detected on "+obj.toString());
	//			}
	//		};
	//	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
				tabs.getShell(),
				"Sample View",
				message);
	}

	private void hookSave(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// Example resource listener
		listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {

				System.out.println("Something changed!");

			}

		};

		workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//m_treeViewer.getControl().setFocus();
		tabs.isFocusControl();
	}

	class Project{
		Folder[]	folders;
		private String name; 

		public Project(int num_of_fold, String name_proj){
			name = name_proj;
			folders = new Folder[num_of_fold];
			for (int i = 0; i < folders.length; i++)
				folders[i] = new Folder(this, i, 5, "fold"+i);
		}

		public Folder[] getFolders(){
			return folders;
		}

		public String toString(){
			return name;
		}
	}

	class Folder{
		Project	Project;
		File[]	files;
		int		indx;
		private String name;

		public Folder(Project Project, int index, int num_files, String name_fold){
			this.Project = Project;
			name = name_fold;
			indx = index + 1;
			files = new File[num_files];
			for (int i = 0; i < files.length; i++)
				files[i] = new File(this, i, "file"+i);
		}

		public File[] getFiles(){
			return files;
		}

		public String toString(){
			return name;
		}
	}


	class File{
		Folder	Folder;
		int	indx;
		private String name;


		public File(Folder Folder, int i, String name_file){
			name = name_file;
			this.Folder = Folder;
			indx = i + 1;
		}

		public String toString(){
			return name;
		}

		public String getLines(int i){
			switch(i){
			case 1: 
				if (indx == 1)
					return "90";
				return "78";
			case 2: 
				if (indx == 1)
					return "94";
				return "73";
			case 3: 
				if (indx == 1)
					return "30";
				return "56";
			case 4: 
				if (indx == 1)
					return "45";
				return "98";
			case 5: 
				if (indx == 1)
					return "50";
				return "45";
			}
			return null;
		}
	}


	class AddressContentProvider implements ITreeContentProvider{
		public Object[] getChildren(Object parentElement){
			if (parentElement instanceof List)
				return ((List<?>) parentElement).toArray();
			if (parentElement instanceof Project)
				return ((Project) parentElement).getFolders();
			if (parentElement instanceof Folder)
				return ((Folder) parentElement).getFiles();
			return new Object[0];
		}

		public Object getParent(Object element){
			if (element instanceof Folder)
				return ((Folder) element).Project;
			if (element instanceof File)
				return ((File) element).Folder;
			return null;
		}

		public boolean hasChildren(Object element){
			if (element instanceof List)
				return ((List<?>) element).size() > 0;
				if (element instanceof Project)
					return ((Project) element).getFolders().length > 0;
					if (element instanceof Folder)
						return ((Folder) element).getFiles().length > 0;
						return false;
		}

		public Object[] getElements(Object cities){
			// cities ist das, was oben in setInput(..) gesetzt wurde.
			return getChildren(cities);
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