package priv.pethan.commands;

import com.google.common.collect.Lists;
import priv.pethan.autorepeater.AutoRepeater;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class Name extends CommandBase {

    @Override
    public String getCommandName() {
        return "name";
    }

    @Override
    public void execute(String[] args) {
        loadFromFile();

        List<Long> nameless = playerBase.getPlayers().entrySet().stream()
                .filter(entry -> isNull(entry.getValue().getName()))
                .map(Entry::getKey)
                .collect(toList());

        int counter = 0;
        for(List<Long> batch : Lists.partition(nameless, 100)) {
            counter += batch.size();
            System.out.print("Processing " + counter + " of " + nameless.size() + "        \r");
            addNames(batch);

            if (counter % 1000 == 0) {
                saveToFile(false);
            }
        }

        System.out.println();

        saveToFile(true);
    }

    private void addNames(List<Long> batch) {
        Map<Long, String> names = AutoRepeater.perform(() -> wotRestService.getPlayerNames(batch));
        names.entrySet().stream().forEach(entry -> { playerBase.getPlayers().get(entry.getKey()).setName(entry.getValue()); });
    }


}
