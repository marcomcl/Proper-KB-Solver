package it.marcodilisa.sapienza.thesis.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.parser.FOLParser;
import it.marcodilisa.sapienza.thesis.parser.ParseException;

public class AddFormListener implements ActionListener {
	
	private JTextField input;
	private JTextArea kbArea;
	private List<Formula> formulas;

	public AddFormListener(JTextField input, JTextArea kbArea, List<Formula> formulas) {
		this.input = input;
		this.kbArea = kbArea;
		this.formulas = formulas;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		kbArea.append("- " + input.getText() + "\n");
		try {
			formulas.add(FOLParser.parse(input.getText()));
		} catch (ParseException e1) {
			JOptionPane.showMessageDialog(null,
				    "The formula is not valid!",
				    "Error", JOptionPane.ERROR_MESSAGE);
		}
		input.setText("");
	}

}
