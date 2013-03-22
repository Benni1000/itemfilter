package eu.benni1000.itemfilter;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Itemfilter extends JavaPlugin {

    private final static Logger log = Bukkit.getLogger();
    private SaveHandler handler;
    private ConfigurationHandler configHandler;
    private Configuration config = new Configuration();
    private ArrayList<ItemfilterPlayer> players = new ArrayList<ItemfilterPlayer>();

    @Override
    public void onEnable() {
        Bukkit.getServer().getLogger().setFilter(new ConsoleMonitor());
        this.saveDefaultConfig();
        configHandler = new ConfigurationHandler(config,this);
        configHandler.loadConfig();
        handler = new SaveHandler(log);
        if(Bukkit.getServer().getOnlinePlayers().length > 0) {
            //Server got reloaded
            this.reload();
        }
        //setup listeners
        getCommand("itemfilter").setExecutor(new Commands(players,handler,configHandler,config));
        Bukkit.getPluginManager().registerEvents(new Listeners(players, handler, log), this);
        //if the server crashed the users should be save, this saves all users in an 5 minute intervall
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for(ItemfilterPlayer pl : players) {
                    if(pl.hasChanged()) {
                        handler.saveUser(pl);
                    }
                }
            }
        },6000,6000);
        log.info("Itemfilter by Benni1000 enabled!");
    }

    @Override
    public void onDisable() {
        log.info("Saving players...");
        for(ItemfilterPlayer pl : players) {
            if(pl.hasChanged()) {
                handler.saveUser(pl);
            }
        }
        log.info("Itemfilter by Benni1000 disabled!");
    }

    private void reload() {
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            String name = p.getName().toLowerCase().replace("'","");
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
    }

}
