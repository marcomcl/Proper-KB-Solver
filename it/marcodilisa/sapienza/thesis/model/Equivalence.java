package it.marcodilisa.sapienza.thesis.model;

import java.util.Set;

public class Equivalence implements Formula {
    private Formula subformulaA = null, subformulaB = null;

    public Equivalence(Formula subformulaA, Formula subformulaB) {
        this.subformulaA = subformulaA;
        this.subformulaB = subformulaB;
    }

    public Formula getSubformulaA() {
        return subformulaA;
    }

    public void setSubformulaA(Formula subformulaA) {
        this.subformulaA = subformulaA;
    }

    public Formula getSubformulaB() {
        return subformulaB;
    }

    public void setSubformulaB(Formula subformulaB) {
        this.subformulaB = subformulaB;
    }

    @Override
    public void substitute(Term term, Term forVar) {
        subformulaA.substitute(term, forVar);
        subformulaB.substitute(term, forVar);
    }

    @Override
    public Set<Term> freeVars() {
        Set<Term> freeVars = subformulaA.freeVars();
        freeVars.addAll(subformulaB.freeVars());
        
        return freeVars;
    }
    
    @Override
    public Set<Term> standardNames() {
        Set<Term> stdNames = subformulaA.standardNames();
        stdNames.addAll(subformulaB.standardNames());
        
        return stdNames;
    }
    
    @Override
    public Set<Term> nulls() {
        Set<Term> nulls = subformulaA.nulls();
        nulls.addAll(subformulaB.nulls());
        
        return nulls;
    }
    
    @Override
    public Set<Term> constants() {
        Set<Term> constants = subformulaA.constants();
        constants.addAll(subformulaB.constants());
        
        return constants;
    }
    
    @Override
    public String toString() {
        return "(" + subformulaA.toString() + " = " + subformulaB.toString() + ")";
    }
}