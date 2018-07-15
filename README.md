## discord-slave

discord-slave is a Discord bot.

The bot currently has the following features:
* Guild specific role assigning through message reactions
    * Includes automatic role list updating when roles are added and removed

Persisted information is stored in a SQLite database.

### Commands
The commands currently use a non-configurable prefix "!"

### Role assigning

The assigned roles have to exist before adding to bot and be listed below the bot's own role on the server's role list.

##### Commands:
* !roles
    * makes the bot send a message containing a list of assignable roles and reactions for them
* !addrole :emoji: @Role
    * adds an assignable to the bot that the members can join by using reactions on role lists
    * on a successful add the bot will react to the command message with a ✅-emoji
* !removerole :emoji: / @role
    * removes a previously assignable role from the bot. Also updates all previous role lists and removes the bot's reactions for the role

##### Required permissions:

The bot should have its own role with at least the following permissions configured:
- [x] Manage roles
- [x]  Read Text Channels & See Voice Channels
- [x]  Send Messages
- [x]  Read Message History
- [x]  Add Reactions

Assigned roles should have the following permissions configured:
- [x] Allow anyone to @mention this role

### Notes

1. The role list command currently returns the role list message in Finnish. 
The bot was designed for a Finnish Discord server but internationalization support is planned.

### Building
```
$ cd discord-bot/
$ docker build -t discord-slave .
```

### Running
```
$ docker run \
    -v slave_data:/data \ 
    -e ENV=prod \
    -e DATABASE_FILE=/data/data.sqlite \
    -e BOT_TOKEN=<TOKEN> \
    --name discord-slave \
    discord-slave:latest
```

### Inviting

Visit the following link as a server admin to invite the bot to your server:
https://discordapp.com/oauth2/authorize?&client_id=YOUR_BOT_CLIENT_ID_HERE&scope=bot&permissions=0