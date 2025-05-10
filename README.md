# 2D RPG Strategy Game

A 2D RPG strategy game where players build teams of characters, customize their abilities and talents, and battle through dungeons.

## Architecture

### Core Packages

- `org.lavostudio.game.core`: Contains the core engine components
  - `Window`: Main window and game loop
  - `GameManager`: Manages game state and coordinates game systems
  - `MouseListener`: Handles mouse input

- `org.lavostudio.game.renderer`: Graphics rendering system
  - `Renderer`: Main rendering system
  - `Sprite`: Visual representation of game objects
  - `Texture`: Texture loading and management

- `org.lavostudio.game.entity`: Character-related classes
  - `Character`: Base abstract class for all characters
  - `CharacterClass`: Enum of available character classes
  - `StatType`: Enum of character statistics
  - `CombatState`: Enum of possible combat states
  - `Talent`: Abstract class for character talents
  - `TalentTree`: Manages a character's talent progression

- `org.lavostudio.game.combat`: Combat-related classes
  - `Ability`: Abstract class for character abilities
  - `StatusEffect`: Abstract class for effects that can be applied to characters
  - `DamageType`: Enum of damage types

### Game Loop

1. `Main` creates a `Window` instance and starts the game loop
2. The `Window` initializes the game systems and manages the main loop:
   - Updates game state based on delta time
   - Renders the current frame
   - Processes input events

3. The `GameManager` coordinates game systems and character updates

### Character System

Characters are built using an abstract base class with specialized subclasses for each character class (Warrior, Mage, etc.). Key features:

- Base and derived statistics
- Abilities and talents
- Visual representation through sprites
- Combat state management
- Status effect application and management

### Build and Run

```
./gradlew run
```

To generate placeholder character sprites:

```
./gradlew generatePlaceholders
```

## Development Plan

1. ✅ Core engine setup
2. ✅ Character class implementation
3. ✅ Basic rendering system
4. ⬜ Implement hex grid system
5. ⬜ Complete ability system
6. ⬜ Battle system implementation
7. ⬜ UI for character management
8. ⬜ Dungeon generation and progression 