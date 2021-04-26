package me.devrik.organicmodelbuilder;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ModelsCommands {
    private final ModelsPlugin plugin;
    private final Map<String, ModelsCommands.CommandContainer> commands = new HashMap();
    private final Map<String, String> aliases = new HashMap();
    private final int perPageHelp = 5;
    private final int maxPageHelp;

    public ModelsCommands(ModelsPlugin plugin) {
        this.plugin = plugin;
        this.addCommand(new String[]{"load", "new", "create"}, "This command is used to load a model. To get the list of all the available models, use /model list.\nThe scale is a number where 1.0 is the base scale.\nIf no pattern is set, the blocks from the schematics will be used.", "<model> [scale] [pattern]", "models.use");
        this.addCommand(new String[]{"cancel", "abort", "stop"}, "Cancel the creation where it is", "", "model.use");
        this.addCommand(new String[]{"rotate", "rot", "setroll", "roll"}, "Use this command to set the roll value of the next part you paste.", "<roll>", "models.use");
        this.addCommand(new String[]{"adjust", "modify", "set"}, "Use this command to adjust the rotation of a certain part.", "<partName> <yaw> <pitch> <roll>", "models.use");
        this.addCommand(new String[]{"validate", "finalise", "complete", "end"}, "Use this command to complete your model.", "", "models.use");
        this.addCommand(new String[]{"list"}, "Use this command to list the available models.", "", "models.list");
        this.maxPageHelp = this.commands.size() / 5 + (this.commands.size() % 5 == 0 ? 0 : 1);
    }

    private void addCommand(String[] aliases, String desc, String usage, String permission) {
        ModelsCommands.CommandContainer container = new ModelsCommands.CommandContainer(aliases, desc, usage, permission);
        String mainCmd = aliases[0];
        this.commands.put(mainCmd, container);
        String[] var7 = aliases;
        int var8 = aliases.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String alias = var7[var9];
            this.aliases.put(alias, mainCmd);
        }

    }

    public boolean onCommand(String commandName, Player p, String[] args) {
        try {
            if ("help".equalsIgnoreCase(commandName)) {
                if (!p.hasPermission("models.help")) {
                    p.print(ChatColor.RED + "You don't have the permission to use that command.");
                }

                this.printHelp(p, args);
                return true;
            } else if (!this.aliases.containsKey(commandName)) {
                throw new CommandException(ChatColor.RED + "Unknown Command, type " + ChatColor.RED + ChatColor.ITALIC + " /model help " + ChatColor.RESET + ChatColor.RED + " for more");
            } else if (!p.hasPermission(((ModelsCommands.CommandContainer)this.commands.get(this.aliases.get(commandName))).permission_)) {
                throw new CommandException(ChatColor.RED + "You don't have the permission to use that command.");
            } else {
                String var4 = (String)this.aliases.get(commandName);
                byte var5 = -1;
                switch(var4.hashCode()) {
                case -1422313585:
                    if (var4.equals("adjust")) {
                        var5 = 2;
                    }
                    break;
                case -1421272810:
                    if (var4.equals("validate")) {
                        var5 = 3;
                    }
                    break;
                case -1367724422:
                    if (var4.equals("cancel")) {
                        var5 = 5;
                    }
                    break;
                case 113111:
                    if (var4.equals("rot")) {
                        var5 = 1;
                    }
                    break;
                case 3322014:
                    if (var4.equals("list")) {
                        var5 = 4;
                    }
                    break;
                case 3327206:
                    if (var4.equals("load")) {
                        var5 = 0;
                    }
                }

                switch(var5) {
                case 0:
                    this.load(p, args);
                    break;
                case 1:
                    this.roll(p, args);
                    break;
                case 2:
                    this.modify(p, args);
                    break;
                case 3:
                    this.finalise(p);
                    break;
                case 4:
                    this.list(p);
                    break;
                case 5:
                    this.cancel(p);
                }

                return true;
            }
        } catch (CommandException var6) {
            p.print(var6.getMessage());
            return true;
        }
    }

    private void printHelp(Player p, String[] args) throws CommandException {
        if (args.length == 0) {
            this.printHelp(p, 1);
        }

        if (args.length == 0) {
            this.printHelp(p, 1);
        } else if (this.aliases.containsKey(args[0].toLowerCase())) {
            this.printHelp(p, (ModelsCommands.CommandContainer)this.commands.get(this.aliases.get(args[0])));
        } else {
            int page;
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException var5) {
                page = 1;
            }

            this.printHelp(p, page);
        }

    }

    private void printHelp(Player p, ModelsCommands.CommandContainer commandContainer) {
        p.print(ChatColor.AQUA + String.format("==========%s%s%s=========", ChatColor.GOLD.toString(), commandContainer.aliases_[0], ChatColor.AQUA.toString()));
        String[] var3 = commandContainer.desc_.split("\n");
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String desc = var3[var5];
            p.print(ChatColor.GOLD + String.format("    %s", desc));
        }

        p.print(" ");
        p.print(ChatColor.GOLD + commandContainer.usage_);
        p.print(ChatColor.AQUA + "===================");
    }

    private void printHelp(Player p, int page) throws CommandException {
        if (page >= 1 && page <= this.maxPageHelp) {
            org.bukkit.entity.Player player = Bukkit.getPlayer(p.getUniqueId());
            player.spigot().sendMessage((new ComponentBuilder("========= ")).color(net.md_5.bungee.api.ChatColor.AQUA).append("help (" + page + "/" + this.maxPageHelp + ")").color(net.md_5.bungee.api.ChatColor.GOLD).append("=========").color(net.md_5.bungee.api.ChatColor.AQUA).create());
            player.sendMessage("  Click a command to get more informations");
            int i = (page - 1) * 5;

            for(ModelsCommands.CommandContainer[] containers = (ModelsCommands.CommandContainer[])this.commands.values().toArray(new ModelsCommands.CommandContainer[0]); i < page * 5 && i < containers.length; ++i) {
                ModelsCommands.CommandContainer container = containers[i];
                HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(container.desc_)).color(net.md_5.bungee.api.ChatColor.GREEN).create());
                ClickEvent click = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/model help " + container.aliases_[0]);
                player.spigot().sendMessage((new ComponentBuilder("  -  /model ")).append(container.aliases_[0]).color(net.md_5.bungee.api.ChatColor.GOLD).event(hover).event(click).append(" : ").append(container.usage_).italic(true).color(net.md_5.bungee.api.ChatColor.YELLOW).create());
            }

            if (this.maxPageHelp != 1) {
                ComponentBuilder builder = (new ComponentBuilder("    ")).append("<<").color(page == 1 ? net.md_5.bungee.api.ChatColor.GRAY : net.md_5.bungee.api.ChatColor.GOLD);
                if (page != 1) {
                    builder = builder.event(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/model help " + (page - 1))).event(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder("Last page")).create()));
                }

                builder = builder.append("            ");
                builder = builder.append(">>").color(page == this.maxPageHelp ? net.md_5.bungee.api.ChatColor.GRAY : net.md_5.bungee.api.ChatColor.GOLD);
                if (page != this.maxPageHelp) {
                    builder = builder.event(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/model help " + (page + 1))).event(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder("Next page")).create()));
                }

                player.spigot().sendMessage(builder.create());
            }

            player.spigot().sendMessage((new ComponentBuilder("========================")).color(net.md_5.bungee.api.ChatColor.AQUA).create());
        } else {
            throw new CommandException("This isn't a valid page number.");
        }
    }

    public boolean load(Player p, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new CommandException(ChatColor.RED + "There isn't enough arguments");
        } else {
            String model = args[0];
            double scale = 1.0D;
            if (args.length > 1) {
                try {
                    scale = Double.parseDouble(args[1]);
                } catch (NumberFormatException var10) {
                    throw new CommandException(ChatColor.RED + "The argument " + args[1] + " isn't a number.");
                }
            }

            if (scale > 2.0D) {
                throw new CommandException(ChatColor.RED + "The scale is too big, the maximum allowed is 2.0.");
            } else {
                Pattern pattern = null;
                if (args.length > 2) {
                    ParserContext parserContext = new ParserContext();
                    parserContext.setActor(p);
                    parserContext.setExtent(p.getWorld());
                    parserContext.setWorld(p.getWorld());
                    parserContext.setSession(WorldEdit.getInstance().getSessionManager().get(p));

                    try {
                        pattern = (Pattern)WorldEdit.getInstance().getPatternFactory().parseFromInput(args[2], parserContext);
                    } catch (InputParseException var9) {
                        throw new CommandException(ChatColor.RED + "Unable to parse the pattern : " + var9.getMessage());
                    }
                }

                if (this.plugin.currents.containsKey(p.getUniqueId())) {
                    throw new CommandException(ChatColor.RED + "You can't load a new model, you're already creating one.");
                } else if (!this.plugin.registry.containsKey(model.toLowerCase())) {
                    throw new CommandException(ChatColor.RED + "The model " + model + " isn't known. Use /model list to list the available models.");
                } else {
                    Model m = new Model((ModelPart)this.plugin.registry.get(model.toLowerCase()), scale, pattern, p.getWorld());
                    this.plugin.currents.put(p.getUniqueId(), m);
                    p.print(ChatColor.GREEN + "Model successfully loaded.");
                    p.print(ChatColor.YELLOW + "Place a part by clicking in the direction you want it to be facing.");
                    p.print(ChatColor.YELLOW + "You can modify the roll value of the next part by using");
                    p.print("" + ChatColor.WHITE + ChatColor.ITALIC + "/model roll <value>");
                    return true;
                }
            }
        }
    }

    private void cancel(Player p) throws CommandException {
        if (!this.plugin.currents.containsKey(p.getUniqueId())) {
            throw new CommandException("You aren't creating a model, you can't use this command now.");
        } else {
            Model current = (Model)this.plugin.currents.get(p.getUniqueId());
            current.stop(this.plugin, p);
        }
    }

    public void roll(Player p, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new CommandException(ChatColor.RED + "There isn't enough arguments");
        } else {
            double rot = 0.0D;

            try {
                rot = Double.parseDouble(args[0]);
            } catch (NumberFormatException var6) {
                throw new CommandException(ChatColor.RED + "The argument " + args[1] + " isn't a number.");
            }

            if (!this.plugin.currents.containsKey(p.getUniqueId())) {
                throw new CommandException("You aren't creating a model, you can't use this command now.");
            } else {
                Model current = (Model)this.plugin.currents.get(p.getUniqueId());
                current.currentRoll = rot % 360.0D;
                p.print(ChatColor.GREEN + "Operation successful, the next part you paste will have a roll value of " + current.currentRoll + "°");
            }
        }
    }

    public void modify(Player p, String[] args) throws CommandException {
        if (args.length < 4) {
            throw new CommandException(ChatColor.RED + "There isn't enough arguments");
        } else {
            String partName = args[0];

            double yaw;
            try {
                yaw = Double.parseDouble(args[1]);
            } catch (NumberFormatException var13) {
                throw new CommandException(ChatColor.RED + "The argument " + args[1] + " isn't a number.");
            }

            double pitch;
            try {
                pitch = Double.parseDouble(args[2]);
            } catch (NumberFormatException var12) {
                throw new CommandException(ChatColor.RED + "The argument " + args[2] + " isn't a number.");
            }

            double roll;
            try {
                roll = Double.parseDouble(args[3]);
            } catch (NumberFormatException var11) {
                throw new CommandException(ChatColor.RED + "The argument " + args[3] + " isn't a number.");
            }

            if (!this.plugin.currents.containsKey(p.getUniqueId())) {
                throw new CommandException("You aren't creating a model, you can't use this command now.");
            } else {
                Model current = (Model)this.plugin.currents.get(p.getUniqueId());
                if (current.modify(p, partName, yaw, pitch, roll)) {
                    sendMessages(p, new String[]{ChatColor.GREEN + "The part was successfully modified, the new values are :", String.format("YAW=%.2f PITCH=%.2f ROLL=%.2f", yaw, pitch, roll)});
                }
            }
        }
    }

    public void finalise(Player p) throws CommandException {
        if (!this.plugin.currents.containsKey(p.getUniqueId())) {
            throw new CommandException(ChatColor.RED + "You aren't creating a model, you can't use this command now.");
        } else {
            Model current = this.plugin.currents.get(p.getUniqueId());
            if (!current.isAtTheEnd()) {
                throw new CommandException(ChatColor.RED + "Your model isn't completed yet, you cannot validate it.");
            } else {
                current.finalise(this.plugin, p);
            }
        }
    }

    public void list(Player p) throws CommandException {
        String text = this.join(this.plugin.registry.keySet(), "\n   - " + ChatColor.YELLOW);
        sendMessages(p, (ChatColor.YELLOW + "The loaded models are :\n   - " + ChatColor.YELLOW + text).split("\n"));
    }

    private String join(Set<String> strings, String s) {
        String stot = "";
        int i = 0;

        for(Iterator var5 = strings.iterator(); var5.hasNext(); ++i) {
            String key = (String)var5.next();
            stot = stot + key + (i < strings.size() - 1 ? s : "");
        }

        return stot;
    }

    public static void sendMessages(Player p, String[] msgs) {
        String[] var2 = msgs;
        int var3 = msgs.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            p.print(s);
        }

    }

    private class CommandContainer {
        final String[] aliases_;
        final String desc_;
        final String usage_;
        final String permission_;

        CommandContainer(String[] aliases, String desc, String usage, String perm) {
            this.aliases_ = aliases;
            this.desc_ = desc;
            this.usage_ = "/model " + this.aliases_[0] + " " + usage;
            this.permission_ = perm;
        }
    }
}
