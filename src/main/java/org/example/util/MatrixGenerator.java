package org.example.util;

import org.example.model.ScratchCard;
import org.example.model.SymbolProbabilities;

import java.util.Map;
import java.util.Random;

public class MatrixGenerator {

    private final ScratchCard scratchCard;
    private final Random random;

    public MatrixGenerator(ScratchCard scratchCard, Random random) {
        this.scratchCard = scratchCard;
        this.random = random;
    }

    public String[][] generateMatrix() {
        int rows = scratchCard.getRows();
        int columns = scratchCard.getColumns();
        String[][] matrix = new String[rows][columns];

        for (SymbolProbabilities cellProb : scratchCard.getProbabilities().getStandardSymbols()) {
            int row = cellProb.getRow();
            int col = cellProb.getColumn();

            if (row < rows && col < columns) {
                matrix[row][col] = selectRandomSymbol(cellProb.getSymbols());
            }
        }

        SymbolProbabilities defaultProb = scratchCard.getProbabilities().getStandardSymbols().get(0);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (matrix[i][j] == null) {
                    matrix[i][j] = selectRandomSymbol(defaultProb.getSymbols());
                }
            }
        }

        if (random.nextInt(3) > 0) {
            int row = random.nextInt(rows);
            int col = random.nextInt(columns);
            matrix[row][col] = selectRandomSymbol(scratchCard.getProbabilities().getBonusSymbols().getSymbols());
        }

        return matrix;
    }

    private String selectRandomSymbol(Map<String, Integer> symbolProbabilities) {
        int totalWeight = symbolProbabilities.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalWeight) + 1;

        int cumulativeWeight = 0;
        for (Map.Entry<String, Integer> entry : symbolProbabilities.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }

        return symbolProbabilities.keySet().iterator().next();
    }
}
