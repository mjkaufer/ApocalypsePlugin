# ApocalypsePlugin

An apocalypse plugin for a Minecraft server.

By [mjkaufer](http://github.com/mjkaufer)


## Mechanics

The plugin automatically spawns zombies behind players every 45 second.

It also adds items to the chests on the map every 30 minutes.

Players choose a class as they start off, and simply try to survive in the server.


## Usage

`/apocalypse` - Plugin info

`/apocalypse class` - Pulls up GUI to pick a class. Only can be used one time. No permissions required.

`/apocalypse zombie` - Spawns zombies on all players in the world. Need permission `Apocalypse.*` This happens automatically every 45 seconds

`/apocalypse chest` - Refills chests in the world with random items. Need permission `Apocalypse.*` This happens automatically every 30 minutes


## Todo
* Add options to config