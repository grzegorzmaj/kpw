package com.nowakmaj.loc.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IAxisSet;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
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

	private int num;
	
	public mydialog(Shell parentShell, int number) {
		super(parentShell);
		
		num = number;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
//		String [] dates = new String[num];
//		for(int i=0; i<num; i++)
//			dates[i] = " num " + i;
		
		
		container.setLayout(new FillLayout());
		Chart chart = new Chart(container, SWT.NONE);
//		chart.getTitle().setText("SWT Chart");
//		chart.getAxisSet().getXAxis(0).getTitle().setText("Operating Systems");
//		chart.getAxisSet().getYAxis(0).getTitle().setText("Love");
//		IAxisSet axisSet = chart.getAxisSet();
//		IAxis xAxis = axisSet.getXAxis(0);
//		
//		
//		
//		xAxis.setCategorySeries(new String {"1","2", "3", "4"});
//		xAxis.enableCategory(true);
//
//		IBarSeries series = (IBarSeries) chart.getSeriesSet().createSeries(
//				SeriesType.BAR, "line series");
//		series.setBarColor(container.getDisplay().getSystemColor(SWT.COLOR_RED));
////		double[] values = new double[num];
////		for(int i=1; i<=num; i++)
////			values[i-1] = 0.5/i;
		double[] values = { 0.5, 0.3, 0.1, 0.1};
		ISeriesSet seriesSet = chart.getSeriesSet();
		ISeries series = seriesSet.createSeries(SeriesType.LINE, "line series");
		series.setYSeries(values);
		IAxisSet axisSet = chart.getAxisSet();
		axisSet.adjustRange();
		

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
