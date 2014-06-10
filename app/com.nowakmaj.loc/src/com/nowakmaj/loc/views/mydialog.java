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


public class mydialog extends Dialog {


	ArrayList<String> dates;// = new ArrayList<String>();
	HashMap<String, String> changes;//= new HashMap<String, String>();
	HashMap<String, String> changesLocpf;
	TabFolder tabs;

	public mydialog(Shell parentShell, ArrayList<String> Dates, HashMap<String, String> Changes, HashMap<String, String> ChangesLocpf) {
		super(parentShell);
		dates = Dates;
		changes = Changes;
		changesLocpf = ChangesLocpf;
	}

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
		chart.getAxisSet().getYAxis(0).getTitle().setText("Ilość linii");
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
			values[i++] = Double.parseDouble(changes.get(name));
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
		
		

		
		// LOPF tab
		TabItem it2 = new TabItem(tabs, SWT.NONE);
		it2.setText("LOCPF");
		Composite container2 = new Composite(tabs, SWT.SINGLE);//(Composite) super.createDialogArea(tabs);
		container2.setLayout(new FillLayout());
		it2.setControl(container2);

		container2.setLayout(new FillLayout());
		Chart chart2 = new Chart(container2, SWT.NONE);
		chart2.getTitle().setText("Wykres zmian LOCPF");
		chart2.getAxisSet().getXAxis(0).getTitle().setText("Kolejne zapisy");
		chart2.getAxisSet().getYAxis(0).getTitle().setText("Ilość linii na plik");
		IAxisSet axisSet2 = chart2.getAxisSet();
		IAxis xAxis2 = axisSet2.getXAxis(0);
		int i2 = 0;
		double [] values2 = new double[dates.size()+2];

		values[i2++] = 0;
		values[dates.size()+1] = 0; 
		for (String name: dates)
		{
			values2[i2++] = Double.parseDouble(changesLocpf.get(name));
		}

		IBarSeries barSeries2 = (IBarSeries) chart2.getSeriesSet()
				.createSeries(SeriesType.BAR, "bar series2");
		barSeries2.setYSeries(values2);

		xAxis2.setCategorySeries(labels);
		xAxis2.enableCategory(true);
		
		// adjust the axis range
		barSeries2.setBarPadding(4);
		barSeries2.getLabel().setVisible(true);
		chart2.getAxisSet().adjustRange();
		chart2.getLegend().setVisible(false);



		return c;
	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Chart Information");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 400);
	}






}
