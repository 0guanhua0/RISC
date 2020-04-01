package edu.duke.ece651.risk.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.io.*;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;
import static edu.duke.ece651.risk.shared.Constant.UP_UNIT_COST;

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

    public static Map<String,Integer> readSizeConfig(String fileName) throws IOException{
        Map<String,Integer> res = new HashMap<>();
        try(Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String name = line.split(":")[0].strip();
                int size = Integer.parseInt(line.split(":")[1].strip());
                res.put(name,size);
            }
        }
        return res;
    }

    public static Map<String,Integer> readBasicResourceConfig(String fileName) throws  IOException{
        Map<String,Integer> res = new HashMap<>();
        try(Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String name = line.split(":")[0].strip();
                int size = Integer.parseInt(line.split(":")[1].strip());
                res.put(name,size);
            }
        }
        return res;
    }
    public static int getUnitUpCost(int cur,int target){
        if (cur<target&&UNIT_BONUS.containsKey(cur)&&UNIT_BONUS.containsKey(target)){
            throw new IllegalArgumentException("invalid input argument");
        }else{
            int start = UP_UNIT_COST.get(cur);
            int end = UP_UNIT_COST.get(target);
            return end-start;
        }
    }

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

    public static int getMaxLevel(Map<Integer,?> map){
        return map.keySet().stream().max(Integer::compareTo).get();
    }

    public static int getMinLevel(Map<Integer,?> map){
        return map.keySet().stream().min(Integer::compareTo).get();
    }
}
