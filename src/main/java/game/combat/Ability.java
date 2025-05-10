package game.combat;

import game.entity.Character;

/**
 * Abstract class representing an ability.
 */
public abstract class Ability {
  private String name;
  private String description;
  private double cooldownDuration;
  private double remainingCooldown;
  private double resourceCost;
  private double activationTime;
  private boolean isUltimate;

  public Ability(String name, String description, double cooldownDuration,
      double resourceCost, double activationTime, boolean isUltimate) {
    this.name = name;
    this.description = description;
    this.cooldownDuration = cooldownDuration;
    this.remainingCooldown = 0;
    this.resourceCost = resourceCost;
    this.activationTime = activationTime;
    this.isUltimate = isUltimate;
  }

  public abstract void activate(Character source, Character target);

  public void updateCooldown(double deltaTime) {
    if (remainingCooldown > 0) {
      remainingCooldown = Math.max(0, remainingCooldown - deltaTime);
    }
  }

  public void startCooldown() {
    remainingCooldown = cooldownDuration;
  }

  public boolean isOnCooldown() {
    return remainingCooldown > 0;
  }

  public boolean isRegularAbility() {
    return !isUltimate;
  }

  // Getters
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public double getCooldownDuration() {
    return cooldownDuration;
  }

  public double getRemainingCooldown() {
    return remainingCooldown;
  }

  public double getResourceCost() {
    return resourceCost;
  }

  public double getActivationTime() {
    return activationTime;
  }
}