[![Discord](https://img.shields.io/discord/176190900945289237?style=flat-square&logo=discord&logoColor=ffffff&label=Discord)](https://discord.gg/ykHRhmC)
# CustomBGM
Custom Background Music (and sound loops) for Minecraft

Provides a framework for custom background music to be used by both mods and map makers. Custom loop points, used in the BGM block, Boss Spawner, optionally configured custom title screen music, and any situatons where mods use the custom loops sound engine the mod provides, can be specified in your sounds.json using "loopStart" and "loopEnd" integers, such as "mp1.boss.flaaghra": {"sounds":[{"loopStart":387124,"loopEnd":3671832,"name":"mc4:mp1/boss/rui_flaaghra","stream":true}]}. the loop points are in samples.

The BGM block can be used to set the BGM for a specific bounding area
The Boss Spawner block is a helper block that can be used to spawn an enemy and play a specific BGM while the entity is alive.
 - the entity will be despawned and the block reset if a player leaves the activation area
 - the block is only active while powered by redstone
 - the block will output a comparator strength of 7 when the boss is spawned and 15 when it is killed
 - turning the redstone signal off resets the block so it can be used again
The Entity Tester block is a helper block to test for entities inside a bounding area
