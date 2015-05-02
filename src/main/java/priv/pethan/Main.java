package priv.pethan;

import priv.pethan.commands.Command;

public class Main {
    public static String dataFileName = "tankdb.json";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Arguments:");
            System.out.println("scan {playerId} {yyyy-mm-dd}  ... scan for new players in certain snapshot from certain player ID on, max 500k records. Only new players IDs are read and added to DB.");
            System.out.println("name                          ... add names to unnamed players");
            System.out.println("rank                          ... update WOT rating/score and ace tanker rating/count according to yesterday's WOT snapshot");
            System.out.println("remove yyyy-mm-dd             ... removes certain timepoint from DB");
            System.out.println("export {count} {yyyy-mm-dd}   ... exports top {count|20k} according to ace tankers rank from give snapshot or last snapshot");
        } else {
            processCommand(args);
        }
    }

    private static void processCommand(String[] args) {
        CommandFactory factory = new CommandFactory();

        Command command = factory.getCommand(args[0].toLowerCase());
        if (command == null) {
            System.out.println("Invalid command " + args[0]);
        } else {
            command.execute(args);
        }
    }

}
