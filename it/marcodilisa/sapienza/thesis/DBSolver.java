package it.marcodilisa.sapienza.thesis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

public class DBSolver {

	public static Set<List<Term>> queryDB(DataBase db, Formula query, List<Formula> types) throws ParseException {
		
		Set<List<Term>> res = new HashSet<List<Term>>();
		
		// First check THEOREM 4.8 -- Types whose variables don't occur free in the query
		
		System.out.println("- Evaluating " + query.getClass().getSimpleName()+  " query: "+query);
		System.out.println("- Types: "+types);
		
		List<Formula> typesToRemove = new ArrayList<Formula>();
		boolean allFree = true;
		int index = 0;
		
		for(Formula t : types) {
			if(!query.freeVars().contains(t.freeVars().iterator().next())) {
				typesToRemove.add(t);
				String typePred = ((Atomic) t).getPredicate().toString();
				res = cartProd(res, db.getTypedConstants(typePred));
				allFree = false;
				index = types.indexOf(t);
			}
		}
		types.removeAll(typesToRemove);
		if(!allFree) {
			res = cartProd1(res, DBSolver.queryDB(db, query, types));
			if(index != 0) {
				List<Integer> pos = new ArrayList<Integer>();
				for(int i = 1; i <= index; i++) pos.add(i);
				pos.add(1);
				for(int i = index + 1; i <= types.size(); i++) pos.add(i);
				res = setProjectPos(res, pos);
			}
			return res;
		}
		
		if(query.getClass().equals(Disjunctive.class)) {
			Disjunctive form = (Disjunctive) query;
			Set<List<Term>> s1 = queryDB(db, form.getSubformulaA(), types);
			Set<List<Term>> s2 = queryDB(db, form.getSubformulaB(), types);
			res = DBSolver.setUnion(s1, s2);
			return res;
		}
		
		if(query.getClass().equals(Conjunctive.class)) {
			Conjunctive form = (Conjunctive) query;
			Set<List<Term>> s1 = queryDB(db, form.getSubformulaA(), types);
			Set<List<Term>> s2 = queryDB(db, form.getSubformulaB(), types);
			res = DBSolver.setInter(s1, s2);
			return res;
		}
		
		if(query.getClass().equals(Existential.class)) {
			Conjunctive sf = (Conjunctive) ((Existential) query).getSubformula();
			types.add(sf.getSubformulaA());
			res = DBSolver.queryDB(db, sf.getSubformulaB(), types);
			res = setProject(res);
		}
		
		if(query.getClass().equals(Universal.class)) {
			Disjunctive sf = (Disjunctive) ((Universal) query).getSubformula();
			types.add(((Negated)sf.getSubformulaA()).getSubformula());
			res = DBSolver.queryDB(db, sf.getSubformulaB(), types);
			Atomic type = (Atomic) ((Negated)sf.getSubformulaA()).getSubformula();
			String pred = type.getPredicate().toString();
			System.out.println("- Performing division on the result!");
			System.out.println("- Tuples before the division: "+res);
			res = setDivide(res, db.getClosure().get(pred));
			System.out.println("- Tuples after the division: "+res);
		}
		
		if(query.getClass().equals(Atomic.class)) {
			
			if(((Atomic)query).getPredicate().toString().equals("eq")) {
				
				//EQUALITY AXIOMS -- COROLLARY 4.10
				
				boolean allConstants = true, allVariables = true;
				
				Term a = ((Atomic)query).getTerms().get(0);
				Term b = ((Atomic)query).getTerms().get(1);
				
				for(Term t : ((Atomic)query).getTerms()) {
					if(t.isConstant()) allVariables = false;
					else if(t.isVariable()) allConstants = false;
				}
				
				if(allConstants) {
					
					// 1. When a and b are constants
					
					if(a.equals(b)) {
						res.add(new ArrayList<Term>());
						return res;
					}else
						return res;
				}
				
				if(types.size() == 1) {
					
					String pred = ((Atomic) types.get(0)).getPredicate().toString();
					
					if(allVariables) {
						
						if(a.equals(b)) {
							
							// 2. ||<X/t | E(X,X)>||
							
							for(Formula f : db.getClosure().get(pred)) {
								res.add(((Atomic)f).constantsList());
							}
						}
					}else {
						
						// 3. ||<X/t | E(X,a)>|| = ||<X/t | E(a,X)>||
						
						for(Formula f : db.getClosure().get(pred)) {
							
							Term c = (a.isConstant()) ? a : b;
							
							if(f.constants().contains(c)) {
								res.add(((Atomic)f).constantsList());
								break;
							}			
						}
					}
					return res;
				}else if(types.size() == 2 && allVariables && !a.equals(b)){
					
					// 4. ||<X/t, Y/f | E(X,Y)>||
					
					String pred1 = ((Atomic) types.get(0)).getPredicate().toString();
					String pred2 = ((Atomic) types.get(1)).getPredicate().toString();
					
					Set<Term> c1 = new HashSet<Term>();
					Set<Term> c2 = new HashSet<Term>();
					
					for(Formula f : db.getClosure().get(pred1)) {
						c1.addAll(f.constants());
					}
					
					for(Formula f : db.getClosure().get(pred2)) {
						c2.addAll(f.constants());
					}
					
					for(Term c : c1) {
						if(c2.contains(c)) {
							List<Term> resCon = new ArrayList<Term>();
							resCon.add(c);
							resCon.add(c);
							res.add(resCon);
						}	
					}
					return res;
				}
			}else {
				// ATOMIC -- THEOREM 4.9
				
				List<Integer> pos = new ArrayList<Integer>();
				Atomic q = (Atomic) query;
				String queryPred = q.getPredicate().toString();
				
				for(Formula t: types) {
					String pred = ((Atomic) t).getPredicate().toString();
					if(q.getTerms().contains(t.freeVars().iterator().next())) {
						res = cartProd(res, db.getTypedConstants(pred));
						System.out.println("Res: "+res);
						for(Term x : t.freeVars()) {
							pos.add(q.getTerms().indexOf(x));
						}
					}
						
					
				}
				System.out.println("- Performing selection!");
				Set<List<Term>> sel = setSelect(q, db.getTuples().get(queryPred));
				System.out.println("Sel: "+sel);
				System.out.println("- Performing projection on the result of the selection!");
				System.out.println("- Performing intersection between the tuples and the proj!");
				res = setInter(res, setProjectPos(sel, pos));
				return res;
				
			}
		}
		
		if(query.getClass().equals(Negated.class)) {
			
			Formula sf = ((Negated) query).getSubformula();
			
			if(sf.getClass().equals(Atomic.class) && ((Atomic) sf).getPredicate().toString().equals("eq")) {
				
				// EQUALITY AXIOMS -- THEOREM 4.11
				
				boolean allConstants = true, allVariables = true;
				
				Term a = ((Atomic)sf).getTerms().get(0);
				Term b = ((Atomic)sf).getTerms().get(1);
				
				for(Term t : ((Atomic)sf).getTerms()) {
					if(t.isConstant()) allVariables = false;
					else if(t.isVariable()) allConstants = false;
				}
				
				if(allConstants) {
					
					// 1. ||-E(a,b)>||
					
					if(db.isIn(query)) {
						res.add(new ArrayList<Term>());
						return res;
					}else
						return res;
				}else if(types.size() == 1) {
					
					if(allVariables) {
						
						// 2. ||<X/t | -E(X,X)>||
						
						if(a.equals(b)) return res;
					}else {
						
						// 3. ||<X/t | -E(X,a)>|| = ||<X/t | -E(a,X)>||
						
						String pred = ((Atomic) types.get(0)).getPredicate().toString();
						Term c = (a.isConstant()) ? a : b;
						for(Formula f : db.getClosure().get(pred)) {
							for(Term t : f.constants()) {
								Negated neg = new Negated(FOLParser.parse("eq("+c+","+t+")"));
								System.out.println("- Checking if " +neg +" belongs to the UNA set of the DB");
								if(db.isIn(neg)) {
									List<Term> l = new ArrayList<Term>();
									l.add(t);
									res.add(l);
								}
							}
						}
						
					}
					return res;
				} else if (types.size() == 2) {
					if(allVariables) {
						
						// 4. ||<X/t, Y/f | -E(X,Y)>||
						
						String pred1 = ((Atomic) types.get(0)).getPredicate().toString();
						String pred2 = ((Atomic) types.get(1)).getPredicate().toString();
						
						Set<Term> c1 = new HashSet<Term>();
						Set<Term> c2 = new HashSet<Term>();
						
						for(Formula f : db.getClosure().get(pred1)) {
							c1.addAll(f.constants());
						}
						
						for(Formula f : db.getClosure().get(pred2)) {
							c2.addAll(f.constants());
						}
						
						for(Term t1 : c1) {
							for(Term t2 : c2) {
								Negated neg = new Negated(FOLParser.parse("eq("+t1+","+t2+")"));
								if(db.isIn(neg)) {
									List<Term> l = new ArrayList<Term>();
									l.add(t1);
									l.add(t2);
									res.add(l);
								}
							}
						}
						return res;	
					}
				}
			}else if(sf.getClass().equals(Atomic.class)) {
				
				// NEGATED ATOMIC - THEOREM 4.12
				List<Term> tVars = new ArrayList<Term>();
				for(Formula t : types) {
					Iterator<Term> it = ((Atomic)sf).freeVarsList().iterator();
					while(it.hasNext()){
						Term x = it.next();
						if(t.freeVars().contains(x)) {
							Atomic type = (Atomic) t;
							tVars.add(x);
							res = cartProd(res, db.getTypedConstants(type.getPredicate().toString()));
						}
					}
				}
				if(sf.constants().size() > 0) res= cartProd(res, sf.constants());
				String pred = ((Atomic) sf).getPredicate().toString();
				res = notAgree(res, db.getTuples().get(pred), ((Atomic)sf).getTerms(), tVars);
				for(List<Term> l : res) l.subList(types.size(), l.size()).clear();
			}
		}
		
		return res;
	}

	private static Set<List<Term>> notAgree(Set<List<Term>> resTuples, Set<List<Term>> predTuples, List<Term> qTerms, List<Term> tVars) {
		Set<List<Term>> toRemove = new HashSet<List<Term>>();
		for(List<Term> l : resTuples) {
			List<Term> tuple = new ArrayList<Term>();
			for(Term t : qTerms) {
				if(t.isVariable()) {
					int pos = tVars.indexOf(t);
					tuple.add(l.get(pos));
				}
				else tuple.add(t);
			}
			if(predTuples.contains(tuple)) toRemove.add(l);
		}
		resTuples.removeAll(toRemove);
		return resTuples;
	}

	//Simplify formula
	public static Formula simplify(Formula f) {
		if(f.getClass().equals(Atomic.class)) return f;
		if(f.getClass().equals(Universal.class)) {
			Universal form = (Universal) f;
			return new Universal(simplify(form.getSubformula()), form.getQuantifiedVar());
		}
		if(f.getClass().equals(Existential.class)) {
			Existential form = (Existential) f;
			return new Existential(simplify(form.getSubformula()), form.getQuantifiedVar());
		}
		if(f.getClass().equals(Implicative.class)) {
			Implicative form = (Implicative) f;
			return new Disjunctive(simplify(new Negated(form.getPremise())), simplify(form.getConclusion()));
		}
		if(f.getClass().equals(Equivalence.class)) {
			Equivalence form = (Equivalence) f;
			Implicative imp1 = new Implicative(form.getSubformulaA(), form.getSubformulaB());
			Implicative imp2 = new Implicative(form.getSubformulaB(), form.getSubformulaA());
			return new Conjunctive(simplify(imp1), simplify(imp2));
		}
		if(f.getClass().equals(Conjunctive.class)) {
			Conjunctive form = (Conjunctive) f;
			return new Conjunctive(simplify(form.getSubformulaA()), simplify(form.getSubformulaB()));
		}
		if(f.getClass().equals(Disjunctive.class)) {
			Disjunctive form = (Disjunctive) f;
			return new Disjunctive(simplify(form.getSubformulaA()), simplify(form.getSubformulaB()));
		}
		if(f.getClass().equals(Negated.class)) {
			Negated form = (Negated) f;
			Formula subForm = form.getSubformula();
			if(subForm.getClass().equals(Atomic.class)) return f;
			if(subForm.getClass().equals(Negated.class)) return simplify(((Negated) subForm).getSubformula());
			if(subForm.getClass().equals(Universal.class)) {
				Universal univ = (Universal) subForm;
				return new Existential(simplify(new Negated(univ.getSubformula())), univ.getQuantifiedVar());
			}
			if(subForm.getClass().equals(Existential.class)) {
				Existential univ = (Existential) subForm;
				return new Universal(simplify(new Negated(univ.getSubformula())), univ.getQuantifiedVar());
			}
			
			//De Morgan
			
			if(subForm.getClass().equals(Conjunctive.class)) {
				Conjunctive conj = (Conjunctive) subForm;
				return new Disjunctive(simplify(new Negated(conj.getSubformulaA())), simplify(new Negated(conj.getSubformulaB())));
			}
			if(subForm.getClass().equals(Disjunctive.class)) {
				Disjunctive disj = (Disjunctive) subForm;
				return new Conjunctive(simplify(new Negated(disj.getSubformulaA())), simplify(new Negated(disj.getSubformulaB())));
			}
			
			if(subForm.getClass().equals(Implicative.class)) {
				Implicative impl = (Implicative) subForm;
				return new Conjunctive(simplify(impl.getPremise()), simplify(new Negated(impl.getConclusion())));
			}
			if(subForm.getClass().equals(Equivalence.class)) {
				Equivalence equi = (Equivalence) subForm;
				Implicative imp1 = new Implicative(equi.getSubformulaA(), equi.getSubformulaB());
				Implicative imp2 = new Implicative(equi.getSubformulaB(), equi.getSubformulaA());
				return new Disjunctive(simplify(new Negated(imp1)), simplify(new Negated(imp2)));
			}
			
		}
		return f;
	}
	
	public static Set<List<Term>> setUnion(Set<List<Term>> s1, Set<List<Term>> s2){
		Set<List<Term>> res = new HashSet<List<Term>>();
		res.addAll(s1);
		res.addAll(s2);
		return res;
	}
	
	private static Set<List<Term>> setInter(Set<List<Term>> s1, Set<List<Term>> s2) {
		Set<List<Term>> res = new HashSet<List<Term>>();
		for(List<Term> l : s1) {
			if(s2.contains(l)) res.add(l);
		}
		return res;
	}
	
	private static Set<List<Term>> cartProd(Set<List<Term>> set1, Set<Term> set2) {
		Set<List<Term>> res = new HashSet<List<Term>>();
		if(set1.isEmpty()) {
			for(Term c : set2) {
				List<Term> newList = new ArrayList<Term>();
				newList.add(c);
				res.add(newList);
			}
		}
		else {
			for(List<Term> l : set1) {
				for(Term c : set2) {
					List<Term> newList = new ArrayList<Term>();
					newList.addAll(l);
					newList.add(c);
					res.add(newList);
				}
			}
		}
		
		return res ;
	}
	
	private static Set<List<Term>> cartProd1(Set<List<Term>> s1, Set<List<Term>> s2) {
		Set<List<Term>> res = new HashSet<List<Term>>();
		
		if(s1.isEmpty()) res.addAll(s2);
		if(s2.isEmpty()) res.addAll(s1);
		
		else {
			for(List<Term> l1 : s1) {
				for(List<Term> l2 : s2) {
					List<Term> newList = new ArrayList<Term>();
					newList.addAll(l1);
					newList.addAll(l2);
					res.add(newList);
				}
			}
		}
		
		return res;
	}
	
	public static Set<List<Term>> setProject(Set<List<Term>> s1){
		Set<List<Term>> res = new HashSet<List<Term>>();
		for(List<Term> l : s1) {
			l.remove(l.size()-1);
			res.add(l);
		}
		return res;
	}
	
	public static Set<List<Term>> setProjectPos(Set<List<Term>> s1, List<Integer> pos){
		Set<List<Term>> res = new HashSet<List<Term>>();
		for(List<Term> l : s1) {
			List<Term> tmp = new ArrayList<Term>();
			for(int i : pos) {
				tmp.add(l.get(i));
			}
			res.add(tmp);
		}
		return res;
	}
	
	private static Set<List<Term>> setSelect(Atomic q, Set<List<Term>> tuples) {
		Set<List<Term>> sel = new HashSet<List<Term>>();
		boolean toKeep;
		
		for(List<Term> tuple : tuples) {
			toKeep = true;
			int pos = 0;
			for(Term c : q.constantsList()) {
				if(!tuple.get(pos).equals(c)) {
					toKeep = false;
					break;
				}
				pos++;
			}
			boolean firstOccurrence;
			for(Term x : q.freeVars()) {
				firstOccurrence = true;
				List<Integer> indexes = occurences(x, q.getTerms());
				Term c = null;
				for(int i : indexes) {
					if(firstOccurrence) {
						c = tuple.get(i);
						firstOccurrence = false;
					}else {
						if(!c.equals(tuple.get(i))) {
							toKeep = false;
							break;
						}
					}
				}
				if(!toKeep) break;
			}
			
			if(toKeep) 
				sel.add(tuple);
		}
		return sel;
	}
	
	private static List<Integer> occurences(Term x, List<Term> terms) {
		List<Integer> indexes = new ArrayList<Integer>();
		for(int i = 0; i < terms.size(); i++) {
			if(terms.get(i).equals(x)) indexes.add(i);
		}
		return indexes;
	}

	private static Set<List<Term>> setDivide(Set<List<Term>> s1, Set<Formula> ext) {
		Set<List<Term>> tmp = new HashSet<List<Term>>();
		Set<List<Term>> res = new HashSet<List<Term>>();
		
		for(List<Term> l : s1) {
			tmp.add(l.subList(0, l.size() - 1));
		}
		boolean toKeep;
		for(List<Term> l : tmp) {
			toKeep = true;
			for(Formula f : ext) {
				List<Term> temp = new ArrayList<Term>();
				temp.addAll(l);
				temp.add(f.constants().iterator().next());
				if(!s1.contains(temp)) {
					toKeep = false;
					break;
				}
			}
			if(toKeep) {
				res.add(l);
			}		
		}
		return res;
	}
}
