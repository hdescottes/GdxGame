# GdxGame
![build workflow](https://github.com/hdescottes/GdxGame/actions/workflows/build.yml/badge.svg)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/hdescottes/GdxGame)

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

#### Profiles
The game is saved automatically when a specific action is down (change map, get quest, ...). <br>
You can chose to start a new game or load an existing profile on the menu screen.

-------
## Demo
<p align="center">
    <img src="desktop/src/main/resources/demo/demo.gif" width="396" height="315">
</p>

-------
## Game idea
- [ ] possibility to custom controls / default control schema
- [ ] add sounds & sounds settings

- [ ] leveling system (includes stats)

- [ ] character class system

- [ ] battle feature
    - [ ] setup battle screen when colision with enemy entity
    - [ ] entities' entries animation before battle begin
    - [ ] battle system
    - [ ] remove entity when battle is done

- [ ] crafting feature

- [ ] cutscenes feature

- [ ] game over screen

- [ ] improve AI

-------
## Bugs

- FadeIn effect not working when transition [menu &#8594; game]
- Drag and drop selection issue on item's render (items are far from mouse when picking them)
- Cant open quest ui or inventory ui when clicking on quest ui once (reset with option screen) &#8680; setInputUI (PlayerHUD) <br>
  &ensp;&ensp;&ensp; &#8594; try to trigger those ui by key input instead of click event
- Blur effect on menu screen when option is triggered ?

-------
## To improve

- More tests (find a way to initialize GL20, some tests don't work anymore with 1.9.13)
- More transition effect
- Z sorting
- Improve collision with Box2D
- Don't show outer map / camera not on player if too close to map's border (clamp)
- Animations on map (opening doors, water, ...)

- Change characters/items/UI sprites to fit seiken densetsu's
