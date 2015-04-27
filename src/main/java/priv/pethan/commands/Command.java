package priv.pethan.commands;

public interface Command {
    String getCommandName();
    void execute(String[] args);
}
