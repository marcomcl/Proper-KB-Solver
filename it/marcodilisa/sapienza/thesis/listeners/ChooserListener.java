package it.marcodilisa.sapienza.thesis.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import it.marcodilisa.sapienza.thesis.NullSolver;
import it.marcodilisa.sapienza.thesis.gui.LoaderFrame;
import it.marcodilisa.sapienza.thesis.gui.SolverFrame;
import it.marcodilisa.sapienza.thesis.parser.ParseException;

public class ChooserListener implements ActionListener {
	
	private ButtonGroup radio;
	private LoaderFrame lf;
	
	public ChooserListener(ButtonGroup radio, LoaderFrame lf) {
		this.radio = radio;
		this.lf = lf;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		int n = fileChooser.showOpenDialog(fileChooser);
		
		if(n == JFileChooser.APPROVE_OPTION) {
			this.lf.dispatchEvent(new WindowEvent(this.lf, WindowEvent.WINDOW_CLOSING));
			String sel = radio.getSelection().getActionCommand();
			boolean translate = false;
			if(sel == "Database") {
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog (null, "Would You Like to Translate Your DB in a Proper KB?","Warning",dialogButton);
				if(dialogResult == JOptionPane.YES_OPTION) translate = true;
			}
	        File f = fileChooser.getSelectedFile();
	        try {
	        	NullSolver.T = null;
				SolverFrame sf = new SolverFrame(f, sel, translate);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
	}

}
