package it.marcodilisa.sapienza.thesis.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import it.marcodilisa.sapienza.thesis.gui.CreatorFrame;
import it.marcodilisa.sapienza.thesis.gui.LoaderFrame;
import it.marcodilisa.sapienza.thesis.model.Formula;

public class SaveListener implements ActionListener {

	private List<Formula> formulas;
	private CreatorFrame cf;
	private JTextField filename;

	public SaveListener(CreatorFrame cf, List<Formula> formulas, JTextField filename) {
		this.formulas = formulas;
		this.cf = cf;
		this.filename = filename;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(filename.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
				    "Please, provide a name!",
				    "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
		      File myObj = new File(filename.getText() + ".txt");
		      if (myObj.createNewFile()) {
		        this.cf.dispatchEvent(new WindowEvent(this.cf, WindowEvent.WINDOW_CLOSING));
		        JOptionPane.showMessageDialog(null,
					    "File saved successfully!",
					    "Success", JOptionPane.INFORMATION_MESSAGE);
		        LoaderFrame lf = new LoaderFrame();
		      } else {
		    	  JOptionPane.showMessageDialog(null,
						    "File already exists!",
						    "Warning", JOptionPane.WARNING_MESSAGE);
		    	  return;
		      }
	    } catch (IOException ex) {
	      System.out.println("An error occurred.");
	      ex.printStackTrace();
	      return;
	    }
		
		try {
		      FileWriter fw = new FileWriter(filename.getText() + ".txt");
		      for(Formula f : formulas) {
		    	  fw.append(f.toString() + "\n");
		      }
		      fw.close();
	    } catch (IOException ex) {
	      System.out.println("An error occurred.");
	      ex.printStackTrace();
	    }

	}

}
