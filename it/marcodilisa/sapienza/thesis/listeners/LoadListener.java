package it.marcodilisa.sapienza.thesis.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import it.marcodilisa.sapienza.thesis.gui.LoaderFrame;
import it.marcodilisa.sapienza.thesis.gui.SolverFrame;

public class LoadListener implements ActionListener {
	
	private SolverFrame sf;

	public LoadListener(SolverFrame sf) {
		this.sf = sf;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LoaderFrame lf = new LoaderFrame();
		
		this.sf.dispatchEvent(new WindowEvent(this.sf, WindowEvent.WINDOW_CLOSING));

	}

}
