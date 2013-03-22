package eu.benni1000.itemfilter;

public class ConfigurationHandler {

    private Configuration config;
    private Itemfilter filter;

    public ConfigurationHandler(Configuration config,Itemfilter filter) {
        this.config = config;
        this.filter = filter;
    }

    public void loadConfig() {
        filter.reloadConfig();
        config.setMaxIds(filter.getConfig().getInt("maximumNumberOfIds"));
    }

}
