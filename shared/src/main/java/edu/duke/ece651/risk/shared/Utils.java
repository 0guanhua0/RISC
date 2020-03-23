package edu.duke.ece651.risk.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.io.*;

public class Utils {
    public static String readFileToString(String fileName) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try(Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        return contentBuilder.toString();
    }
    public static Map<String,Set<String>> readNeighConfig(String fileName) throws IOException {
        Map<String,Set<String>> res = new HashMap<>();
        try(Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int i = line.indexOf(":");
                String terrName = line.substring(0,i).strip();
                List<String> neighList = Arrays.asList(line.substring(i+1).strip().split("\\^"));
                Set<String> neighSet = new HashSet<>(neighList);
                res.put(terrName,neighSet);
            }
        }
        return res;
    }
    public static List<String> readColorConfig(String fileName) throws IOException{
        List<String> res = new ArrayList<>();
        try(Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()){
                res.add(scanner.nextLine());
            }
        }
        return res;
    }

    public static Map<Set<String>,Boolean> readGroupConfig(String fileName) throws IOException {
        Map<Set<String>,Boolean> res = new HashMap<>();
        try(Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                List<String> groupList = Arrays.asList(line.strip().split("\\^"));
                Set<String> neighSet = new HashSet<>(groupList);
                res.put(neighSet,false);
            }
        }
        return res;
    }

//    //TODO test the correctness of this method
//    public static <T extends Serializable> T clone(T obj) throws IOException, ClassNotFoundException {
//        T cloneObj = null;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ObjectOutputStream obs = new ObjectOutputStream(out);
//        obs.writeObject(obj);
//        obs.close();
//
//        ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
//        ObjectInputStream ois = new ObjectInputStream(ios);
//
//        cloneObj = (T) ois.readObject();
//        ois.close();
//
//        return cloneObj;
//    }
}
