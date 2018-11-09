package com.rexcantor64.triton.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rexcantor64.triton.MultiLanguagePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class MainCMD implements CommandExecutor, TabCompleter {

    private HashMap<String, CommandExecutor> subCommands = Maps.newHashMap();
    private HashMap<String, TabCompleter> subCompleters = Maps.newHashMap();

    public MainCMD() {
        setCommandAndCompleter("getflag", new GetFlagCMD());
        setCommandAndCompleter("setlanguage", new SetLanguageCMD());
        subCommands.put("openselector", new OpenSelectorCMD());
        subCommands.put("reload", new ReloadCMD());
        setCommandAndCompleter("sign", new SignCMD());
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            subCommands.get("openselector").onCommand(s, cmd, label, args);
            return true;
        }
        for (Entry<String, CommandExecutor> entry : subCommands.entrySet())
            if (entry.getKey().equalsIgnoreCase(args[0]))
                return entry.getValue().onCommand(s, cmd, label, args);

        if (!s.hasPermission("multilanguageplugin.help") && !s.hasPermission("triton.help")) {
            s.sendMessage(MultiLanguagePlugin.get().getMessage("error.no-permission", "&cNo permission. Permission required: &4%1", "triton.help"));
            return true;
        }

        for (String str : MultiLanguagePlugin.get().getMessageList("help.menu", "&a---------MultiLanguagePlugin---------", "&6Available commands:", "%1", "&a---------MultiLanguagePlugin---------"))
            if (str.equalsIgnoreCase("%1"))
                for (String command : subCommands.keySet())
                    s.sendMessage(MultiLanguagePlugin.get().getMessage("help.menu-item", "&6/%1 %2", label, command));
            else
                s.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        if (args.length == 0)
            return new ArrayList<>(subCommands.keySet());
        for (Entry<String, TabCompleter> entry : subCompleters.entrySet())
            if (entry.getKey().equalsIgnoreCase(args[0]))
                return entry.getValue().onTabComplete(s, cmd, label, args);
        List<String> tab = Lists.newArrayList();
        if (args.length == 1)
            for (String c : subCommands.keySet())
                if (c.toLowerCase().startsWith(args[0].toLowerCase()))
                    tab.add(c);
        return tab;
    }

    private void setCommandAndCompleter(String name, Object commandAndCompleter) {
        subCommands.put(name, (CommandExecutor) commandAndCompleter);
        subCompleters.put(name, (TabCompleter) commandAndCompleter);
    }

}