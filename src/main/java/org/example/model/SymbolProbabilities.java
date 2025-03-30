package org.example.model;

import java.util.Map;


public class SymbolProbabilities {
    private int column;
    private int row;
    private Map<String, Integer> symbols;

    public SymbolProbabilities() {
    }

    public SymbolProbabilities(int column, int row, Map<String, Integer> symbols) {
        this.column = column;
        this.row = row;
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return "SymbolProbabilities{" +
                "column=" + column +
                ", row=" + row +
                ", symbols=" + symbols +
                '}';
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Map<String, Integer> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Integer> symbols) {
        this.symbols = symbols;
    }
}
