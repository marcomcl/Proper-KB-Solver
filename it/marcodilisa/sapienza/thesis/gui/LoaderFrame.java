package it.marcodilisa.sapienza.thesis.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import it.marcodilisa.sapienza.thesis.listeners.ChooserListener;
import it.marcodilisa.sapienza.thesis.listeners.CreatorListener;
import it.marcodilisa.sapienza.thesis.listeners.SubmitListener;

public class LoaderFrame extends JFrame{
	
	private JRadioButton proper;
	private JRadioButton database;
	private ButtonGroup radio;
	private JPanel northPanel;
	private JPanel centerPanel;
	private JButton chooser;
	private JButton creator;
	
	public LoaderFrame() {
		
		//Create Main Container
		Container main = this.getContentPane();
		main.setLayout(new BorderLayout());
		
		proper = new JRadioButton("Proper KB", true);
		proper.setActionCommand( proper.getText() );
		database = new JRadioButton("Database", false);
		database.setActionCommand( database.getText() );
		radio = new ButtonGroup();
		chooser = new JButton("Load File");
		creator = new JButton("New File");
		northPanel = new JPanel();
		centerPanel = new JPanel();
		
		ActionListener chooseList = new ChooserListener(radio, this);
		chooser.addActionListener(chooseList);
		
		ActionListener newFileList = new CreatorListener(this);
		creator.addActionListener(newFileList);
		
		radio.add(proper);
		radio.add(database);
		northPanel.add(proper);
		northPanel.add(database);
		centerPanel.add(creator);
		centerPanel.add(chooser);
		main.add(northPanel, BorderLayout.NORTH);
		main.add(centerPanel, BorderLayout.CENTER);
		main.setPreferredSize(new Dimension(260,80));
		
		
		this.setTitle("FOL Solver");
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
