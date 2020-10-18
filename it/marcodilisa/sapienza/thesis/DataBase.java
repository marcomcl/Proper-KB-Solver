package it.marcodilisa.sapienza.thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.marcodilisa.sapienza.thesis.model.Atomic;
import it.marcodilisa.sapienza.thesis.model.Conjunctive;
import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.model.Negated;
import it.marcodilisa.sapienza.thesis.model.Term;
import it.marcodilisa.sapienza.thesis.parser.FOLParser;
import it.marcodilisa.sapienza.thesis.parser.ParseException;

public class DataBase extends KnowledgeBase {
	
	private Set<Term> constants;
	private Set<Formula> uniqueNames;
	private Map<String, Set<Formula>> closure; //Used to represent the closure axioms
	private Map<String, Set<List<Term>>> tuples; //Used to perform the selection of tuples

	public DataBase(List<Formula> f) throws ParseException {
		super(f);
		
		constants = new HashSet<Term>();
		uniqueNames = new HashSet<Formula>();
		closure = new HashMap<String, Set<Formula>>();
		tuples = new HashMap<String, Set<List<Term>>>(); 
		
		for(Formula form : formulas) {
			constants.addAll(form.constants());
			
			//Initialization of the closure axiom
			
			//Creation of the disjunction chain
			
			String predicate = ((Atomic) form).getPredicate().toString();
			
			Formula equal = null;
			List<Term> tuple = new ArrayList<Term>();
			int index = 1;
			for(Term c : ((Atomic) form).constantsList()) {
				String var = "V"+index;
				Formula eq = FOLParser.parse("eq("+var+","+c+")");
				if(index == 1) equal = eq;
				else equal = new Conjunctive(equal, eq);
				tuple.add(c);
				index++;
			}
			
			
			if(closure.get(predicate) == null) {
				closure.put(predicate, new HashSet<Formula>());
					
			}
			
			if(tuples.get(predicate) == null) {
				tuples.put(predicate, new HashSet<List<Term>>());
					
			}
			
			closure.get(predicate).add(equal);
			tuples.get(predicate).add(tuple);
		}
	
		//Unique Name Axioms Generation
		
		for(Term c1 : constants) {
			for(Term c2 : constants) {
				if(!c1.equals(c2)) {
					Negated f1 = new Negated(FOLParser.parse("eq("+c1+","+c2+")"));
					//Negated f2 = new Negated(FOLParser.parse("eq("+c2+","+c1+")"));
					
					uniqueNames.add(f1);
				}
			}
		}
	}
	
	public Set<Term> getConstants(){
		return (Set<Term>) constants;
	}
	
	public Set<Term> getTypedConstants(String pred){
		Set<Term> tc = new HashSet<Term>();
		Set<Formula> clos = closure.get(pred);
		for(Formula f : clos) tc.add(f.constants().iterator().next());
		return tc;
	}
	
	
	public Map<String, Set<Formula>> getClosure(){
		return (HashMap<String, Set<Formula>>) closure;
	}
	
	public Map<String, Set<List<Term>>> getTuples(){
		return (Map<String, Set<List<Term>>>) tuples;
	}
	
	public Set<Formula> getUniqueNames(){
		return (Set<Formula>) uniqueNames;
	}
	
	public boolean isIn(Formula f) {
		for(Formula form : this.uniqueNames) {
			if(f.equals(form)) return true;
		}
		return false;
	}
	
	/*private Formula toUniversal(Formula form) {
		Equivalence eq = (Equivalence) form;
		Atomic atom = (Atomic) eq.getSubformulaA();
		for(Term v : atom.freeVars()) {
			form = new Universal(form, v);
		}
		return form;
	}*/

}
