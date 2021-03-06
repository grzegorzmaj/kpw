/*
 * Plik mydialog.java z paczki com.nowakmaj.loc.views
 * 
 * Klasa mydialog odpowida za nowe okna wyświetlające wykresy.
 * 
 * dates - lista dat zmian dla danego pliku
 * dates2 - lista dat zmian wszytskich w tym projekcie
 * changes - mapa zmian dla danego pliku
 * changesLocpf - mapa zmian LocPF dla projektu
 * tabs - zakładki
 * 
 * Poszczególne metody opisane są w kodzie.
 * 
 * */

package com.nowakmaj.loc.views;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IAxisSet;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries.SeriesType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/* 
* Klasa mydialog jest rozszerzeniem Dialog umożliwia to wyświetlanie okna.
* */

public class mydialog extends Dialog {


	ArrayList<String> dates;
	ArrayList<String> dates2;
	HashMap<String, String> changes;
	HashMap<String, String> changesLocpf;
	TabFolder tabs;

	/* 
	 * Konstruktor.
	 */
	public mydialog(Shell parentShell, ArrayList<String> DatesLOC, ArrayList<String> DatesLOCPF, HashMap<String, String> Changes, HashMap<String, String> ChangesLocpf) {
		super(parentShell);
		dates = DatesLOC;
		dates2 = DatesLOCPF;
		changes = Changes;
		changesLocpf = ChangesLocpf;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * 
	 * createDialogArea tworzy obszar wykresu.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = (Composite) super.createDialogArea(parent);
		c.setLayout(new FillLayout());
		tabs = new TabFolder(c, SWT.UP);

		
		// LOC tab
		TabItem it = new TabItem(tabs, SWT.NONE);
		it.setText("LOC");
		Composite container = new Composite(tabs, SWT.SINGLE);//(Composite) super.createDialogArea(tabs);
		container.setLayout(new FillLayout());
		it.setControl(container);

		container.setLayout(new FillLayout());
		Chart chart = new Chart(container, SWT.NONE);
		chart.getTitle().setText("Wykres zmian LOC");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Kolejne zapisy");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Ilosc linii");
		IAxisSet axisSet = chart.getAxisSet();
		IAxis xAxis = axisSet.getXAxis(0);
		
		int i = 0;
		double [] values = new double[dates.size()+2];
		String [] labels = new String[dates.size()+2];
		labels[i]=" ";
		values[i++] = 0;
		values[dates.size()+1] = 0; 
		labels[dates.size()+1] = " "; 
		for (String name: dates)
		{
			labels[i] = Integer.toString(i);
			values[i] = Double.parseDouble(changes.get(name));
			if(values[i++]==-1)
				values[i-1]=0;
		}

		IBarSeries barSeries = (IBarSeries) chart.getSeriesSet()
				.createSeries(SeriesType.BAR, "bar series");
		barSeries.setYSeries(values);
		

		xAxis.setCategorySeries(labels);
		xAxis.enableCategory(true);

		// adjust the axis range
		barSeries.setBarPadding(4);
		barSeries.getLabel().setVisible(true);
		chart.getAxisSet().adjustRange();
		chart.getLegend().setVisible(false);
		
		

		
		// LOCPF tab
		TabItem it2 = new TabItem(tabs, SWT.NONE);
		it2.setText("LOCPF");
		Composite container2 = new Composite(tabs, SWT.SINGLE);//(Composite) super.createDialogArea(tabs);
		container2.setLayout(new FillLayout());
		it2.setControl(container2);

		container2.setLayout(new FillLayout());
		Chart chart2 = new Chart(container2, SWT.NONE);
		chart2.getTitle().setText("Wykres zmian LOCPF");
		chart2.getAxisSet().getXAxis(0).getTitle().setText("Kolejne zapisy");
		chart2.getAxisSet().getYAxis(0).getTitle().setText("Ilosc linii na plik");
		IAxisSet axisSet2 = chart2.getAxisSet();
		IAxis xAxis2 = axisSet2.getXAxis(0);
		int i2 = 0;
		double [] values2 = new double[dates2.size()+2];
		String [] labels2 = new String[dates2.size()+2];
		labels2[i2]=" ";
		labels2[dates2.size()+1] = " ";
			
		values2[i2++] = 0;
		values2[dates2.size()+1] = 0; 
		for (String name2: dates2)
		{
			values2[i2] = Double.parseDouble(changesLocpf.get(name2));
			labels2[i2] = Integer.toString(i2);
			if(values2[i2++]==-1)
				values2[i2-1]=0;
		}

		IBarSeries barSeries2 = (IBarSeries) chart2.getSeriesSet()
				.createSeries(SeriesType.BAR, "bar series2");
		barSeries2.setYSeries(values2);

		xAxis2.setCategorySeries(labels2);
		xAxis2.enableCategory(true);
		
		// adjust the axis range
		barSeries2.setBarPadding(4);
		barSeries2.getLabel().setVisible(true);
		chart2.getAxisSet().adjustRange();
		chart2.getLegend().setVisible(false);



		return c;
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 * 
	 * configureShell umożliwia zmianę tytułu okna.
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Chart Information");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
	 * 
	 * getInitialSize ustawia początkowy rozmiar okna.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}






}
