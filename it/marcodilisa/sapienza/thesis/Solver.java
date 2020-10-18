package it.marcodilisa.sapienza.thesis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import it.marcodilisa.sapienza.thesis.model.Atomic;
import it.marcodilisa.sapienza.thesis.model.Conjunctive;
import it.marcodilisa.sapienza.thesis.model.Disjunctive;
import it.marcodilisa.sapienza.thesis.model.Equivalence;
import it.marcodilisa.sapienza.thesis.model.Existential;
import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.model.Implicative;
import it.marcodilisa.sapienza.thesis.model.Negated;
import it.marcodilisa.sapienza.thesis.model.Term;
import it.marcodilisa.sapienza.thesis.model.Universal;
import it.marcodilisa.sapienza.thesis.parser.FOLParser;
import it.marcodilisa.sapienza.thesis.parser.ParseException;
import it.marcodilisa.sapienza.thesis.gui.LoaderFrame;



public class Solver {
	
	public static void main(String[] args) throws IOException, ParseException {
		
		LoaderFrame lf = new LoaderFrame(); 

	}
	
	public static float V(ProperKB kb, Formula form) throws ParseException {
		
		// Atomic Formula
		
		if(form.getClass().equals(Atomic.class)) {
			Atomic atom = (Atomic) form;
			if(atom.getPredicate().toString().equals("eq")) {
				Iterator<Term> it = atom.standardNamesList().iterator();
				Term stdNameA = it.next();
				Term stdNameB = it.next();
				float res = (stdNameA.equals(stdNameB)) ? 1.0f : 0.0f;
				return res;
			}else
				return Solver.isInKnowledgeBase(kb, atom);
		}
		
		// Negated Formula
		
		if(form.getClass().equals(Negated.class)) {
			Formula subForm = ((Negated) form).getSubformula();
			float res;
			if(subForm.getClass().equals(Negated.class))
				res = Solver.V(kb, ((Negated) subForm).getSubformula());
			else
				res = Math.abs(1 - Solver.V(kb, subForm));
			return res;
		}
		
		// Conjunctive Formula
		
		if(form.getClass().equals(Conjunctive.class)) {
			Conjunctive conj = (Conjunctive) form;
			Formula subA = conj.getSubformulaA();
			Formula subB = conj.getSubformulaB();
			float resA = Solver.V(kb, subA);
			float resB = Solver.V(kb, subB);
			float res = Solver.min(resA, resB);
			return res;
		}
		
		// Disjunctive Formula
		
		if(form.getClass().equals(Disjunctive.class)) {
			Disjunctive disj = (Disjunctive) form;
			Formula subA = disj.getSubformulaA();
			Formula subB = disj.getSubformulaB();
			float resA = Solver.V(kb, subA);
			float resB = Solver.V(kb, subB);
			return Solver.max(resA, resB);
		}
		
		// Implicative Formula
		
		if(form.getClass().equals(Implicative.class)) {
			Implicative impl = (Implicative) form;
			Formula subA = new Negated(impl.getPremise());
			Formula subB = impl.getConclusion();
			return Solver.V(kb, new Disjunctive(subA, subB));
		}
		
		// Equivalence Formula
		
		if(form.getClass().equals(Equivalence.class)) {
			Equivalence equiv = (Equivalence) form;
			Implicative impl1 = new Implicative(equiv.getSubformulaA(), equiv.getSubformulaB());
			Implicative impl2 = new Implicative(equiv.getSubformulaB(), equiv.getSubformulaA());
			return Solver.V(kb, new Conjunctive(impl1, impl2));
		}
		
		// Universal Formula
		
		if(form.getClass().equals(Universal.class)) {
			Universal univ = (Universal) form;
			
			//Generate the Standard Name Set
			
			Set<Term> H = new HashSet<Term>();
			H.addAll(kb.getStdNames());
			H.addAll(form.standardNames());
			Solver.newStdName(H);
			
			//Clone the formula to substitute the terms
			
			Formula f = ((Universal) FOLParser.parse(form.toString())).getSubformula();
			
			Iterator<Term> it = H.iterator();
			
			f.substitute(it.next(), univ.getQuantifiedVar());
			
			float res = Solver.V(kb, f);
			
			while(it.hasNext()) {
				f = ((Universal) FOLParser.parse(form.toString())).getSubformula();
				f.substitute(it.next(), univ.getQuantifiedVar());
				res = Solver.min(res, Solver.V(kb, f));
			}
			
			return res;
			
		}
		
		// Existential Formula
		
		if(form.getClass().equals(Existential.class)) {
			System.out.println("- Evaluating "+form.getClass().getSimpleName()+" query: "+form);
			Existential exist = (Existential) form;
			
			//Generate the Standard Name Set
			
			Set<Term> H = new HashSet<Term>();
			H.addAll(kb.getStdNames());
			H.addAll(form.standardNames());
			Solver.newStdName(H);
			
			//Clone the formula to substitute the terms
			
			Formula f = ((Existential) FOLParser.parse(form.toString())).getSubformula();
			
			Iterator<Term> it = H.iterator();
			Term n = it.next();
			f.substitute(n, exist.getQuantifiedVar());
			float res = Solver.V(kb, f);
			
			System.out.println("Standard name: "+n);
			System.out.println("Result: "+res);
			
			while(it.hasNext()) {
				n = it.next();
				f = ((Existential) FOLParser.parse(form.toString())).getSubformula();
				f.substitute(n, exist.getQuantifiedVar());
				res = Solver.max(res, Solver.V(kb, f));
				System.out.println("Standard name: "+n);
				System.out.println("Result: "+res);
			}
			
			return res;
			
		}
		
		return 0;
	}
	
	private static void newStdName(Set<Term> h) {
		Boolean done = false;
		int i = 1;
		while(!done) {
			Term t = new Term("n"+i);
			if(h.contains(t))
				i++;
			else {
				h.add(t);
				done = true;
			}
		}
		
	}

	private static float max(float resA, float resB) {
		float res = (resA >= resB) ? resA : resB;
		return res;
	}

	private static float min(float resA, float resB) {
		float res = (resA <= resB) ? resA : resB;
		return res;
	}

	private static float isInKnowledgeBase(ProperKB kb, Atomic form) throws ParseException{
		
		Iterator<Formula> it = kb.getFormulas().iterator();
		while(it.hasNext()) {
			
			//Exploit the Proper KB structure to get the different parts of the formula
			// forall ([Conjunction of equality axioms] -> [Atomic Formula / Negated Atomic Formula]) !! TODO: Manage more complex conclusions!
			
			//Get the next formula in the KB
			
			Universal next = (Universal) it.next();
			
			List<Term> qVars = new LinkedList<Term>();
			
			// Get the nested universal formulas ( forall X. forall Y ...)
			
			while(next.getClass().equals(Universal.class)) {
				qVars.add(next.getQuantifiedVar());
				if(next.getSubformula().getClass().equals(Universal.class))
					next = (Universal) next.getSubformula();
				else
					break;
			}
			
			
			//Get the implicative formula
			
			Implicative impl = (Implicative) next.getSubformula();
			
			//Get the premise (intersection of equality axioms) and the conclusion
			
			Formula prem = impl.getPremise();
			Formula concl = impl.getConclusion();
			
			
			//Get the atomic conclusion and check whether it is negated or not
			boolean negConcl = false;
			Atomic nConcl;
			if(concl.getClass().equals(Negated.class)) {
				negConcl = true;
				nConcl = (Atomic) ((Negated) concl).getSubformula();
			}
			else
				nConcl = (Atomic) concl;
			
			//Check if the conclusion predicate is the same of the formula we want to check
			
			if(nConcl.getPredicate().toString().equals(form.getPredicate().toString()) && nConcl.arity() == form.arity()) {
				
				//Divide the premise in a set of atomic equality axioms
				
				List<Formula> todo = new ArrayList<Formula>();
				List<Formula> axioms = new ArrayList<Formula>();
				
				todo.add(prem);
				
				Iterator<Formula> todoIt = todo.iterator();
				
				while(todoIt.hasNext()) {
					
					Formula nextForm = todoIt.next();
					
					if(nextForm.getClass().equals(Atomic.class))
						axioms.add((Atomic) nextForm);
					else if(nextForm.getClass().equals(Negated.class))
						axioms.add((Negated) nextForm);	
					else {
						todo.add(((Conjunctive) nextForm).getSubformulaA());
						todo.add(((Conjunctive) nextForm).getSubformulaB());
					}
					todo.remove(nextForm);
					todoIt = todo.iterator();
				}
				
				// For each quantified variable of the KB formula check if the substitution in the formula satisfies the equality axioms
				
				Iterator<Term> qVarIt = qVars.iterator();
				Iterator<Term> stdNameIt = form.standardNamesList().iterator();
				Term qVar, stdName;
				boolean premOk = false;
				
				while(qVarIt.hasNext() && stdNameIt.hasNext()) {
					qVar = qVarIt.next();
					stdName = stdNameIt.next();
					
					premOk = solvePremise(axioms, qVar, stdName);
					
					// If one of the equalities is not verified exit the loop (efficiency reasons)
					if(!premOk) break;
				}
				if(premOk && !negConcl) 
					return 1.0f;
				else if (premOk && negConcl)
					return 0.0f;
			}
		}
		return 0.5f;
	}
	
	private static boolean solvePremise(List<Formula> axioms, Term qVar, Term stdName) throws ParseException{
		
		Iterator<Formula> axIt = axioms.iterator();
		Formula axiom;
		Formula nAxiom;
		Atomic toCheck;
		boolean premOk = false;
		
		toCheck = (Atomic) FOLParser.parse("eq("+ qVar + "," + stdName + ")");
		boolean negAxiom;
		
		// Check if the axiom is an equality or a negated equality
		
		while(axIt.hasNext()) {
			negAxiom = false;
			axiom = axIt.next();
			if (axiom.getClass().equals(Negated.class)) {
				negAxiom = true;
				if(((Negated) axiom).getSubformula().getClass().equals(Atomic.class))
					nAxiom = (Atomic)((Negated) axiom).getSubformula();
				else
					nAxiom = (Conjunctive)((Negated) axiom).getSubformula();
			}
			else
				nAxiom = (Atomic) axiom;
			
			// Manage cwa formulas
			if(nAxiom.getClass().equals(Conjunctive.class)) {
				List<Formula> nAxioms = new ArrayList<Formula>();
				List<Formula> nConj = new ArrayList<Formula>();
				nConj.add(nAxiom);
				
				while(!nConj.isEmpty()) {
					Formula subA = ((Conjunctive) nAxiom).getSubformulaA();
					if(subA.getClass().equals(Atomic.class)) nAxioms.add(subA);
					else nConj.add(subA);
					Formula subB = ((Conjunctive) nAxiom).getSubformulaB();
					if(subB.getClass().equals(Atomic.class)) nAxioms.add(subB);
					else nConj.add(subB);
					nConj.remove(nAxiom);
				}
				
				Iterator<Formula> nAxIt = nAxioms.iterator();
				while(nAxIt.hasNext()) {
					Atomic nAx = (Atomic) nAxIt.next();
					if(nAx.toString().equals(toCheck.toString())) {
						if(!negAxiom) return true;
						if(negAxiom) premOk = false;
					}
					else{
						Term var = nAx.getTerms().iterator().next();
						//Same variable but different standard name, and negated axiom
						if(var.toString().equals(qVar.toString())) {
							if(negAxiom) premOk = true;
						}
					}
				}
			}
			else {
				if(nAxiom.toString().equals(toCheck.toString())) {
					if(!negAxiom) return true;
					if(negAxiom) premOk = false;
				}
				else{
					Term var = ((Atomic) nAxiom).getTerms().iterator().next();
					//Same variable but different standard name, and negated axiom
					if(var.toString().equals(qVar.toString())) {
						if(negAxiom) premOk = true;
					}
				}
			}
			
		}
			
		return premOk;
	}
}
