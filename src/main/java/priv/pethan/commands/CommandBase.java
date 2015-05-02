package priv.pethan.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import priv.pethan.data.PlayerBase;
import priv.pethan.rest.WOTRestService;

import java.io.File;
import java.io.IOException;

import static priv.pethan.Main.dataFileName;

public abstract class CommandBase implements Command {
    protected PlayerBase playerBase;

    protected WOTRestService wotRestService = new WOTRestService();
    private ObjectMapper objectMapper = new ObjectMapper();

    public void loadFromFile() {
        try {
            File file = new File(dataFileName);
            if (file.exists()) {
                playerBase = objectMapper.readValue(file, PlayerBase.class);
                System.out.println("Read " + playerBase.getPlayers().size() + " results from file " + dataFileName);
            } else {
                playerBase = new PlayerBase();
                System.out.println("Created new file " + dataFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(boolean showMessage) {
        try {
            objectMapper.writeValue(new File(dataFileName), playerBase);
            if (showMessage) {
                System.out.println("Wrote " + playerBase.getPlayers().size() + " results to file " + dataFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
