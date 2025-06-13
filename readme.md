# MOG-ANTICHEAT
1.8.9 Spigot Minecraft Anticheat


**You can find this project also in: [Spigot](https://www.spigotmc.org/resources/mog-anticheat.125279/) [PaperMC](https://hangar.papermc.io/mogdeveloping/MOG-ANTICHEAT)

![Stars](https://img.shields.io/github/stars/MOG-Developing/MOG-ANTICHEAT?style=for-the-badge&color=brightgreen) ![Forks](https://img.shields.io/github/forks/MOG-Developing/MOG-ANTICHEAT?style=for-the-badge&color=blue) ![Issues](https://img.shields.io/github/issues/MOG-Developing/MOG-ANTICHEAT?style=for-the-badge&color=yellow) ![Pull Requests](https://img.shields.io/github/issues-pr/MOG-Developing/MOG-ANTICHEAT?style=for-the-badge&color=orange) ![License](https://img.shields.io/github/license/MOG-Developing/MOG-ANTICHEAT?style=for-the-badge&color=red)
--- 

## Simple Anti-Cheat for Spigot 1.8.9
A lightweight anti-cheat designed for Spigot 1.8.9 servers. Built to detect and block some of the most common hacks used in pvp environments and etc.

Checks:
- Killaura
- Reach
- Speed
- Spider
- Timer
- Scaffold
- Destroyer
- PacketSpammer
and other stuff!

- **Bugs may exist**.

### Installation
1. Download the `.jar` file and place it in your server’s `plugins/` folder.
2. Restart the server.
3. You're all set!


## MOG-ANTICHEAT Permissions
- ``mogac.admin`` Gives access to all MOG-AC commands.
- ``mogac.alerts`` Allows receiving alerts when players are detected cheating.
- ``mogac.bypass`` Allows to bypass all checks.

## MOG-ANTICHEAT COMMANDS
- ``/mogac help`` Shows the help message.
- ``/mogac checks`` Lists all of the checks.
- ``/mogac toggle <check>`` Toggles a check.
- ``/mogac alerts`` Toggles the violation alerts.
- ``/mogac reload`` Reloads the plugin.



## How to compile  (Tested on Windows)
- First you need to download Apache Maven and add it to path(enviroment variable). (Apache Maven used in testing **Apache Maven 3.9.9**)
- Then you should download JDK-8 by Oracle and put it in path(enviroment variable).
- After all of that download the sourcecode and navigate with terminal to the directory where the sourcecode is and then paste ``mvn clean package``.
