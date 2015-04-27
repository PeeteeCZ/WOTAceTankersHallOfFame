package priv.pethan.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import priv.pethan.data.PlayerList;
import priv.pethan.data2.PlayerBase;
import priv.pethan.rest.WOTRestService;

import java.io.File;
import java.io.IOException;

import static priv.pethan.Main.dataFileName;

public abstract class CommandBase implements Command {
    protected PlayerList playerList;
    protected PlayerBase playerBase;

    protected WOTRestService wotRestService = new WOTRestService();
    private ObjectMapper objectMapper = new ObjectMapper();

    public void loadFromFile() {
        try {
            playerBase = objectMapper.readValue(new File(dataFileName), PlayerBase.class);
            System.out.println("Read " + playerBase.getPlayers().size() + " results from file " + dataFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile() {
        try {
            objectMapper.writeValue(new File(dataFileName), playerBase);
            System.out.println("Wrote " + playerBase.getPlayers().size() + " results to file " + dataFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFileOld(String fileName) {
        try {
            playerList = objectMapper.readValue(new File(fileName), PlayerList.class);
            System.out.println("Read " + playerList.getPlayers().size() + " results from file " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
