package eu.benni1000.itemfilter;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Commands implements CommandExecutor {

    private ArrayList<ItemfilterPlayer> players;
    private ConfigurationHandler configHandler;
    private Configuration config;
    private SaveHandler handler;

    public Commands(ArrayList<ItemfilterPlayer> players, SaveHandler handler, ConfigurationHandler configHandler, Configuration config) {
        this.players = players;
        this.handler = handler;
        this.configHandler = configHandler;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length < 1) {
            printHelp(sender);
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (hasPermission(sender, "reload")) {
                    configHandler.loadConfig();
                    sender.sendMessage("§2Configuration reloaded!");
                }
            } else if (args[0].equalsIgnoreCase("save")) {
                if (hasPermission(sender, "admin")) {
                    sender.sendMessage("§2Saving users...");
                    for (ItemfilterPlayer player : players) {
                        handler.saveUser(player);
                    }
                    sender.sendMessage("§2Users saved sucessfully!");
                }
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Sorry, this command can only be used by Players!");
                    return true;
                }
                Player pl = (Player) sender;
                String name = pl.getName().toLowerCase().replace("'", "");
                if (hasPermission(sender, "use")) {

                    ItemfilterPlayer itemplayer = null;
                    for (ItemfilterPlayer plr : players) {
                        if (plr.getUsername().equalsIgnoreCase(name)) {
                            itemplayer = plr;
                            break;
                        }
                    }

                    if (args[0].equalsIgnoreCase("add")) {
                        if (itemplayer.getItems().size() >= config.getMaxIds() && !pl.hasPermission("itemfilter.unlimited") && !pl.hasPermission("itemfilter.admin")) {
                            sender.sendMessage("§4Sorry you have reached the allowed number of items on the List.");
                        } else {
                            if (args.length < 2) {
                                sender.sendMessage("§4Correct usage: /itemfilter add <id>[:type]");
                            } else {
                                String[] arr = args[1].split(":");
                                if (arr.length > 1) {
                                    final int[] item = parseArguments(sender, arr[0].toUpperCase(), arr[1]);
                                    final int id = item[0], type = item[1];
                                    if (id > 0 && type > 0 && type <= 32767) {
                                        itemplayer.addItem(id, (short) type);
                                        sender.sendMessage("§2Item added to filter!");
                                    } else {
                                        sender.sendMessage("§4Invalid item!");
                                    }
                                } else {
                                    final int[] item = parseArguments(sender, args[1].toUpperCase(), null);
                                    final int id = item[0];
                                    if (id > 0) {
                                        itemplayer.addItem(id, (short) -1);
                                        sender.sendMessage("§2Item added to filter!");
                                    } else {
                                        sender.sendMessage("§4Invalid item!");
                                    }
                                }
                            }
                        }
                    }

                    if (args[0].equalsIgnoreCase("remove")) {
                        if (args.length < 2) {
                            sender.sendMessage("§4Correct usage: /itemfilter remove <id>[:type]");
                        } else {
                            String[] arr = args[1].split(":");
                            if (arr.length > 1) {
                                final int[] item = parseArguments(sender, arr[0].toUpperCase(), arr[1]);
                                final int id = item[0], type = item[1];
                                if (id > 0 && type > 0 && type <= 32767) {
                                    itemplayer.removeItem(id, (short) type);
                                    sender.sendMessage("§2Item removed from filter!");
                                } else {
                                    sender.sendMessage("§4Invalid item!");
                                }
                            } else {
                                final int[] item = parseArguments(sender, args[1].toUpperCase(), null);
                                final int id = item[0];
                                if (id > 0) {
                                    itemplayer.removeItem(id, (short) -1);
                                    sender.sendMessage("§2Item removed from filter!");
                                } else {
                                    sender.sendMessage("§4Invalid item!");
                                }
                            }
                        }
                    }

                    if (args[0].equalsIgnoreCase("status")) {
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) {
                                if (args[1].equalsIgnoreCase("on")) {
                                    itemplayer.setEnabled(true);
                                    sender.sendMessage("§2The itemfilter is now enabled!");
                                } else {
                                    itemplayer.setEnabled(false);
                                    sender.sendMessage("§2The itemfilter is now disabled!");
                                }
                            } else {
                                sender.sendMessage("§4Correct usage: /itemfilter status <on/off>");
                            }
                        } else {
                            sender.sendMessage("§4Correct usage: /itemfilter status <on/off>");
                        }
                    }

                    if (args[0].equalsIgnoreCase("mode")) {
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("whitelist") || args[1].equalsIgnoreCase("blacklist")) {
                                if (args[1].equalsIgnoreCase("whitelist")) {
                                    itemplayer.setBlacklist(false);
                                    sender.sendMessage("§2Mode set to whitelist");
                                } else {
                                    itemplayer.setBlacklist(true);
                                    sender.sendMessage("§2Mode set to blacklist");
                                }
                            } else {
                                sender.sendMessage("§4Correct usage: /itemfilter mode <whitelist/blacklist>");
                            }
                        } else {
                            sender.sendMessage("§4Correct usage: /itemfilter mode <whitelist/blacklist>");
                        }
                    }

                    if (args[0].equalsIgnoreCase("clear")) {
                        itemplayer.clearItems();
                        sender.sendMessage("§2Itemfilter cleared");
                    }

                    if (args[0].equalsIgnoreCase("show")) {
                        sender.sendMessage("§2Status:");
                        sender.sendMessage("Filter enabled: " + itemplayer.getEnabled());
                        sender.sendMessage("Blacklist mode: " + itemplayer.getBlacklist());
                        sender.sendMessage("Blocks in Filter: ");
                        String tmp = "";
                        for (MinimalItem it : itemplayer.getItems()) {
                            final Material mat = Material.getMaterial(it.getId());
                            final String id;
                            if (mat == null) {
                                id = it.getId() + "";
                            } else {
                                id = mat.name().toLowerCase();
                            }
                            if (it.getDamage() != -1) {
                                tmp += id + ":" + it.getDamage() + ", ";
                            } else {
                                tmp += id + ", ";
                            }
                        }
                        if (tmp.length() > 2) {
                            tmp = tmp.substring(0, tmp.length() - 2); //cut away the , at the end of the string
                        }
                        sender.sendMessage(tmp);
                    }

                }
            }
        }
        return true;
    }

    private void printHelp(CommandSender sender) {
        sender.sendMessage("§6Itemfilter commands:");
        sender.sendMessage("§6/itemfilter add <id>[:type]§f - Adds a new block to the filter.");
        sender.sendMessage("§6/itemfilter remove <id>[:type]§f - Removes a block from the filter.");
        sender.sendMessage("§6/itemfilter status <on/off>§f - Enables or disables the filter.");
        sender.sendMessage("§6/itemfilter mode <whitelist/blacklist>§f - Changes the filter to a white or a blacklist.");
        sender.sendMessage("§6/itemfilter clear§f - Removes all items from the filter.");
        sender.sendMessage("§6/itemfilter show§f - Displays all Blocks on the list, and additional information.");
        if (sender.hasPermission("itemfilter.admin")) {
            sender.sendMessage("§6/itemfilter reload§f - Reloads the plugin configuration.");
            sender.sendMessage("§6/itemfilter save§f - Saves all players to the database.");
        }
    }

    private boolean hasPermission(CommandSender sender, String perm) {
        if (sender.hasPermission("itemfilter." + perm)) {
            return true;
        } else {
            sender.sendMessage("§4Sorry, you don't have Permission to execute this command!");
            return false;
        }
    }

    private int parseInt(CommandSender sender, String str) {
        //returns -1 when it's no number
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private int[] parseArguments(CommandSender sender, String matid, String damage) {
        final Material mat = Material.getMaterial(matid);
        final int id, dmg;
        if (mat == null) {
            id = parseInt(sender, matid);
        } else {
            id = mat.getId();
        }
        if (damage != null) {
            dmg = parseInt(sender, damage);
        } else {
            dmg = -1;
        }
        return new int[]{id, dmg};
    }

}
