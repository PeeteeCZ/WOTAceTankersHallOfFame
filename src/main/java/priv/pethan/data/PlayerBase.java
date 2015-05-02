package priv.pethan.data;

import lombok.Data;
import priv.pethan.rest.PlayerWithRank;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerBase {
    private Map<Long, Player> players = new HashMap<>();

    public void addPlayerIfNew(PlayerWithRank playerWithRank) {
        if (!players.containsKey(playerWithRank.getAccount_id())) {
            players.put(playerWithRank.getAccount_id(), new Player());
        }
    }
}
