/* Copyright 2014 Dominic Scheurer
 *
 * This file is part of FirstOrderParser.
 * 
 * FirstOrderParser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FirstOrderParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FirstOrderParser.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.marcodilisa.sapienza.thesis.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Atomic implements Formula {
    private Predicate predicate = null;
    private ArrayList<Term> terms = null;
    
    public Atomic(Predicate predicate) {
        this.predicate = predicate;
        this.terms = new ArrayList<Term>();
    }
    
    public Atomic(Predicate predicate, ArrayList<Term> terms) {
        this.predicate = predicate;
        this.terms = terms;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<Term> terms) {
        this.terms = terms;
    }
    
    public int arity() {
        return terms.size();
    }
    
    public boolean isPropositional() {
        return arity() == 0;
    }

    @Override
    public void substitute(Term term, Term forVar) {
        for (int i = 0; i < terms.size(); i++) {
            terms.get(i).substitute(term, forVar);
        }
    }
    
    @Override
    public Set<Term> freeVars() {
        HashSet<Term> freeVars = new HashSet<Term>();
        for (Term term : terms) {
            if (term.isVariable()) {
                freeVars.add(term);
            }
        }
        return freeVars;
    }
    
    @Override
    public Set<Term> standardNames() {
        HashSet<Term> stdNames = new HashSet<Term>();
        for (Term term : terms) {
            if (term.isStandard()) {
                stdNames.add(term);
            }
        }
        return stdNames;
    }
    
    @Override
    public Set<Term> nulls() {
        HashSet<Term> nulls = new HashSet<Term>();
        for (Term term : terms) {
            if (term.isNull()) {
                nulls.add(term);
            }
        }
        return nulls;
    }
    
    @Override
    public Set<Term> constants() {
        HashSet<Term> constants = new HashSet<Term>();
        for (Term term : terms) {
            if (term.isConstant()) {
                constants.add(term);
            }
        }
        return constants;
    }
    
    public List<Term> standardNamesList() {
        LinkedList<Term> stdNames = new LinkedList<Term>();
        for (Term term : terms) {
            if (term.isStandard()) {
                stdNames.add(term);
            }
        }
        return (List<Term>) stdNames.clone();
    }
    
    public List<Term> constantsList() {
        LinkedList<Term> constants = new LinkedList<Term>();
        for (Term term : terms) {
            if (term.isConstant()) {
                constants.add(term);
            }
        }
        return (List<Term>) constants.clone();
    }
    
    public List<Term> freeVarsList() {
    	LinkedList<Term> freeVars = new LinkedList<Term>();
        for (Term term : terms) {
            if (term.isVariable()) {
                freeVars.add(term);
            }
        }
        return (List<Term>) freeVars.clone();
	}
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(predicate.toString());
        
        if (!isPropositional()) {
            result.append("(");
            
            int i;
            for (i = 0; i < terms.size() - 1; i++) {
                result.append(terms.get(i).toString());
                result.append(",");
            }
            result.append(terms.get(i).toString());
            
            result.append(")");
        }
        
        return result.toString();
    }
}
