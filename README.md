# GdxGame
![build workflow](https://github.com/hdescottes/GdxGame/actions/workflows/build.yml/badge.svg)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/hdescottes/GdxGame)

## Description
<p>This project is based on https://github.com/patrickhoey/BludBourne using LibGDX library.<br>
It is a RPG 2D base game.</p>

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

#### Battle Screen
The battle system is a turn based fight. <br>
The screen displays the amount of damage taken and inflicted by the player. <br>
For now, you can do a basic attack and use objects to restore your health and magic. Later you will be able to choose between a various set of attacks. <br>

#### Option Screen
The option screen is divided into 3 buttons : <br>
- The music settings (music is ok / sound is not implemented yet) <br>
- The control settings (which will be added later) <br>
- A back input, so we can return to the previous screen <br>

The option screen is triggered on the "O" key.

#### Inventory
The inventory can be displayed through the chest icon on the status UI. <br>
You can drag and drop items and use consumables by double-clicking on them.

#### Level Up
When you level up (battle, quest), you will be able to display some more extra points to your base stats. <br>

#### Profiles
The game is saved automatically when a specific action is down (change map, get quest, ...). <br>
You can chose to start a new game or load an existing profile on the menu screen.

-------
## Demo
<p align="center">
  <img src="desktop/src/main/resources/demo/demo.gif" width="396" height="315">
</p>
<p align="center">gameplay demo</p>
<p align="center">
  <img src="desktop/src/main/resources/demo/battle_demo.gif" width="396" height="315">
</p>
<p align="center">battle demo</p>

-------
## Game idea
- [ ] possibility to custom controls / default control schema

- [ ] add sounds & sounds settings

- [x] leveling system
  - [ ] class tree

- [x] battle feature
    - [ ] entities' animation while attacking
    - [ ] implement speed stat

- [ ] crafting feature

- [ ] cutscenes feature

- [ ] improve AI

-------
## Bugs

- FadeIn effect not working when transition [menu &#8594; game]
- Drag and drop selection issue on item's render (items are far from mouse when picking them)
- Sometimes current hp/mp on status HUD add a blank space before the max player's hp/mp

&#8855; _Reset position when exiting the game is not properly set when you close the game with the X window button (it is fine when exiting with the ESCAPE key)_

-------
## To improve

- More tests (find a way to initialize GL20, some tests don't work anymore with 1.9.13)
- More transition effect
- Z sorting
- Improve collision with Box2D
- Animations on map (opening doors, water, ...)

- Change characters/items/UI sprites to fit seiken densetsu's
