package priv.pethan.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerList {
    private String snapshotDate;
    private List<Player> players = new ArrayList<>();

    public void add(Player player) {
        players.add(player);
    }

    public void addAll(List<Player> playersBatch) {
        players.addAll(playersBatch);
    }
}
