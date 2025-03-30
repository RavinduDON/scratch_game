package org.example.service;

import org.example.model.GameResult;
import org.example.model.ScratchCard;
import org.example.model.Symbol;
import org.example.model.WinCombination;
import org.example.util.MatrixGenerator;

import java.util.*;

import static org.example.Constants.ApplicationConstants.COLON;
import static org.example.Constants.ApplicationConstants.EXTRA_BONUS;
import static org.example.Constants.ApplicationConstants.LINEAR_SYMBOLS;
import static org.example.Constants.ApplicationConstants.MISS;
import static org.example.Constants.ApplicationConstants.MULTIPLY_REWARD;
import static org.example.Constants.ApplicationConstants.SAME_SYMBOLS;
import static org.example.Constants.ApplicationConstants.STANDARD;
import static org.example.Constants.ApplicationConstants.BONUS;

public class GameEngine {
    private final ScratchCard scratchCard;
    private final Random random;
    private final MatrixGenerator matrixGenerator;

    public GameEngine(ScratchCard scratchCard) {
        this.scratchCard = scratchCard;
        this.random = new Random();
        this.matrixGenerator = new MatrixGenerator(scratchCard, random);
    }

    public GameResult play(double betAmount) {
        String[][] matrix = matrixGenerator.generateMatrix();

        Map<String, List<String>> appliedWinCombinations = findWinningCombinations(matrix);

        double reward = calculateReward(appliedWinCombinations, betAmount);

        GameResult result = new GameResult(matrix, reward);

        if (!appliedWinCombinations.isEmpty()) {
            result.setAppliedWinningCombinations(appliedWinCombinations);

            String appliedBonusSymbol = applyBonusSymbols(matrix, result);
            if (appliedBonusSymbol != null) {
                result.setAppliedBonusSymbol(appliedBonusSymbol);
            }
        }

        return result;
    }

    private Map<String, List<String>> findWinningCombinations(String[][] matrix) {
        Map<String, List<String>> appliedCombinations = new HashMap<>();
        Map<String, List<String>> groupAppliedCombinations = new HashMap<>();

        Map<String, Integer> symbolCounts = countStandardSymbols(matrix);

        for (Map.Entry<String, WinCombination> entry : scratchCard.getWinCombinations().entrySet()) {
            WinCombination winscratchCard = entry.getValue();

            if (SAME_SYMBOLS.equals(winscratchCard.getWhen())) {
                for (Map.Entry<String, Integer> symbolEntry : symbolCounts.entrySet()) {
                    String symbol = symbolEntry.getKey();
                    int count = symbolEntry.getValue();

                    if (count >= winscratchCard.getCount()) {
                        List<String> alreadyApplied = new ArrayList<>();
                        if (appliedCombinations.containsKey(symbol)) {
                            alreadyApplied = appliedCombinations.get(symbol);
                        }

                        List<String> appliedCombination = getMaxSameSymbolCombination(alreadyApplied, entry.getKey());
                        if (!appliedCombination.isEmpty()) {
                            addValueToCombinationList(alreadyApplied, appliedCombination);
                        }
                        appliedCombinations.put(symbol, alreadyApplied);
                    }
                }
            }
        }

        for (Map.Entry<String, WinCombination> entry : scratchCard.getWinCombinations().entrySet()) {
            WinCombination winscratchCard = entry.getValue();

            if (LINEAR_SYMBOLS.equals(winscratchCard.getWhen()) && winscratchCard.getCoveredAreas() != null) {
                for (List<String> area : winscratchCard.getCoveredAreas()) {
                    String firstSymbol = null;
                    boolean allSame = true;

                    for (String position : area) {
                        String[] indices = position.split(COLON);
                        int row = Integer.parseInt(indices[0]);
                        int col = Integer.parseInt(indices[1]);

                        String symbol = matrix[row][col];

                        if (scratchCard.getSymbols().containsKey(symbol) &&
                                STANDARD.equals(scratchCard.getSymbols().get(symbol).getType())) {

                            if (firstSymbol == null) {
                                firstSymbol = symbol;
                            } else if (!firstSymbol.equals(symbol)) {
                                allSame = false;
                                break;
                            }
                        } else {
                            allSame = false;
                            break;
                        }
                    }

                    if (allSame && firstSymbol != null) {
                        String group = winscratchCard.getGroup();
                        if (!groupAppliedCombinations.containsKey(group) ||
                                compareCombinations(entry.getKey(), groupAppliedCombinations.get(group).get(0)) > 0) {

                            groupAppliedCombinations.put(group, Collections.singletonList(entry.getKey()));
                            List<String> alreadyApplied = new ArrayList<>();
                            if (appliedCombinations.containsKey(firstSymbol)) {
                                alreadyApplied = appliedCombinations.get(firstSymbol);
                            }

                            List<String> appliedCombination = getMaxSameSymbolCombination(alreadyApplied, entry.getKey());
                            if (appliedCombination != null) {
                                addValueToCombinationList(alreadyApplied, appliedCombination);
                            }
                            appliedCombinations.put(firstSymbol, alreadyApplied);
                        }
                    }
                }
            }
        }

        return appliedCombinations;
    }


    private List<String> getMaxSameSymbolCombination(List<String> alreadyApplied, String combination) {
        List<String> appliedCombinations = new ArrayList<>();
        appliedCombinations.addAll(alreadyApplied);
        List<Integer> indexesToRemove = new ArrayList<>();
        String value = null;
        if (!appliedCombinations.isEmpty()) {
            WinCombination winCombination = scratchCard.getWinCombinations().get(combination);
            int index = 0;
            for (String appliedComb : appliedCombinations) {
                WinCombination appliedCombination = scratchCard.getWinCombinations().get(appliedComb);

                if (winCombination.getCount() != 0) {
                    if (appliedCombination.getCount() < winCombination.getCount()) {
                        indexesToRemove.add(index);
                        value = combination;
                    }
                } else {
                    value = combination;
                }
                index++;
            }
            if (!indexesToRemove.isEmpty()) {
                for (int indexToRemove : indexesToRemove) {
                    appliedCombinations.remove(indexToRemove);
                }
            }
            addValueToCombinationList(appliedCombinations, value);
        } else {
            addValueToCombinationList(appliedCombinations, combination);
        }

        return appliedCombinations.stream().filter(Objects::nonNull).toList();
    }

    private void addValueToCombinationList (List<String> appliedCombinations, String value) {
        if (!appliedCombinations.contains(value)) {
            appliedCombinations.add(value);
        }
    }

    private void addValueToCombinationList (List<String> appliedCombinations, List<String> appliedValues) {
        appliedCombinations.clear();
        appliedCombinations.addAll(appliedValues);
    }


    private int compareCombinations(String combo1, String combo2) {
        double multiplier1 = scratchCard.getWinCombinations().get(combo1).getRewardMultiplier();
        double multiplier2 = scratchCard.getWinCombinations().get(combo2).getRewardMultiplier();

        return Double.compare(multiplier1, multiplier2);
    }

    private Map<String, Integer> countStandardSymbols(String[][] matrix) {
        Map<String, Integer> counts = new HashMap<>();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                String symbol = matrix[i][j];

                if (scratchCard.getSymbols().containsKey(symbol) &&
                        STANDARD.equals(scratchCard.getSymbols().get(symbol).getType())) {
                    counts.put(symbol, counts.getOrDefault(symbol, 0) + 1);
                }
            }
        }

        return counts;
    }

    private double calculateReward(Map<String, List<String>> appliedWinCombinations, double betAmount) {
        if (appliedWinCombinations.isEmpty()) {
            return 0.0;
        }

        double totalReward = 0.0;

        for (Map.Entry<String, List<String>> entry : appliedWinCombinations.entrySet()) {
            String symbol = entry.getKey();
            List<String> combinations = entry.getValue();

            double symbolMultiplier = scratchCard.getSymbols().get(symbol).getRewardMultiplier();

            double combinedMultiplier = 1.0;
            for (String combination : combinations) {
                combinedMultiplier *= scratchCard.getWinCombinations().get(combination).getRewardMultiplier();
            }

            totalReward += betAmount * symbolMultiplier * combinedMultiplier;
        }

        return totalReward;
    }

    private String applyBonusSymbols(String[][] matrix, GameResult result) {
        if (result.getReward() <= 0) {
            return null;
        }

        List<String> bonusSymbols = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                String symbol = matrix[i][j];

                if (scratchCard.getSymbols().containsKey(symbol) &&
                        BONUS.equals(scratchCard.getSymbols().get(symbol).getType())) {
                    bonusSymbols.add(symbol);
                }
            }
        }

        if (bonusSymbols.isEmpty()) {
            return null;
        }

        String bonusSymbol = bonusSymbols.get(0);
        Symbol bonusscratchCard = scratchCard.getSymbols().get(bonusSymbol);

        if (MULTIPLY_REWARD.equals(bonusscratchCard.getImpact())) {
            result.setReward(result.getReward() * bonusscratchCard.getRewardMultiplier());
        } else if (EXTRA_BONUS.equals(bonusscratchCard.getImpact())) {
            result.setReward(result.getReward() + bonusscratchCard.getExtra());
        } else if (MISS.equals(bonusscratchCard.getImpact())) {
            return null;
        }

        return bonusSymbol;
    }
}