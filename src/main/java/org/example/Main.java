package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.example.model.GameResult;
import org.example.model.ScratchCard;
import org.example.service.GameEngine;
import org.example.util.ConfigFileReader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ConfigFileReader configFileReader = new ConfigFileReader();
        String configPath = "";
        double betAmount = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--config") && i + 1 < args.length) {
                configPath = args[i + 1];
                i++;
            } else if (args[i].equals("--betting-amount") && i + 1 < args.length) {
                betAmount = Double.parseDouble(args[i + 1]);
                i++;
            }
        }

        if (StringUtils.isBlank(configPath) || betAmount <= 0) {
            System.out.println("Invalid input arguments. Please provide valid values for --config and --betting-amount");
            System.exit(1);
        }

        try {
            ScratchCard scratchCard = configFileReader.readConfigFile(configPath);

            GameEngine engine = new GameEngine(scratchCard);
            GameResult result = engine.play(betAmount);

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}