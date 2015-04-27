package priv.pethan.commands;

import priv.pethan.data2.Player;
import priv.pethan.data2.PlayerBase;

import static java.util.stream.Collectors.toList;

public class Migrate extends CommandBase {
    @Override
    public String getCommandName() {
        return "migrate";
    }

    @Override
    public void execute(String[] args) {
        loadFromFileOld("AceTankers.json");

        playerBase = new PlayerBase();
        playerBase.setPlayers(playerList.getPlayers().stream().map(old -> { Player np = new Player();
                                                                            np.setId(old.getId());
                                                                            np.setName(old.getName());
                                                                            return np;
                                                                          }).collect(toList()));

        saveToFile();
    }
}
