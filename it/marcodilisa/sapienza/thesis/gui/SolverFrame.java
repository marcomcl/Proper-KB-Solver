package it.marcodilisa.sapienza.thesis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import it.marcodilisa.sapienza.thesis.DBTranslator;
import it.marcodilisa.sapienza.thesis.DataBase;
import it.marcodilisa.sapienza.thesis.KnowledgeBase;
import it.marcodilisa.sapienza.thesis.ProperKB;
import it.marcodilisa.sapienza.thesis.listeners.LoadListener;
import it.marcodilisa.sapienza.thesis.listeners.SubmitListener;
import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.parser.FOLParser;
import it.marcodilisa.sapienza.thesis.parser.ParseException;

public class SolverFrame extends JFrame{
	
	private JTextField input;
	private JButton submit;
	private JButton load;
	private JPanel centerPanel;
	
	private JTextArea kbArea;
	private JTextArea outputArea;
	private JPanel northPanel;
	
	private JPanel southPanel;
	
	private KnowledgeBase kb;
	
	public SolverFrame(File f, String sel, boolean translate) throws IOException, ParseException {
		
		
		//Create Main Container
		Container main = this.getContentPane();
		main.setLayout(new BorderLayout());
		
		List<Formula> formulas = new ArrayList<Formula>();
		  
		BufferedReader br = new BufferedReader(new FileReader(f)); 
		  
		String st; 
		while ((st = br.readLine()) != null) {
			Formula form = FOLParser.parse(st); 
			formulas.add(form);
		}
		br.close();
		
		//Create Knowledge Base Object
		if(sel == "Proper KB")
			kb = new ProperKB(formulas);
		else {
			kb = new DataBase(formulas);
			if(translate)
				kb = DBTranslator.translate((DataBase) kb);
			else
				kb = (DataBase) kb;
			
		}
		
		centerPanel = new JPanel();
		
		//Input Area
		southPanel = new JPanel();
		input = new JTextField(30) {
		    public void addNotify() {
		        super.addNotify();
		        requestFocus();
		    }
		};
		TextPrompt tp = new TextPrompt("Insert Formula", input); //Used for the placeholder
		submit = new JButton("Solve");
		load = new JButton("Load New");
		

		outputArea = new JTextArea(5, 45);
		JScrollPane scrollOutput = new JScrollPane (outputArea, 
				   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//Set the submit button
		submit.setBorder(new RoundedBorder(2));
		ActionListener submitList = new SubmitListener(input, outputArea, kb);
		submit.addActionListener(submitList);
		
		load.setBorder(new RoundedBorder(2));
		ActionListener loadList = new LoadListener(this);
		load.addActionListener(loadList);
		
		//Add the button and the input field to the panel
		southPanel.add(input);
		southPanel.add(submit);
		southPanel.add(load);
		
		//Knowledge Base Area
		northPanel = new JPanel();
		kbArea = new JTextArea(10, 45);
		
		//Append the formulas in the area
		kbArea.append("------------------------------------\n Knowledge Base\n ------------------------------------\n");
		Iterator<Formula> it = kb.getFormulas().iterator();
		while(it.hasNext()) {
			kbArea.append("- " + it.next().toString() + "\n");
		}
		
		outputArea.append("------------------------------------\n Log Output\n ------------------------------------\n");
		
		kbArea.setEditable(false);
		outputArea.setEditable(false);
		
		//Set the borders of the panels
		northPanel.setBorder(new EmptyBorder(20, 15, 0, 15));
		centerPanel.setBorder(new EmptyBorder(20, 15, 0, 15));
		southPanel.setBorder(new EmptyBorder(20, 15, 0, 15));
		
		JScrollPane scroll = new JScrollPane (kbArea, 
				   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		northPanel.add(scroll);
		centerPanel.add(scrollOutput);
		
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
		this.getRootPane().setDefaultButton(submit);
	}
	
}
