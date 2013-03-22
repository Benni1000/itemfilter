package eu.benni1000.itemfilter;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
    
    private ArrayList<ItemfilterPlayer> players;
    private SaveHandler handler;
    private Logger log;
    
    public Listeners(ArrayList<ItemfilterPlayer> players,SaveHandler handler,Logger log) {
        this.players = players;
        this.handler = handler;
        this.log = log;
    }
    
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent ev) {
        String name = ev.getPlayer().getName().toLowerCase().replace("'",""); //prevent sql injection
        ItemfilterPlayer pl = handler.loadUser(name);
        if(pl != null) {
            players.add(pl);
            log.info("Loaded user: "+name);
        } else {
            ItemfilterPlayer tmp = handler.createNewUser(name);
            if(tmp != null) {
                players.add(tmp);
                log.info("Created user: "+name);
            }
            else {
                log.info("Error: Could not create new user :(");
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent ev) {
        final int id = ev.getItem().getItemStack().getTypeId();
        for(ItemfilterPlayer pl : players) {
            if(pl.getUsername().equalsIgnoreCase(ev.getPlayer().getName().toLowerCase().replace("'",""))) {

                if(pl.getEnabled() && pl.getItems().size() > 0) {

                    /* Blacklist filter */
                    if(pl.getBlacklist()) {
                        for(MinimalItem i : pl.getItems()) {
                            if(i.getId() == id) {
                                short dur = ev.getItem().getItemStack().getDurability();
                                if(i.getDamage() == -1 && dur == 0) {
                                    ev.getItem().remove();
                                    ev.setCancelled(true);
                                } else {
                                    if(dur == i.getDamage()) {
                                        ev.getItem().remove();
                                        ev.setCancelled(true);
                                    }
                                }
                                break;
                            }
                        }
                    }

                    /* Whitelist filter */
                    else {
                        boolean remove = true;
                        for(MinimalItem i : pl.getItems()) {
                            if(i.getId() == id) {
                                short dur = ev.getItem().getItemStack().getDurability();
                                if(i.getDamage() == -1 && dur == 0) {
                                    remove = false;
                                } else {
                                    if(dur == i.getDamage()) {
                                        remove = false;
                                    }
                                }
                                break;
                            }
                        }
                        if(remove) {
                            ev.getItem().remove();
                            ev.setCancelled(true);
                        }
                    }

                }
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent ev) {
        savePlayer(ev.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent ev) {
        if(!ev.isCancelled()) {
            savePlayer(ev.getPlayer());
        }
    }

    public void savePlayer(Player pl) {
        for(ItemfilterPlayer p : players) {
            if(p.getUsername().equalsIgnoreCase(pl.getName())) {
                if(p.hasChanged()) {
                    handler.saveUser(p);
                    players.remove(p);
                }
                break;
            }
        }
    }
    
}
