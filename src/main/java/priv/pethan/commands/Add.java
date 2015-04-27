package priv.pethan.commands;

import priv.pethan.data.Player;

import java.util.Comparator;
import java.util.List;

public class Add extends CommandBase {

    @Override
    public String getCommandName() {
        return "add";
    }

    @Override
    public void execute(String[] args) {
        loadFromFile();

        long countToAdd = (args.length == 1) ? 50 : Long.valueOf(args[1]);

        Player startingPlayer;
        if (playerList.getPlayers().isEmpty()) {
            Player firstPlayer = wotRestService.getFirstPlayer(playerList.getSnapshotDate());
            updateNameAndAceTankers(firstPlayer);
            playerList.add(firstPlayer);
            startingPlayer = firstPlayer;
            countToAdd--;
        } else {
            startingPlayer = playerList.getPlayers().stream().max(Comparator.comparingLong(Player::getRank)).get();
        }

        while(countToAdd > 0) {
            System.out.println("Remaining to load: " + countToAdd);
            long batchSize = (countToAdd <= 50) ? countToAdd : 50;

            List<Player> playersBatch = wotRestService.getNextPlayers(playerList.getSnapshotDate(), startingPlayer, batchSize);
            playersBatch.stream().forEach(this::updateNameAndAceTankers);
            playerList.addAll(playersBatch);

            startingPlayer = playersBatch.get(playersBatch.size() - 1);

            countToAdd -= batchSize;
            saveToFile();
        }
    }

    private void updateNameAndAceTankers(Player player) {
        player.setName(wotRestService.getPlayerName(player.getId()));
        player.setAceTankers(wotRestService.getPlayerAceTankers(player.getId()));
    }


}
