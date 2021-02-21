# GdxGame

## Description

<p>This project is based on https://github.com/patrickhoey/BludBourne using LibGDX library.<br>
It is a roguelike RPG 2D base game.</p>

#### Map
<p>The maps are from the Sword of Mana game (GBA) and influenced by the seiken densetsu serie. <br>
The edition was made with Tiled.exe in order to build layers.
</p>

#### Entities
- The hero is an animated 2D sprite. <br>
The hero moves freely in the maps with the arrow keys or with "WASD". <br>
The hero will soon be able to interact with NPCs with the "E" key.
- NPCs are also animated 2D sprites. <br>
You can interact with them by clicking on them at a proper distance. Some have conversations and quests.

#### Battle Screen (disabled until battle system is done)
Currently, the battle screen only hold the background. <br>
It has a timer of 3s before returning to the main game screen. <br>
Soon, it will hold the player and enemy sprite, and we will be able to properly "battle" in a turn-based battle.

#### Option Screen
The option screen is divided into 3 buttons : <br>
- The music settings (music is ok / sound is not implemented yet) <br>
- The control settings (which will be added later) <br>
- A back input, so we can return to the previous screen <br>

The option screen is triggered on the "O" key.

#### Inventory
The inventory can be displayed through the chest icon on the status UI. <br>
You can drag and drop items and use consumables by double-clicking on them.

-------
<p align="center">
    <img src="desktop/src/main/resources/demo/demo.gif" width="396" height="315">
</p>
