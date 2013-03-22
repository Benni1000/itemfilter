package eu.benni1000.itemfilter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.SQLite;

public class SaveHandler {

    /*
    Note: Sql-Injection is impossible, the input is filtered in other parts of the plugin.
    */

    private SQLite sql;

    public SaveHandler(Logger log) {
        sql = new SQLite(log,"itemfilter","data","plugins/itemfilter");
        sql.open();
        if(!sql.checkTable("player")) {
            sql.query("CREATE TABLE IF NOT EXISTS `players` (`id` INTEGER PRIMARY KEY AUTOINCREMENT,`username` varchar(100),`blacklist` tinyint(1),`enabled` tinyint(1))");
        }
        if(!sql.checkTable("items")) {
            sql.query("CREATE TABLE `items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT,`player_id` INTEGER,`item_id` INTEGER,`item_damage` INTEGER)");
        }
        sql.close();
    }

    private boolean parseBoolean(int number) {
        if(number == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void saveUser(ItemfilterPlayer pl) {
        try {
            sql.open();
            String username = pl.getUsername().replace("'","");
            int blacklist = (pl.getBlacklist()) ? 1 : 0;
            int enabled = (pl.getEnabled()) ? 1 : 0;
            //clean table
            sql.query("DELETE FROM items WHERE player_id='"+pl.getDatabaseId()+"'");
            //update
            sql.query("UPDATE players SET blacklist="+blacklist+",enabled="+enabled+" WHERE username='"+username+"'");
            for(MinimalItem item : pl.getItems()) {
                sql.query("INSERT INTO items (player_id,item_id,item_damage) VALUES ("+pl.getDatabaseId()+","+item.getId()+","+item.getDamage()+")");
            }
            sql.close();
        } catch (Exception ex) {
            sql.close();
            Logger.getLogger(SaveHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ItemfilterPlayer loadUser(String username) {
        sql.open();
        ResultSet set = sql.query("SELECT id,blacklist,enabled FROM players WHERE username='"+username+"'");
        try {
            if(set != null && set.next()) {
                int id = -1;
                boolean blacklist = false,enabled = false;
                ArrayList<MinimalItem> items = new ArrayList<MinimalItem>();
                id = set.getInt("id");
                blacklist = parseBoolean(set.getInt("blacklist"));
                enabled = parseBoolean(set.getInt("enabled"));
                set.close();
                set = sql.query("SELECT item_id,item_damage FROM items WHERE player_id="+id);
                if (set != null && set.next()) {
                    while(true) {
                        int itemId = set.getInt("item_id");
                        short itemDamage = (short) set.getInt("item_damage");
                        items.add(new MinimalItem(itemId,itemDamage));
                        if(!set.next()) {
                            break;
                        }
                    }
                }
                set.close();
                sql.close();
                return new ItemfilterPlayer(username,enabled,blacklist,items,id);
            } else {
                set.close();
                sql.close();
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SaveHandler.class.getName()).log(Level.SEVERE, null, ex);
            sql.close();
            return null;
        }
    }

    public ItemfilterPlayer createNewUser(String username) {
        sql.open();
        sql.query("INSERT INTO players (username,blacklist,enabled) VALUES ('"+username+"',1,0)");
        sql.close();
        return loadUser(username);
    }

}
