package game.entity;

/**
 * Abstract class representing a talent or skill that can be unlocked by a
 * character.
 */
public abstract class Talent {
  private final String name;
  private final String description;
  private final int maxRank;
  private int currentRank;

  public Talent(String name, String description, int maxRank) {
    this.name = name;
    this.description = description;
    this.maxRank = maxRank;
    this.currentRank = 0;
  }

  /**
   * Apply stat modifiers to the character based on the talent.
   * 
   * @param character The character to apply modifiers to
   */
  public abstract void applyStatModifiers(Character character);

  /**
   * Increase the rank of this talent, if possible.
   * 
   * @return true if the rank was increased, false if it was already at max rank
   */
  public boolean increaseRank() {
    if (currentRank < maxRank) {
      currentRank++;
      return true;
    }
    return false;
  }

  // Getters
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getMaxRank() {
    return maxRank;
  }

  public int getCurrentRank() {
    return currentRank;
  }
}