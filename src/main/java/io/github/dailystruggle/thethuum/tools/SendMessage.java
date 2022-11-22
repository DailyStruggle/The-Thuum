package io.github.dailystruggle.thethuum.tools;

import io.github.dailystruggle.commandsapi.common.CommandsAPI;
import io.github.dailystruggle.thethuum.tools.softdepends.PAPIChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.tozymc.spigot.api.title.TitleApi;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendMessage {
    private static final Pattern hexColorPattern1 = Pattern.compile("(&?#[0-9a-fA-F]{6})");
    private static final Pattern hexColorPattern2 = Pattern.compile("(&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F]&[0-9a-fA-F])");

    public static void sendMessage(CommandSender target1, CommandSender target2, String message) {
        if(message == null || message.isEmpty()) return;
        sendMessage(target1,message);
        if(!target1.getName().equals(target2.getName())) {
            sendMessage(target2, message);
        }
    }

    public static void sendMessage(CommandSender sender, String message) {
        if(message == null || message.isEmpty()) return;
        if(sender instanceof Player) sendMessage((Player) sender,message);
        else {
            message = format(Bukkit.getOfflinePlayer(CommandsAPI.serverId),message);
//            if(Version.getServerIntVersion() >=12) {
//                BaseComponent[] components = TextComponent.fromLegacyText(message);
//                sender.spigot().sendMessage(components);
//            }
//            else
            sender.sendMessage(message);
        }
    }

    public static void sendMessage(Player player, String message) {
        if(message == null || message.isEmpty()) return;
        message = format(player,message);
//        if(Version.getServerIntVersion() >=12) {
//            BaseComponent[] components = TextComponent.fromLegacyText(message);
//            player.spigot().sendMessage(components);
//        }
//        else
            player.sendMessage(message);
    }

    public static void sendMessage(CommandSender sender, String message, String hover, String click) {
        if(message.equals("")) return;

        OfflinePlayer player;
        if(sender instanceof Player) player = (OfflinePlayer) sender;
        else player = Bukkit.getOfflinePlayer(CommandsAPI.serverId).getPlayer();

        message = format(player,message);

//        if(Version.getServerIntVersion() >=12) {
//            BaseComponent[] textComponents = TextComponent.fromLegacyText(message);
//
//            if (!hover.equals("")) {
//                BaseComponent[] hoverComponents = TextComponent.fromLegacyText(format(player, hover));
//                //noinspection deprecation
//                HoverEvent hoverEvent = (Version.getServerIntVersion()>=16)
//                        ? new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverComponents))
//                        : new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponents);
//                for (BaseComponent component : textComponents) {
//                    component.setHoverEvent(hoverEvent);
//                }
//            }
//
//            if (!click.equals("")) {
//                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click);
//                for (BaseComponent component : textComponents) {
//                    component.setClickEvent(clickEvent);
//                }
//            }
//
//            sender.spigot().sendMessage(textComponents);
//        }
//        else
            sender.sendMessage(message);
    }

    public static String format(OfflinePlayer player, String text) {
        if(text == null) return "";

        //check PAPI exists and fill remaining PAPI placeholders
        //todo: if a null player doesn't work with another PAPI import, blame that import for not verifying its inputs.
        text = formatDry(player,text);
        

        return text;
    }

    public static String formatDry(OfflinePlayer player, String text) {
        if(text == null) return "";

        //check PAPI exists and fill remaining PAPI placeholders
        //todo: if a null player doesn't work with another PAPI import, blame that import for not verifying its inputs.
        text = PAPIChecker.fillPlaceholders(player,text);


        text = ChatColor.translateAlternateColorCodes('&',text);
        text = Hex2Color(text);
        return text;
    }

    private static String Hex2Color(String text) {
        //reduce patterns
        if(text == null) return "";
        Matcher matcher2 = hexColorPattern2.matcher(text);
        while (matcher2.find()) {
            String hexColor = text.substring(matcher2.start(), matcher2.end());
            String shortColor = "#" + hexColor.replaceAll("&","");
            text = text.replaceAll(hexColor, shortColor);
        }

        //colorize
        Matcher matcher1 = hexColorPattern1.matcher(text);
        while (matcher1.find()) {
            String hexColor = text.substring(matcher1.start(), matcher1.end());
            String bukkitColor;
            StringBuilder bukkitColorCode = new StringBuilder('\u00A7' + "x");
            for (int i = hexColor.indexOf('#')+1; i < hexColor.length(); i++) {
                bukkitColorCode.append('\u00A7').append(hexColor.charAt(i));
            }
            bukkitColor = bukkitColorCode.toString().toLowerCase();
            text = text.replaceAll(hexColor, bukkitColor);
            matcher1.reset(text);
        }
        return text;
    }

    public static void log(Level level, String message) {
        if(message.isEmpty()) return;

        message = format(null,message);

        Logger logger = Bukkit.getLogger();
        if(logger!=null) logger.log(level,message);
    }

    public static void log(Level level, String message, Exception exception) {
        if(message.isEmpty()) return;

        message = format(null,message);

        Bukkit.getLogger().log(level,message,exception);
    }

    public static void title(Player player, String title, String subtitle, int in, int stay, int out) {
        boolean noTitle = title == null || title.isEmpty();
        boolean noSubtitle = subtitle == null || subtitle.isEmpty();

        if(noTitle && noSubtitle) return;

        if(title!=null) title = Hex2Color(ChatColor.translateAlternateColorCodes('&',title));
        if(subtitle!=null) subtitle = Hex2Color(ChatColor.translateAlternateColorCodes('&',subtitle));

        if(Version.getServerIntVersion()<18) TitleApi.sendTitle(player,title,subtitle,in,stay,out);
        else player.sendTitle(title,subtitle,in,stay,out);
    }

    public static void actionbar(Player player, String bar) {
        if(bar == null || bar.isEmpty()) return;
        bar = Hex2Color(ChatColor.translateAlternateColorCodes('&',bar));
        TitleApi.sendActionbar(player,bar);
    }
}
