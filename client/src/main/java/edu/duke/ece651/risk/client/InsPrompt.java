package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.util.List;
import java.util.Map;

/**
 * store system instruction
 */
class InsPrompt {

    public static void insAskRoomOption(){
        System.out.println("What do you want to do?\n" +
                "J(oin) an existing room\n" +
                "C(reate) a new room");
    }

    public static void insShowRooms(List<Integer> roomList){
        System.out.println("All rooms:");
        for (Integer room : roomList){
            System.out.println("room " + room);
        }
        System.out.println("Which one do you want to join?");
    }

    public static void insShowMaps(Map<String, WorldMap<String>> maps){
        for (String mapName : maps.keySet()){
            SceneCLI.showMap(maps.get(mapName));
        }
    }

    public static void insShowMapOption(Map<Integer, String> options){
        System.out.println("Which map do you want to play?");
        for (int i = 1; i <= options.size(); i++){
            System.out.println(i + ". " + options.get(i));
        }
    }

    public static void actInfo(Player<String> player) {
        System.out.println( "You are the " + player.getPlayerColor() + " player, what would you like to do?\n" +
                " (M)ove\n" +
                " (A)ttack\n" +
                " (D)one\n" +
                " (Q)uit");
    }


    public static void selfInfo(String player) {
        System.out.println(player + " player:");
        System.out.println("-------------");

    }

    public static void srcInfo() {
        System.out.println("input source territory");
    }

    public static void dstInfo() {
        System.out.println("input destination territory");
    }

    public static void unitInfo() {
        System.out.println("input unit number");
    }

    public static void insInvalidOption(){
        System.out.println("Invalid option, please try again.");
    }

    public static void showMsg(String msg){
        System.out.println(msg);
    }
}