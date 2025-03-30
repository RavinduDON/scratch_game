
import org.example.model.GameResult;
import org.example.model.Probabilities;
import org.example.model.ScratchCard;
import org.example.model.Symbol;
import org.example.model.SymbolProbabilities;
import org.example.model.WinCombination;
import org.example.service.GameEngine;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class GameEngineTest {
    private ScratchCard scratchCard;
    private GameEngine engine;

    @Before
    public void setUp() throws IOException {
        scratchCard = createTestConfig();
        engine = new GameEngine(scratchCard);
    }

    @Test
    public void testGamePlayGeneratesValidMatrix() {
        double betAmount = 100.0;
        GameResult result = engine.play(betAmount);

        assertEquals(scratchCard.getRows(), result.getMatrix().length);
        assertEquals(scratchCard.getColumns(), result.getMatrix()[0].length);

        for (int i = 0; i < result.getMatrix().length; i++) {
            for (int j = 0; j < result.getMatrix()[i].length; j++) {
                assertNotNull(result.getMatrix()[i][j]);
                assertTrue(scratchCard.getSymbols().containsKey(result.getMatrix()[i][j]));
            }
        }
    }

    @Test
    public void testZeroRewardForNoWinningCombinations() {
        GameResult result = engine.play(100.0);

        if (result.getAppliedWinningCombinations() == null || result.getAppliedWinningCombinations().isEmpty()) {
            assertEquals(0.0, result.getReward(), 0.01);
            assertNull(result.getAppliedBonusSymbol());
        }
    }

    private ScratchCard createTestConfig() {
        ScratchCard scratchCard = new ScratchCard();

        scratchCard.setRows(3);
        scratchCard.setColumns(3);

        Map<String, Symbol> symbols = new HashMap<>();

        Symbol symbolA = new Symbol();
        symbolA.setType("standard");
        symbolA.setRewardMultiplier(5.0);
        symbols.put("A", symbolA);

        Symbol symbolB = new Symbol();
        symbolB.setType("standard");
        symbolB.setRewardMultiplier(3.0);
        symbols.put("B", symbolB);

        Symbol bonus10x = new Symbol();
        bonus10x.setType("bonus");
        bonus10x.setImpact("multiply_reward");
        bonus10x.setRewardMultiplier(10.0);
        symbols.put("10x", bonus10x);

        scratchCard.setSymbols(symbols);

        Probabilities probabilities = new Probabilities();

        Map<String, Integer> symbolProbs = new HashMap<>();
        symbolProbs.put("A", 1);
        symbolProbs.put("B", 2);

        SymbolProbabilities cellProb = new SymbolProbabilities();
        cellProb.setRow(0);
        cellProb.setColumn(0);
        cellProb.setSymbols(symbolProbs);

        List<SymbolProbabilities> standardSymbols = List.of(cellProb);
        probabilities.setStandardSymbols(standardSymbols);

        SymbolProbabilities bonusSymbols = new SymbolProbabilities();
        Map<String, Integer> bonusProbs = new HashMap<>();
        bonusProbs.put("10x", 1);
        bonusSymbols.setSymbols(bonusProbs);

        probabilities.setBonusSymbols(bonusSymbols);
        scratchCard.setProbabilities(probabilities);

        Map<String, WinCombination> winCombinations = new HashMap<>();

        WinCombination sameSymbol3 = new WinCombination();
        sameSymbol3.setWhen("same_symbols");
        sameSymbol3.setCount(3);
        sameSymbol3.setRewardMultiplier(1);
        sameSymbol3.setGroup("same_symbols");
        winCombinations.put("same_symbol_3_times", sameSymbol3);

        scratchCard.setWinCombinations(winCombinations);

        return scratchCard;
    }
}