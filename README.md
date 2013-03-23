#Itemfilter#

###What is itemfilter?###
Itemfilter is a plugin that allows your users to create an personalised list
of items that should either be collected or be removed when they try to 
pick-up an item. The users also can disable their itemfilter, and only use it
when they want to.

###Why should I use it?###
Itemfilter is pretty useful when mining, to only collect the materials you want
and not all that useless cobblestone. Also when you really don't want an 
specific item like sand when building your beach-house you can use itemfilter
to keep you inventory clean.

###Installation###
Installing the itemfilter plugin is pretty easy just, drag and drop the jarfile
in you plugins directory. Then you must install the needed dependencies:
download and install the sql-library by dragging it into your plugin folder and
you are done.

###Configuration###
There is only one thing that can be configured and that is the maximum number
of items that can be stored on the list to prevent memory leaks. Change the
number to whatever you want in the configuration-file and reload the plugin
using the /itemfilter reload command.

###Commands###
/itemfilter add <id>[:type] - Adds a new block to the filter.
/itemfilter remove <id>[:type] - Removes a block from the filter.
/itemfilter status <on/off> - Enables or disables the filter.
/itemfilter mode <whitelist/blacklist> - Changes the filter to a white or a blacklist.
/itemfilter clear - Removes all items from the filter.
/itemfilter show - Displays all Blocks on the list, and additional information.
/itemfilter reload - Reloads the plugin configuration.
/itemfilter save - Saves all players to the database.

###Permissions###
itemfilter.use
Allows using the plugin's basic functionality like adding blocks to the filter and using it.

itemfilter.unlimited
Allows exceeding the limit that is set in the configuration-file.

itemfilter.admin
Allows everything.

###Usage###
To use the itemfilter you have to add an item to the itemfilter using the
itemfilter add command: /itemfilter add cobblestone . Now that cobblestone
is in the filter you can enable the filter to stop collecting cobbelstones:
/itemfilter status on . Now you could change the filter-mode to whitelist to only collect cobblestone and no other items: /itemfilter mode whitelist .