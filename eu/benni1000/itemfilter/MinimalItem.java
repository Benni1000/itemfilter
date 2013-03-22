package eu.benni1000.itemfilter;

public class MinimalItem {

    // minimal item class, should save some memory

    private final int id;
    private final short damage;

    public MinimalItem(int id,short damage) {
        this.id = id;
        this.damage = damage;
    }

    public int getId() {
        return id;
    }

    public short getDamage() {
        return damage;
    }

}
