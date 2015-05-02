package priv.pethan.commands;

import priv.pethan.autorepeater.AutoRepeater;
import priv.pethan.rest.PlayerWithRank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.util.Objects.isNull;

public class Scan extends CommandBase {

    @Override
    public String getCommandName() {
        return "scan";
    }

    @Override
    public void execute(String[] args) {
        Long startPlayerId = null;

        LocalDate snapshotDateDate = LocalDate.now().minusDays(1);

        for (int idx = 1; idx < args.length; idx++) {
            String arg = args[idx];

            try {
                snapshotDateDate = LocalDate.parse(arg);
            } catch (DateTimeParseException ex) {
                startPlayerId = Long.valueOf(arg);
            }
        }

        String snapshotDate = snapshotDateDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        loadFromFile();

        int playerCountAtTheBeginning = playerBase.getPlayers().size();

        if (isNull(startPlayerId)) {
            System.out.println("Updating from the first player from snapshot with date " + snapshotDate);

            PlayerWithRank playerWithRank = AutoRepeater.perform(() -> wotRestService.getFirstPlayer(snapshotDate));
            startPlayerId = playerWithRank.getAccount_id();
            playerBase.addPlayerIfNew(playerWithRank);
        } else {
            System.out.println("Updating from player id " + startPlayerId + " from snapshot with date " + snapshotDate);
        }

        Long currentRank = 1L;
        Long currentPlayerId = startPlayerId;

        while (currentRank < 500000) {
            System.out.print("Processing rank " + currentRank + ", found " + (playerBase.getPlayers().size() - playerCountAtTheBeginning) + " new players so far          \r");
            final Long finalStartPlayerId = currentPlayerId;
            List<PlayerWithRank> players = AutoRepeater.perform(() -> wotRestService.getNextPlayers(snapshotDate, finalStartPlayerId, 50L));

            players.stream().forEach(playerBase::addPlayerIfNew);

            PlayerWithRank lastPlayerInBatch = players.get(players.size()-1);
            currentRank = lastPlayerInBatch.getGlobal_rating().getRank();
            currentPlayerId = lastPlayerInBatch.getAccount_id();

            if (currentRank % 1000 == 0) {
                saveToFile(false);
            }
        }

        System.out.println("Last processed rank " + currentRank + ", found " + (playerBase.getPlayers().size() - playerCountAtTheBeginning) + " new players                ");

        saveToFile(true);
    }


}
