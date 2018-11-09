package com.rexcantor64.triton.commands;

import com.rexcantor64.triton.MultiLanguagePlugin;
import com.rexcantor64.triton.guiapi.Gui;
import com.rexcantor64.triton.guiapi.GuiButton;
import com.rexcantor64.triton.guiapi.ScrollableGui;
import com.rexcantor64.triton.language.Language;
import com.rexcantor64.triton.language.LanguageManager;
import com.rexcantor64.triton.wrappers.items.ItemStackParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenSelectorCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage("Only Players.");
            return true;
        }

        Player p = (Player) s;

        if (!p.hasPermission("multilanguageplugin.openselector") && !p.hasPermission("triton.openselector")) {
            p.sendMessage(MultiLanguagePlugin.get().getMessage("error.no-permission", "&cNo permission. Permission required: &4%1", "triton.openselector"));
            return true;
        }

        openLanguagesSelectionGUI(p);
        return true;
    }

    private void openLanguagesSelectionGUI(Player p) {
        LanguageManager language = MultiLanguagePlugin.get().getLanguageManager();
        Language pLang = MultiLanguagePlugin.asSpigot().getPlayerManager().get(p.getUniqueId()).getLang();
        Gui gui = new ScrollableGui(MultiLanguagePlugin.get().getMessage("other.selector-gui-name", "&aSelect a language"));
        for (Language lang : language.getAllLanguages())
            gui.addButton(new GuiButton(ItemStackParser.bannerToItemStack(lang.getBanner(), pLang.equals(lang))).setListener(event -> {
                MultiLanguagePlugin.asSpigot().getPlayerManager().get(p.getUniqueId()).setLang(lang);
                p.closeInventory();
                p.sendMessage(MultiLanguagePlugin.get().getMessage("success.selector", "&aLanguage changed to %1", lang.getDisplayName()));
            }));
        gui.open(p);
    }

}