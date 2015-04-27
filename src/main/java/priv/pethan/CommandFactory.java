package priv.pethan;

import org.reflections.Reflections;
import priv.pethan.commands.Command;
import priv.pethan.commands.CommandBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandFactory {
    private Map<String, Command> commands;

    public CommandFactory() {
        commands = new HashMap<>();

        Reflections reflections = new Reflections("priv.pethan");
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);

        for(Class<? extends Command> clazz : commandClasses) {
            try {
                if (clazz != CommandBase.class) {
                    Command command = clazz.newInstance();
                    commands.put(command.getCommandName(), command);
                }
            } catch (IllegalAccessException|InstantiationException e) {
                System.out.println("Failed to create command instance " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

    public Command getCommand(String commandName) {
        return commands.get(commandName);
    }
}
