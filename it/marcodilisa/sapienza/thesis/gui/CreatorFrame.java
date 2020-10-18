package it.marcodilisa.sapienza.thesis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import it.marcodilisa.sapienza.thesis.listeners.AddFormListener;
import it.marcodilisa.sapienza.thesis.listeners.CancelListener;
import it.marcodilisa.sapienza.thesis.listeners.SaveListener;
import it.marcodilisa.sapienza.thesis.model.Formula;

public class CreatorFrame extends JFrame {
	
	private JTextField input;
	private JButton addForm;
	private JButton save;
	private JButton cancel;
	private JPanel centerPanel;
	
	private JTextArea kbArea;
	private JPanel northPanel;
	
	private JPanel southPanel;
	
	private List<Formula> formulas;
	private JTextField filename;
	
	
	public CreatorFrame() {
		
		formulas = new ArrayList<Formula>();
		
		//Create Main Container
		Container main = this.getContentPane();
		main.setLayout(new BorderLayout());
		
		centerPanel = new JPanel();
		
		//Input Area
		southPanel = new JPanel();
		input = new JTextField(20) {
		    public void addNotify() {
		        super.addNotify();
		        requestFocus();
		    }
		};
		filename = new JTextField(10);

		TextPrompt tp = new TextPrompt("Insert Formula", input); //Used for the placeholder
		TextPrompt tpn = new TextPrompt("Save As", filename);
		addForm = new JButton("Add");
		save = new JButton("Save");
		cancel = new JButton("Cancel");

		
		//Set the addForm button
		addForm.setBorder(new RoundedBorder(2));
		
		save.setBorder(new RoundedBorder(2));
		ActionListener saveList = new SaveListener(this, formulas, filename);
		save.addActionListener(saveList);
		
		ActionListener cancelList = new CancelListener(this);
		cancel.addActionListener(cancelList);
		
		//Add the button and the input field to the panel
		southPanel.add(input);
		southPanel.add(filename);
		southPanel.add(addForm);
		southPanel.add(save);
		southPanel.add(cancel);
		
		//Knowledge Base Area
		northPanel = new JPanel();
		kbArea = new JTextArea(20, 45);
		
		//Append the formulas in the area
		kbArea.append("------------------------------------\n Knowledge Base\n ------------------------------------\n");
		
		
		kbArea.setEditable(false);
		
		ActionListener addFormList = new AddFormListener(input, kbArea, formulas);
		addForm.addActionListener(addFormList);
		
		//Set the borders of the panels
		northPanel.setBorder(new EmptyBorder(20, 15, 0, 15));
		centerPanel.setBorder(new EmptyBorder(20, 15, 0, 15));
		southPanel.setBorder(new EmptyBorder(20, 15, 0, 15));
		
		JScrollPane scroll = new JScrollPane (kbArea, 
				   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		northPanel.add(scroll);
		
		//Set the backgrounds of the 3 panels
		centerPanel.setBackground(Color.decode("#dceffb"));
		northPanel.setBackground(Color.decode("#dceffb"));
		southPanel.setBackground(Color.decode("#dceffb"));
		
		main.add(centerPanel, BorderLayout.CENTER);
		main.add(northPanel, BorderLayout.NORTH);
		main.add(southPanel, BorderLayout.SOUTH);
		main.setPreferredSize(new Dimension(600,400));
		
		this.setTitle("FOL Solver");
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.getRootPane().setDefaultButton(addForm);
	}

}
