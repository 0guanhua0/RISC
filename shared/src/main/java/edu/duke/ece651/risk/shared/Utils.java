package edu.duke.ece651.risk.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Utils {
    public static String readFileToString(String fileName) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        Stream<String> stream = Files.lines(Paths.get(fileName));
        stream.forEach(s -> contentBuilder.append(s).append("\n"));
        stream.close();
        return contentBuilder.toString();
    }
}
