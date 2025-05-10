package game;

import engine.renderer.Renderer;
import game.entity.Character;
import game.entity.Warrior;

import java.io.File;

/**
 * Manages the game state, characters, and resources.
 */
public class GameManager {
  // Test characters
  private Character warrior;

  // Reference to the renderer
  private final Renderer renderer;

  public GameManager(Renderer renderer) {
    this.renderer = renderer;

    // Ensure resources directory exists
    createResourceDirectories();

    // Initialize test characters
    initializeCharacters();
  }

  private void createResourceDirectories() {
    // Create asset directories if they don't exist
    File resourcesDir = new File("src/main/resources");
    if (!resourcesDir.exists()) {
      resourcesDir.mkdir();
    }

    File assetsDir = new File("src/main/resources/assets");
    if (!assetsDir.exists()) {
      assetsDir.mkdir();
    }

    File charactersDir = new File("src/main/resources/assets/characters");
    if (!charactersDir.exists()) {
      charactersDir.mkdir();
    }

    // Create a placeholder image if it doesn't exist
    File placeholderFile = new File("src/main/resources/assets/characters/placeholder.png");
    if (!placeholderFile.exists()) {
      // We can't actually create an image file here, but we'd need to add one
      // manually
      System.out.println("Please add a placeholder.png file in: " + placeholderFile.getAbsolutePath());
    }

    // Character class specific placeholders
    String[] classes = { "warrior", "mage", "druid", "rogue", "paladin", "warlock", "monk", "shaman" };
    for (String className : classes) {
      File classFile = new File("src/main/resources/assets/characters/" + className + ".png");
      if (!classFile.exists()) {
        System.out
            .println("Please add a " + className + ".png file in: " + classFile.getParentFile().getAbsolutePath());
      }
    }
  }

  private void initializeCharacters() {
    // Create warrior test character
    warrior = new Warrior("Test Warrior");

    // Position the warrior at the center of the screen
    warrior.moveTo(9, 5); // Mid-screen in a 18x10 grid

    // Initialize the sprite
    warrior.initializeSprite();

    // Add the sprite to the renderer
    renderer.add(warrior.getSprite());
  }

  public void update(double deltaTime) {
    // Update character state
    warrior.update(deltaTime);
  }
}