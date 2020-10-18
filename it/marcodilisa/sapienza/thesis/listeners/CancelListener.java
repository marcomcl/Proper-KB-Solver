package it.marcodilisa.sapienza.thesis.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import it.marcodilisa.sapienza.thesis.gui.CreatorFrame;
import it.marcodilisa.sapienza.thesis.gui.LoaderFrame;

public class CancelListener implements ActionListener {

	private CreatorFrame cf;

	public CancelListener(CreatorFrame cf) {

		this.cf = cf;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		this.cf.dispatchEvent(new WindowEvent(this.cf, WindowEvent.WINDOW_CLOSING));
		LoaderFrame lf = new LoaderFrame();

	}

}
