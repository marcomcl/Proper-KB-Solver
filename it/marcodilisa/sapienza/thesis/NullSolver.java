package it.marcodilisa.sapienza.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.marcodilisa.sapienza.thesis.model.Atomic;
import it.marcodilisa.sapienza.thesis.model.Conjunctive;
import it.marcodilisa.sapienza.thesis.model.Disjunctive;
import it.marcodilisa.sapienza.thesis.model.Existential;
import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.model.Implicative;
import it.marcodilisa.sapienza.thesis.model.Negated;
import it.marcodilisa.sapienza.thesis.model.Term;
import it.marcodilisa.sapienza.thesis.model.Universal;
import it.marcodilisa.sapienza.thesis.parser.FOLParser;
import it.marcodilisa.sapienza.thesis.parser.ParseException;

public class NullSolver {
	
	public static Set<ArrayList<Term>> T;

	public static Float Solve(ProperKB kb, Formula form) throws ParseException {
		float res = -1.0f, solveRes;
		boolean allTrue = false, allFalse = false;
		if (T == null) T = generateT(kb);
		int count = 0;
		HashSet<Term> stdNames = (HashSet<Term>) kb.getStdNames();
		stdNames.addAll(form.standardNames());
		
		Set<ArrayList<Term>> H = generateH(stdNames, kb.getNulls().size()); // Use the assumption that the std names in the query are a subset of those in KB
		for(ArrayList<Term> l : H) {
			ProperKB newKB = substKB(kb.getFormulas(), l, (HashSet<Term>) kb.getNulls());
			boolean consistent = true;
			for(ArrayList<Term> t : T) {
				if(isMatching(l, t, kb.getStdNames())) {
					consistent = false;
					break;
				}
			}
			if(!consistent) continue;
			Formula newForm = substForm(form, l, (HashSet<Term>) kb.getNulls());
			solveRes = Solver.V(newKB,newForm);
			if(solveRes == 0.5f) return 0.5f;
			if(solveRes == 1.0f) {
				if(count == 0) {
					allTrue = true;
					allFalse = false;
					res = 1.0f;
				}else {
					if(allFalse) return 0.5f;
					else res = 1.0f;
				}
			}else if(solveRes == 0.0f) {
				if(count == 0) {
					allFalse = true;
					allTrue = false;
					res = 0.0f;
				}else {
					if(allTrue) return 0.5f;
					else res = 0.0f;
				}
			}
			count++;
		}
		
		return res;
	}

	private static Formula substForm(Formula form, List<Term> l, HashSet<Term> nulls) throws ParseException {
		Formula f = FOLParser.parse(form.toString());
		int i = 0;
		Iterator<Term> it = nulls.iterator();
		while(it.hasNext()) {
			f.substitute(l.get(i), it.next());
			i++;
		}
		return f;
	}

	private static ProperKB substKB(ArrayList<Formula> f, List<Term> l, HashSet<Term> nulls) {
		for(Formula form : f) {
			Iterator<Term> it = nulls.iterator();
			int i = 0;
			while(it.hasNext()){
				form.substitute(l.get(i), it.next());
				i++;
			}
		}
		return new ProperKB(f);
	}

	private static Set<ArrayList<Term>> generateH(Set<Term> stdNames, int nullsTot) {
		Term[] newStd = new Term[nullsTot];
		for(int i = 0; i < nullsTot; i++) {
			newStd[i] = newStdName(stdNames);
		}
		Set<ArrayList<Term>> H = new HashSet<ArrayList<Term>>();
		Set<ArrayList<Term>> toRemove = new HashSet<ArrayList<Term>>();
		ArrayList<Term> stdList = new ArrayList<Term>();
		for(Term t : stdNames) {
			for(int i = 0; i <= nullsTot; i++)
				stdList.add(t);
		}
		permK(stdList, H, 0, nullsTot);
		for(int i = 0; i < nullsTot; i++) {
			for(ArrayList<Term> l : H) {
				if(l.indexOf(newStd[i]) != -1 && l.indexOf(newStd[i]) < i) toRemove.add(l);
			}
		}
		H.removeAll(toRemove);
		return H;
	}
	
	private static void permK(List<Term> stdNames, Set<ArrayList<Term>> H, int i, int k)
	{
	  if(i == k)
	  {
	    H.add(new ArrayList<Term>(stdNames.subList(0, k)));
	    return;
	  }

	  for(int j = i; j < stdNames.size(); j++)
	  {
	    Collections.swap(stdNames, i, j);
	    permK(stdNames, H, i+1, k);    
	    Collections.swap(stdNames, i, j);
	  }
	}
	
	private static Set<ArrayList<Term>> generateT(ProperKB kb) throws ParseException{
		System.out.println("Generating T...");
		Set<ArrayList<Term>> temp = generateH(kb.getStdNames(), kb.getNulls().size());
		Set<ArrayList<Term>> T = new HashSet<ArrayList<Term>>();
		for(ArrayList<Term> l : temp) {
			ProperKB newKB = substKB(kb.getFormulas(), l, (HashSet<Term>) kb.getNulls());
			if(!checkConsistency(newKB)) T.add(l);
		}
		return T;
	}
	
	private static boolean checkConsistency(ProperKB kb) throws ParseException {
		Map<String, Formula> eq = new HashMap<String,Formula>();
		
		for(Formula f : kb.getFormulas()) {
			Implicative imp = (Implicative) ((Universal) f).getSubformula();
			Formula key = imp.getConclusion();
			String pred = (key.getClass().equals(Negated.class)) ? "not"+ ((Atomic)((Negated)key).getSubformula()).getPredicate().toString() : ((Atomic) key).getPredicate().toString();
			if(!eq.containsKey(pred)) {
				eq.put(pred, imp.getPremise());
			}else
				eq.put(pred, new Disjunctive(eq.get(pred), imp.getPremise()));
		}
		
		List<String> done = new ArrayList<String>();
		
		
		for(String pred : eq.keySet()) {
			String compl = (pred.startsWith("not")) ? pred.substring(3) : "not" + pred;
			if(done.contains(pred) || done.contains(compl)) continue;
			if(!eq.containsKey(compl)) continue;
			Formula conj = new Conjunctive(eq.get(pred), eq.get(compl));
			Formula toCheck = null;
			for(Term t : conj.freeVars()) {
				if(toCheck == null) toCheck = new Existential(conj, t);
				else toCheck = new Existential(toCheck, t);
			}
			float res = Solver.V(new ProperKB(), toCheck);
			if(res == 1.0f) return false;
			done.add(pred);
			done.add(compl);
		}
		
		return true;
	}
	
	private static Term newStdName(Set<Term> h) {
		boolean done = false;
		int i = 1;
		Term t = null;
		while(!done) {
			t = new Term("n"+i);
			if(h.contains(t))
				i++;
			else {
				h.add(t);
				done = true;
			}
		}
		return t;
	}
	
	public static boolean isMatching(List<Term> t1, List<Term> t2, Set<Term> stdNames) {
		for(int i = 0; i < t1.size(); i++) {
			for(int j = 0; j < t1.size(); j++) {
				if(t1.get(i).equals(t1.get(j))) {
					if(!t2.get(i).equals(t2.get(j))) return false;
				}
			}
			if(stdNames.contains(t1.get(i))) {
				if(!t1.get(i).equals(t2.get(i))) return false;
			}else {
				if(stdNames.contains(t2.get(i))) return false;
			}
		}
		return true;
	}

}
