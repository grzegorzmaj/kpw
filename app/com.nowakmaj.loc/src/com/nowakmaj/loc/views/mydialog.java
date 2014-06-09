package com.nowakmaj.loc.views;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IAxisSet;
import org.swtchart.IAxisTick;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesLabel;
import org.swtchart.ISeriesSet;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class mydialog extends Dialog {


	ArrayList<String> dates;// = new ArrayList<String>();
	HashMap<String, String> changes;//= new HashMap<String, String>();

	public mydialog(Shell parentShell, ArrayList<String> Dates, HashMap<String, String> Changes) {
		super(parentShell);
		dates = Dates;
		changes = Changes;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);


		container.setLayout(new FillLayout());
		Chart chart = new Chart(container, SWT.NONE);
		chart.getTitle().setText("Wykres zmian LOC");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Kolejne zapisy");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Ilość linii");
		IAxisSet axisSet = chart.getAxisSet();
		
		int i = 0;
		double [] values = new double[dates.size()+2];

		values[i++] = 0;
		values[dates.size()+1] = 0; 
		for (String name: dates)
		{
			values[i++] = Double.parseDouble(changes.get(name));
		}

		IBarSeries barSeries = (IBarSeries) chart.getSeriesSet()
				.createSeries(SeriesType.BAR, "bar series");
		barSeries.setYSeries(values);

		// adjust the axis range
		barSeries.setBarPadding(4);
		barSeries.getLabel().setVisible(true);
		chart.getAxisSet().adjustRange();
		chart.getLegend().setVisible(false);

		
//				IAxisTick xTick = axisSet.getXAxis(0).getTick();
//				xTick.setVisible(false);


		return container;
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
