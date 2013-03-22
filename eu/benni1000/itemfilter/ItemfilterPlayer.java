package eu.benni1000.itemfilter;

import java.util.ArrayList;

public class ItemfilterPlayer {

    private String username;
    //the changed boolean is a litte protection againts join bots, to prevent too many DB changes.
    private boolean enabled,blacklist,changed = false;
    private ArrayList<MinimalItem> items;
    private int databaseId;
    
    public ItemfilterPlayer(String username, boolean enabled, boolean blacklist, ArrayList<MinimalItem> items,int databaseId) {
        this.databaseId = databaseId;
        if(items == null) {
            this.items = new ArrayList<MinimalItem>();
        } else {
            this.items = items;
        }
        this.username = username;
        this.enabled = enabled;
        this.blacklist = blacklist;
    }

    public String getUsername() {
        return username;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public boolean getBlacklist() {
        return blacklist;
    }

    public ArrayList<MinimalItem> getItems() {
        return items;
    }

    public void addItem(int id, short damage) {
        boolean add = true;
        for(MinimalItem i : items) {
            if(i.getId() == id && i.getDamage() == damage) {
                add = false;
                break;
            }
        }
        if(add) {
            changed = true;
            this.items.add(new MinimalItem(id,damage));
        }
    }

    public void removeItem(int id, short damage) {
        for(MinimalItem i : items) {
            if(i.getId() == id && i.getDamage() == damage) {
                changed = true;
                items.remove(i);
                break;
            }
        }
    }

    public void clearItems() {
        changed = true;
        items = new ArrayList<MinimalItem>();
    }

    public void setEnabled(boolean status) {
        changed = true;
        enabled = status;
    }

    public void setBlacklist(boolean status) {
        changed = true;
        blacklist = status;
    }
    
    public int getDatabaseId() {
        return databaseId;
    }

    public boolean hasChanged() {
        return changed;
    }

}
