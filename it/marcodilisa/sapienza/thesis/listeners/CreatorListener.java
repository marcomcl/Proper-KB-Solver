package it.marcodilisa.sapienza.thesis.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import it.marcodilisa.sapienza.thesis.gui.CreatorFrame;
import it.marcodilisa.sapienza.thesis.gui.LoaderFrame;

public class CreatorListener implements ActionListener {
	
	private LoaderFrame lf;

	public CreatorListener(LoaderFrame lf) {
		this.lf = lf;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		this.lf.dispatchEvent(new WindowEvent(this.lf, WindowEvent.WINDOW_CLOSING));
		CreatorFrame cf = new CreatorFrame();
		
	}

}
