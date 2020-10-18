package it.marcodilisa.sapienza.thesis.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import it.marcodilisa.sapienza.thesis.DBSolver;
import it.marcodilisa.sapienza.thesis.DataBase;
import it.marcodilisa.sapienza.thesis.KnowledgeBase;
import it.marcodilisa.sapienza.thesis.NullSolver;
import it.marcodilisa.sapienza.thesis.ProperKB;
import it.marcodilisa.sapienza.thesis.Solver;
import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.model.Term;
import it.marcodilisa.sapienza.thesis.parser.FOLParser;
import it.marcodilisa.sapienza.thesis.parser.ParseException;
import it.marcodilisa.sapienza.thesis.parser.TokenMgrError;

public class SubmitListener implements ActionListener {
	
	private JTextField input;
	private JTextArea outputArea;
	private Formula form;
	private KnowledgeBase kb;

	public SubmitListener(JTextField input, JTextArea outputArea, KnowledgeBase kb) {
		this.input = input;
		this.outputArea = outputArea;
		this.kb = kb;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String txt =  input.getText();
		if(!txt.equals("")){
			if(kb.getClass().equals(ProperKB.class)) {
				try {
					form = FOLParser.parse(txt);
				
					float res = -1.0f;
					if(((ProperKB) kb).hasNulls()) {
						long start  = System.currentTimeMillis();
						res = NullSolver.Solve((ProperKB)kb,form);
						long finish = System.currentTimeMillis();
						System.out.println("Time: " + (finish-start));
					}
					else {
						long start  = System.currentTimeMillis();
						res = Solver.V((ProperKB)kb,form);
						long finish = System.currentTimeMillis();
						System.out.println("Time: " + (finish-start));
					}
					if(res == 0.0) {
						System.out.println("- The query " + form + " is not entailed by the KB!");
						this.outputArea.append("- The query " + form + " is not entailed by the KB!\n");
						JOptionPane.showMessageDialog(null,
							    "The query is not entailed by the KB!",
							    "Output", JOptionPane.WARNING_MESSAGE);
					}
					else if (res == 0.5) {
						System.out.println("- Not enough information in KB for the query "+form);
						this.outputArea.append("- "+ form + " : don't know.\n");
						JOptionPane.showMessageDialog(null,
							    "Not enough information in KB!",
							    "Output",
							    JOptionPane.WARNING_MESSAGE);
					}
					else if (res == 1.0) {
						System.out.println("- The query " + form + " is entailed by the KB!");
						this.outputArea.append("- The query " + form + " is entailed by the KB!\n");
						JOptionPane.showMessageDialog(null,
							    "The query is entailed by the KB!",
							    "Output", JOptionPane.INFORMATION_MESSAGE);	
					}
					
				}catch(ParseException e1){
					this.outputArea.setText("- The query is not valid!\n");
					JOptionPane.showMessageDialog(null,
						    "The query is not valid!",
						    "Error", JOptionPane.ERROR_MESSAGE);
				}catch(TokenMgrError e1){
					this.outputArea.setText("- The query is not valid!\n");
					JOptionPane.showMessageDialog(null,
						    "The query is not valid!",
						    "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			if(kb.getClass().equals(DataBase.class)) {
				try {
					String[] text = txt.split("/");
					List<Formula> types = new ArrayList<Formula>();
					if(text.length > 1) {
						for(String s : text[0].split(",")) {
							types.add(FOLParser.parse(s));
						}
					}
					form = FOLParser.parse(text[text.length - 1]);
					Formula query = DBSolver.simplify(form);
					long start = System.currentTimeMillis();
					Set<List<Term>> res = DBSolver.queryDB((DataBase)kb, query, types);
					long finish = System.currentTimeMillis();
					System.out.println("Time: " + (finish-start));
					this.outputArea.append("- "+ txt + " : " + res.toString() + "\n");
					JOptionPane.showMessageDialog(null,
							"Result: " + res.toString() + "\n",
						    "Output", JOptionPane.INFORMATION_MESSAGE);	
				}catch(ParseException e1){
					this.outputArea.setText("- Formula is not valid!\n");
				}
			}
			input.setText("");
		}

	}

}
