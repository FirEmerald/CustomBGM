[![Discord](https://img.shields.io/discord/176190900945289237?style=flat-square&logo=discord&logoColor=ffffff&label=Discord)](https://discord.gg/ykHRhmC)
# CustomBGM
Custom Background Music (and sound loops) for Minecraft

Provides a framework for custom background music to be used by both mods and map makers. Music is defined inside a "loops.xml" file inside the base assets folder (I.E. assets/minecraft/loops.xml), with the following format:

    <Loops>
        <[name]>
            <resource start=[loop start sample] end=[loop end sample]>[sound file location, as found inside the loops folder inside the base assets folder]</resource>
            [more resource definitions - a random one will be picked by the sound engine]
        </[name]>
    </Loops>

The BGM block can be used to set the BGM for a specific volume
The Boss Spawner block is a helper block that can be used to spawn an enemy and play a specific BGM while the entity is alive.
 - the entity will be despawned and the block reset if a player leaves the activation volume
 - the block is only active while powered by redstone
 - the block will output a comparator strength of 7 when the boss is spawned and 15 when it is killed
 - turning the redstone signal off resets the block so it can be used again
The Entity Tester block is a helper block to test for entities inside a volume
