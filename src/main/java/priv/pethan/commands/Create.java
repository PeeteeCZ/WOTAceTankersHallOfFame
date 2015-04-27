package priv.pethan.commands;

import priv.pethan.data.PlayerList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Create extends CommandBase {

    @Override
    public String getCommandName() {
        return "create";
    }

    @Override
    public void execute(String[] args) {
        LocalDate date = (args.length == 1) ? LocalDate.now().minusDays(1) : LocalDate.parse(args[1]);

        playerList = new PlayerList();
        playerList.setSnapshotDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));

        saveToFile();
    }
}
