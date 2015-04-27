package priv.pethan.commands;

import priv.pethan.data.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;

public class Update extends CommandBase {

    @Override
    public String getCommandName() {
        return "update";
    }

    @Override
    public void execute(String[] args) {
        int position = 0;
        LocalDate date = LocalDate.now().minusDays(1);

        for (int idx = 1; idx < args.length; idx++) {
            String arg = args[idx];

            try {
                date = LocalDate.parse(arg);
            } catch (DateTimeParseException ex) {
                position = Integer.valueOf(arg);
            }
        }

        System.out.println("Updating from position " + position + " from snapshot with date " + date.format(DateTimeFormatter.ISO_LOCAL_DATE));

        loadFromFile();

        while (position < playerList.getPlayers().size()) {
            Player player = playerList.getPlayers().get(position);

            Long aceTankers = null;
            Integer delayInSeconds = 1;
            Integer maxDelay = 128;

            do {
                try {
                    aceTankers = wotRestService.getPlayerAceTankers(player.getId());
                } catch(Exception e) {
                }

                if (aceTankers == null) {
                    try {
                        TimeUnit.SECONDS.sleep(delayInSeconds);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (maxDelay < delayInSeconds) {
                        delayInSeconds = delayInSeconds * 2;
                    }
                }
            } while (aceTankers == null);

            player.setAceTankers(aceTankers);

            if (position % 100 == 0) {
                System.out.println("Updated position " + position);
                saveToFile();
            }

            position++;
        }

        saveToFile();
    }
}
