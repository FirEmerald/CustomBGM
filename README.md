[![Discord](https://img.shields.io/discord/176190900945289237?style=flat-square&logo=discord&logoColor=ffffff&label=Discord)](https://discord.gg/ykHRhmC)  
[![Curseforge](https://cf.way2muchnoise.eu/short_590902_downloads.svg?badge_style=flat)](https://www.curseforge.com/minecraft/mc-mods/custombgm)  

# CustomBGM
Custom Background Music (and sound loops) for Minecraft  

Provides a framework for [custom background music](https://github.com/FirEmerald/CustomBGM/wiki/Background-Music-Providers) to be used by both mods and map makers.  
Also adds support for [custom loop points in sounds](https://github.com/FirEmerald/CustomBGM/wiki/Adding-Loop-Points-to-Sounds).  

The BGM block can be used to set the BGM for a specific bounding area  
The Boss Spawner block is a helper block that can be used to spawn an enemy and play a specific BGM while the entity is alive.  
 - the entity will be despawned and the block reset if a player leaves the activation area  
 - the block is only active while powered by redstone  
 - the block will output a comparator strength of 7 when the boss is spawned and 15 when it is killed  
 - turning the redstone signal off resets the block so it can be used again  
The Entity Tester block is a helper block to test for entities inside a bounding area  
