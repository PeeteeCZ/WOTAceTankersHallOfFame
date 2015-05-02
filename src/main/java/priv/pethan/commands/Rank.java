package priv.pethan.commands;

import com.google.common.collect.Lists;
import priv.pethan.autorepeater.AutoRepeater;
import priv.pethan.data.Player;
import priv.pethan.data.TimePoint;
import priv.pethan.rest.PlayerWithRank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class Rank extends CommandBase {

    @Override
    public String getCommandName() {
        return "rank";
    }

    @Override
    public void execute(String[] args) {
        LocalDate snapshotDateDate = LocalDate.now().minusDays(1);
        String snapshotDate = snapshotDateDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        System.out.println("Retrieving data from snapshot " + snapshotDate);

        loadFromFile();

        List<Long> toRank = playerBase.getPlayers().entrySet().stream()
                .filter(entry -> !entry.getValue().getTimePoints().containsKey(snapshotDate))
                .map(Entry::getKey)
                .collect(toList());

        int counter = 0;
        for(List<Long> batch : Lists.partition(toRank, 100)) {
            counter += batch.size();
            System.out.print("Processing " + counter + " of " + toRank.size() + "        \r");
            addWOTRating(batch, snapshotDate);
            addAceTankers(batch, snapshotDate);

            if (counter % 1000 == 0) {
                saveToFile(false);
            }
        }

        System.out.println();

        saveToFile(false);

        System.out.print("Sorting by ace tankers                     \r");
        List<Player> sortedByAceTankers = playerBase.getPlayers().entrySet().stream()
                .map(Entry::getValue)
                .sorted((p1, p2) -> {
                    TimePoint t1 = p1.getTimePoints().get(snapshotDate);
                    TimePoint t2 = p2.getTimePoints().get(snapshotDate);

                    if (isNull(t1)) {
                        if (isNull(t2)) return 0;
                        return 1;
                    }

                    if (isNull(t1.getAceTankers())) {
                        if (isNull(t2.getAceTankers())) return 0;
                        return 1;
                    }

                    if (t1.getAceTankers().equals(t2.getAceTankers())) {
                        if (isNull(t1.getWotRank())) {
                            if (isNull(t2.getWotRank())) return 0;
                            return -1;
                        }
                        return t1.getWotRank().compareTo(t2.getWotRank());
                    }

                    return -t1.getAceTankers().compareTo(t2.getAceTankers());
                })
                .collect(toList());

        System.out.println();

        long aceRank = 0;
        Long lastAceTankers = 0L;
        for(Player player : sortedByAceTankers) {
            System.out.print("Marking ace tankers rank " + aceRank + "         \r");
            TimePoint timePoint = player.getTimePoints().get(snapshotDate);
            if (nonNull(timePoint)) {
                if (!timePoint.getAceTankers().equals(lastAceTankers)) {
                    aceRank++;
                    lastAceTankers = timePoint.getAceTankers();
                }
                timePoint.setAceTankersRank(aceRank);
            }
        }

        System.out.println();

        saveToFile(true);
    }

    private void addWOTRating(List<Long> batch, String snapshotDate) {
        Map<Long, PlayerWithRank> playerRanks = AutoRepeater.perform(() -> wotRestService.getWOTRankings(batch));

        for(Entry<Long, PlayerWithRank> rankedPlayer : playerRanks.entrySet()) {
            TimePoint timePoint = getPlayerTimePoint(rankedPlayer.getKey(), snapshotDate);

            if (isNull(rankedPlayer.getValue())) {
                System.out.println();
                System.out.println("No value for rating for player " + rankedPlayer.getKey());
                timePoint.setWotRank(0L);
                timePoint.setWotRating(0L);
            } else {
                timePoint.setWotRank(isNull(rankedPlayer.getValue().getGlobal_rating().getRank()) ? 0L : rankedPlayer.getValue().getGlobal_rating().getRank());
                timePoint.setWotRating(rankedPlayer.getValue().getGlobal_rating().getValue());
            }
        }
    }

    private void addAceTankers(List<Long> batch, String snapshotDate) {
        Map<Long, Long> aceTankers = AutoRepeater.perform(() -> wotRestService.getAceTankers(batch));

        for(Entry<Long, Long> player : aceTankers.entrySet()) {
            TimePoint timePoint = getPlayerTimePoint(player.getKey(), snapshotDate);

            if (isNull(player.getValue())) {
                System.out.println("Removing player " + player.getKey() + " because it does not exist          ");
                timePoint.setAceTankers(0L);
                playerBase.getPlayers().remove(player.getKey());
            } else {
                timePoint.setAceTankers(player.getValue());
            }
        }
    }

    private TimePoint getPlayerTimePoint(Long playerId, String snapshotDate) {
        Player dbPlayer = playerBase.getPlayers().get(playerId);

        TimePoint timePoint = dbPlayer.getTimePoints().get(snapshotDate);
        if (isNull(timePoint)) {
            timePoint = new TimePoint();
            dbPlayer.getTimePoints().put(snapshotDate, timePoint);
        }

        return timePoint;
    }
}
