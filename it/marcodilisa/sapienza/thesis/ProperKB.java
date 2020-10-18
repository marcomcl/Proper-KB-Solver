package it.marcodilisa.sapienza.thesis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.marcodilisa.sapienza.thesis.model.Formula;
import it.marcodilisa.sapienza.thesis.model.Term;

public class ProperKB extends KnowledgeBase {
	
	private Set<Term> stdNames;
	private Set<Term> nulls;

	public ProperKB(List <Formula> f){
		super(f);

		stdNames = new HashSet<Term>();
		nulls = new HashSet<Term>();
		
		
		for(Formula form : formulas) {
			stdNames.addAll(form.standardNames());
		}
		
		for(Formula form : formulas) {
			nulls.addAll(form.nulls());
		}
	}
	
	public ProperKB() {
		super();
		stdNames = new HashSet<Term>();
		nulls = new HashSet<Term>();
	}

	public Set<Term> getStdNames(){
		return (Set<Term>) ((HashSet<Term>) stdNames).clone();
	}
	
	public Set<Term> getNulls(){
		return nulls;
	}
	
	public boolean hasNulls() {
		return nulls.size() > 0;
	}
}
