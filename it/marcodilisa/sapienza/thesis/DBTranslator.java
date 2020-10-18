package it.marcodilisa.sapienza.thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import it.marcodilisa.sapienza.thesis.model.Atomic;
import it.marcodilisa.sapienza.thesis.model.Conjunctive;
import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.model.Implicative;
import it.marcodilisa.sapienza.thesis.model.Negated;
import it.marcodilisa.sapienza.thesis.model.Predicate;
import it.marcodilisa.sapienza.thesis.model.Term;
import it.marcodilisa.sapienza.thesis.model.Universal;
import it.marcodilisa.sapienza.thesis.parser.FOLParser;
import it.marcodilisa.sapienza.thesis.parser.ParseException;

public abstract class DBTranslator {
	public static ProperKB translate(DataBase db) throws ParseException{
		
		HashMap<String, Set<Formula>> ext = (HashMap<String, Set<Formula>>) db.getClosure();
		List<Formula> formulas = new ArrayList<Formula>();
		int stdNumber = 1;
		HashMap<String, Term> stdNames = new HashMap<String, Term>();
		
		for(String pred : ext.keySet()) {
			Formula cwa = null; // Closed World Assumption Formula
			ArrayList<Term> freeVars = null;
			for(Formula f : ext.get(pred)) {
				freeVars = new ArrayList<Term>();
				for(Term t : f.freeVars()) {
					freeVars.add(t);
				}
				
				// Transforming variables into standard names
				
				Formula newForm = FOLParser.parse(f.toString());
				for(Term c : f.constants()) {
					if(stdNames.containsKey(c.toString())) {
						newForm .substitute(stdNames.get(c.toString()), c);
					}
					else {
						Term n = generateStdName(stdNumber);
						stdNames.put(c.toString(), n);
						stdNumber++;
						newForm.substitute(n, c);
					}
				}
				
				// Closed World Assumption
				Formula newFormNeg = new Negated(newForm);
				cwa = (cwa == null) ? newFormNeg : new Conjunctive(cwa, newFormNeg);
				
				
				// Translation of the facts into Proper KB formulas
				
				Implicative impl = new Implicative(newForm,new Atomic(new Predicate(pred), freeVars));
				int count = freeVars.size() - 1;
				Universal univ = new Universal(impl, freeVars.get(count));
				for(int i = count - 1; i >= 0; i--) {
					univ = new Universal(univ, freeVars.get(i));
				}
				formulas.add(univ);
			}
			int count = freeVars.size() - 1;
			cwa = new Implicative(cwa, new Negated(new Atomic(new Predicate(pred), freeVars)));
			cwa = new Universal(cwa, freeVars.get(count));
			for(int i = count - 1; i >= 0; i--) {
				cwa = new Universal(cwa, freeVars.get(i));
			}
			formulas.add(cwa);
		}
			
		
		return new ProperKB(formulas);
	}
	
	private static Term generateStdName(int i) {
		return new Term("n" + i);
	}
	
}
