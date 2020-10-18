package it.marcodilisa.sapienza.thesis;

import java.util.ArrayList;
import java.util.List;

import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.parser.FOLParser;
import it.marcodilisa.sapienza.thesis.parser.ParseException;



public class KnowledgeBase {
	protected ArrayList<Formula> formulas;
	
	public KnowledgeBase(List<Formula> f){
		
		formulas = new ArrayList<Formula>();
		
		for(Formula form : f) {
			formulas.add(form);
		}
		
	}
	
	public KnowledgeBase() {
		formulas = new ArrayList<Formula>();
	}

	public ArrayList<Formula> getFormulas() throws ParseException{
		ArrayList<Formula> f = new ArrayList<Formula>();
		for(Formula form : formulas) {
			f.add(FOLParser.parse(form.toString()));
		}	
		return f;
	}

}
