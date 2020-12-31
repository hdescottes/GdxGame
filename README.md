# GdxGame

## Description

<p>This project is based on https://carelesslabs.wordpress.com/2017/07/30/making-a-libgdx-roguelike-survival-game-part-1-gamedev/
using LibGDX library.<br>
It is a roguelike RPG 2D base game.</p>

#### Map
<p>The map is actually a random quadrilateral island area with random trees.</p>

#### Entities
- The hero is an animated 2D sprite. <br>
The hero move inside the island with the arrow keys or with "WASD". <br>
The hero can interact with the trees with the "E" key. For now, it will just remove the tree.
- The bird is an animated sprite. It has a random behaviour and stay inside the island (see https://github.com/libgdx/gdx-ai/wiki/State-Machine to improve the AI).

#### Battle Screen
Currently, the battle screen only hold the background and the hero sprite. <br>
It has a timer of 3s before returning to the main game screen. <br>
Soon, it will hold the enemy sprite and we will be able to properly "battle" in a turn-based battle.

#### Option Screen
The option screen is divided into 3 buttons : <br>
- The music settings (music is ok / sound is not implemented yet) <br>
- The control settings (which will be add later) <br>
- A back input, so we can return to the previous screen <br>

The option screen is triggered on the "O" key.

-------
<p align="center">
    <img src="desktop/src/main/resources/demo/demo.gif" width="396" height="315">
</p>
