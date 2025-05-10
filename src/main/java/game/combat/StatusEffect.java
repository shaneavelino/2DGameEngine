package game.combat;

import game.entity.Character;

/**
 * Abstract class representing a status effect that can be applied to a
 * character.
 */
public abstract class StatusEffect {
  private final String name;
  private final String description;
  private final double duration;
  private double remainingDuration;
  private final boolean isStackable;

  public StatusEffect(String name, String description, double duration, boolean isStackable) {
    this.name = name;
    this.description = description;
    this.duration = duration;
    this.remainingDuration = duration;
    this.isStackable = isStackable;
  }

  /**
   * Apply the effect to the target character.
   * 
   * @param target The character to apply the effect to
   */
  public abstract void applyEffect(Character target);

  /**
   * Remove the effect from the target character.
   * 
   * @param target The character to remove the effect from
   */
  public abstract void removeEffect(Character target);

  /**
   * Update the status effect's duration.
   * 
   * @param deltaTime Time since last update in seconds
   */
  public void update(double deltaTime) {
    remainingDuration -= deltaTime;
  }

  /**
   * Check if the status effect has expired.
   * 
   * @return true if the effect has expired, false otherwise
   */
  public boolean isExpired() {
    return remainingDuration <= 0;
  }

  /**
   * Refresh the duration of this effect with a new effect.
   * 
   * @param newEffect The new effect to use for refreshing
   */
  public void refresh(StatusEffect newEffect) {
    this.remainingDuration = newEffect.duration;
  }

  /**
   * Stack an additional effect onto this one.
   * Default behavior just takes the longer duration.
   * 
   * @param additionalEffect The additional effect to stack
   */
  public void stack(StatusEffect additionalEffect) {
    this.remainingDuration = Math.max(this.remainingDuration, additionalEffect.duration);
  }

  /**
   * Check if this effect can be stacked.
   * 
   * @return true if the effect is stackable, false otherwise
   */
  public boolean isStackable() {
    return isStackable;
  }

  // Getters
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public double getRemainingDuration() {
    return remainingDuration;
  }
}