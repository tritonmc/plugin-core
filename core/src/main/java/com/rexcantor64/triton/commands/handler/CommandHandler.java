package com.rexcantor64.triton.commands.handler;

import com.rexcantor64.triton.Triton;
import com.rexcantor64.triton.commands.*;
import lombok.val;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandHandler {

    public static HashMap<String, Command> commands = new HashMap<>();

    static {
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand());
        commands.put("openselector", new OpenSelectorCommand());
        commands.put("getflag", new GetFlagCommand());
        commands.put("setlanguage", new SetLanguageCommand());
        commands.put("reload", new ReloadCommand());
        commands.put("sign", new SignCommand());
        commands.put("database", new DatabaseCommand());
        commands.put("twin", new TwinCommand());
    }

    public void handleCommand(CommandEvent event) {
        if (event.getLabel().equalsIgnoreCase("twin"))
            event = new CommandEvent(event.getSender(), "twin", event
                    .getSubCommand() == null ? new String[0] : new String[]{event.getSubCommand()}, "twin", event
                    .getEnvironment());

        try {
            // No args given
            Command command = commands.get(event.getSubCommand() == null ? "openselector" : event.getSubCommand());

            if (command == null)
                command = commands.get("help");

            if (!command.handleCommand(event) && Triton.isBungee())
                Triton.asBungee().getBridgeManager().forwardCommand(event);
        } catch (NoPermissionException e) {
            event.getSender().sendMessageFormatted("error.no-permission", e.getPermission());
        }
    }

    protected List<String> handleTabCompletion(CommandEvent event) {
        if (event.getLabel().equalsIgnoreCase("twin")) return Collections.emptyList();

        val subCommand = event.getSubCommand();

        try {
            if (subCommand == null || event.getArgs().length == 0)
                return commands.keySet().stream().filter(cmd -> subCommand != null && cmd.startsWith(subCommand))
                        .collect(Collectors.toList());

            val command = commands.get(subCommand);
            if (command == null)
                return Collections.emptyList();

            return command.handleTabCompletion(event);
        } catch (NoPermissionException e) {
            return Collections.emptyList();
        }
    }

}