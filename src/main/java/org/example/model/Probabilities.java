package org.example.model;

import java.util.List;

public class Probabilities {

    private List<SymbolProbabilities> standardSymbols;
    private SymbolProbabilities bonusSymbols;

    public Probabilities() {
    }

    public Probabilities(List<SymbolProbabilities> standardSymbols, SymbolProbabilities bonusSymbols) {
        this.standardSymbols = standardSymbols;
        this.bonusSymbols = bonusSymbols;
    }

    public List<SymbolProbabilities> getStandardSymbols() {
        return standardSymbols;
    }

    public void setStandardSymbols(List<SymbolProbabilities> standardSymbols) {
        this.standardSymbols = standardSymbols;
    }

    public SymbolProbabilities getBonusSymbols() {
        return bonusSymbols;
    }

    public void setBonusSymbols(SymbolProbabilities bonusSymbols) {
        this.bonusSymbols = bonusSymbols;
    }

    @Override
    public String toString() {
        return "Probabilities{" +
                "standardSymbols=" + standardSymbols +
                ", bonusSymbols=" + bonusSymbols +
                '}';
    }
}
