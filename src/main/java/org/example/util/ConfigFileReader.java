package org.example.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Probabilities;
import org.example.model.ScratchCard;
import org.example.model.Symbol;
import org.example.model.SymbolProbabilities;
import org.example.model.WinCombination;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.example.Constants.ApplicationConstants.BONUS_SYMBOLS;
import static org.example.Constants.ApplicationConstants.COLUMN;
import static org.example.Constants.ApplicationConstants.COLUMNS;
import static org.example.Constants.ApplicationConstants.COUNT;
import static org.example.Constants.ApplicationConstants.COVERED_AREAS;
import static org.example.Constants.ApplicationConstants.EXTRA;
import static org.example.Constants.ApplicationConstants.GROUP;
import static org.example.Constants.ApplicationConstants.IMPACT;
import static org.example.Constants.ApplicationConstants.PROBABILITIES;
import static org.example.Constants.ApplicationConstants.REWARD_MULTIPLIER;
import static org.example.Constants.ApplicationConstants.ROW;
import static org.example.Constants.ApplicationConstants.ROWS;
import static org.example.Constants.ApplicationConstants.STANDARD_SYMBOLS;
import static org.example.Constants.ApplicationConstants.SYMBOLS;
import static org.example.Constants.ApplicationConstants.TYPE;
import static org.example.Constants.ApplicationConstants.WHEN;
import static org.example.Constants.ApplicationConstants.WIN_COMBINATIONS;

public class ConfigFileReader {

    public ScratchCard readConfigFile(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File(filePath);
            JsonNode rootNode = mapper.readTree(jsonFile);
            ScratchCard scratchCard = new ScratchCard();

            scratchCard.setColumns(rootNode.get(COLUMNS).asInt());
            scratchCard.setRows(rootNode.get(ROWS).asInt());

            Map<String, Symbol> symbols = new HashMap<>();
            JsonNode symbolsNode = rootNode.get(SYMBOLS);
            Iterator<Map.Entry<String, JsonNode>> symbolIterator = symbolsNode.fields();

            while (symbolIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = symbolIterator.next();
                String symbolName = entry.getKey();
                JsonNode symbolNode = entry.getValue();

                Symbol symbol = new Symbol();
                symbol.setType(symbolNode.get(TYPE).asText());

                if (symbolNode.has(REWARD_MULTIPLIER)) {
                    symbol.setRewardMultiplier(symbolNode.get(REWARD_MULTIPLIER).asDouble());
                }

                if (symbolNode.has(IMPACT)) {
                    symbol.setImpact(symbolNode.get(IMPACT).asText());
                }

                if (symbolNode.has(EXTRA)) {
                    symbol.setExtra(symbolNode.get(EXTRA).asInt());
                }

                symbols.put(symbolName, symbol);
            }
            scratchCard.setSymbols(symbols);

            Probabilities probabilities = new Probabilities();

            List<SymbolProbabilities> standardSymbols = new ArrayList<>();
            JsonNode standardSymbolsNode = rootNode.get(PROBABILITIES).get(STANDARD_SYMBOLS);
            for (JsonNode posNode : standardSymbolsNode) {
                SymbolProbabilities posProbability = new SymbolProbabilities();
                posProbability.setColumn(posNode.get(COLUMN).asInt());
                posProbability.setRow(posNode.get(ROW).asInt());

                Map<String, Integer> symbolProbabilities = new HashMap<>();
                JsonNode symbolsProb = posNode.get(SYMBOLS);
                Iterator<Map.Entry<String, JsonNode>> symbolProbIterator = symbolsProb.fields();

                while (symbolProbIterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = symbolProbIterator.next();
                    symbolProbabilities.put(entry.getKey(), entry.getValue().asInt());
                }

                posProbability.setSymbols(symbolProbabilities);
                standardSymbols.add(posProbability);
            }
            probabilities.setStandardSymbols(standardSymbols);

            SymbolProbabilities bonusSymbols = new SymbolProbabilities();
            Map<String, Integer> bonusSymbolMap = new HashMap<>();
            JsonNode bonusSymbolsNode = rootNode.get(PROBABILITIES).get(BONUS_SYMBOLS).get(SYMBOLS);
            Iterator<Map.Entry<String, JsonNode>> bonusIterator = bonusSymbolsNode.fields();

            while (bonusIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = bonusIterator.next();
                bonusSymbolMap.put(entry.getKey(), entry.getValue().asInt());
            }

            bonusSymbols.setSymbols(bonusSymbolMap);
            probabilities.setBonusSymbols(bonusSymbols);
            scratchCard.setProbabilities(probabilities);

            Map<String, WinCombination> winCombinations = new HashMap<>();
            JsonNode winCombinationsNode = rootNode.get(WIN_COMBINATIONS);
            Iterator<Map.Entry<String, JsonNode>> winCombIterator = winCombinationsNode.fields();

            while (winCombIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = winCombIterator.next();
                String combName = entry.getKey();
                JsonNode combNode = entry.getValue();

                WinCombination winComb = new WinCombination();
                winComb.setRewardMultiplier(combNode.get(REWARD_MULTIPLIER).asInt());
                winComb.setWhen(combNode.get(WHEN).asText());
                winComb.setGroup(combNode.get(GROUP).asText());

                if (combNode.has(COUNT)) {
                    winComb.setCount(combNode.get(COUNT).asInt());
                }

                if (combNode.has(COVERED_AREAS)) {
                    List<List<String>> coveredAreas = new ArrayList<>();
                    JsonNode areasNode = combNode.get(COVERED_AREAS);

                    for (JsonNode areaNode : areasNode) {
                        List<String> area = new ArrayList<>();
                        for (JsonNode posNode : areaNode) {
                            area.add(posNode.asText());
                        }
                        coveredAreas.add(area);
                    }

                    winComb.setCoveredAreas(coveredAreas);
                }

                winCombinations.put(combName, winComb);
            }
            scratchCard.setWinCombinations(winCombinations);

            return scratchCard;
        } catch (IOException e) {
            System.err.println("Error parsing JSON file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
