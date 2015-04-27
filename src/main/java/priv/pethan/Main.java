package priv.pethan;

import priv.pethan.commands.Command;

public class Main {
    public static String dataFileName = "tankdb.json";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Arguments:");
            System.out.println("create yyyy-mm-dd   ... create empty new list starting with given date (default date is yesterday)");
            System.out.println("add {count}         ... append next batch of players to data  (default count 50)");
            System.out.println("update {start} {yyyy-mm-dd}   ... update ace tanker counts from start position (zero based) according to certain (yesterday's) WOT snapshot");
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
