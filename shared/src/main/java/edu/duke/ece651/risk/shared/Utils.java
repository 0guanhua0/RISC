package edu.duke.ece651.risk.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Stream;
import java.io.*;

public class Utils {
    public static String readFileToString(String fileName) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        Stream<String> stream = Files.lines(Paths.get(fileName));
        stream.forEach(s -> contentBuilder.append(s).append("\n"));
        stream.close();
        return contentBuilder.toString();
    }
    //TODO test the correctness of this method
    public static <T extends Serializable> T clone(T obj) throws IOException, ClassNotFoundException {
        T cloneObj = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream obs = new ObjectOutputStream(out);
        obs.writeObject(obj);
        obs.close();

        ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(ios);

        cloneObj = (T) ois.readObject();
        ois.close();

        return cloneObj;
    }
}
