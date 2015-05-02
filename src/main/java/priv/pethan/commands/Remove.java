package priv.pethan.commands;

public class Remove extends CommandBase {

    @Override
    public String getCommandName() {
        return "remove";
    }

    @Override
    public void execute(String[] args) {
        String snapshotDate = args[1];

        loadFromFile();

        System.out.println("Removing snapshot " + snapshotDate + " from database");

        playerBase.getPlayers().values().stream().forEach(player -> player.getTimePoints().remove(snapshotDate));

        saveToFile(true);
    }
}
