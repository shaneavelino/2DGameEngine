package game.entity;

import game.combat.Ability;
import game.combat.StatusEffect;
import game.combat.DamageType;
import engine.renderer.Sprite;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Abstract class representing a character in the RPG game.
 * Base class for all character types
 */
public abstract class Character {
  // Base character info
  private String characterName;
  private final CharacterClass characterClass;
  private int level;
  private int experiencePoints;
  private int talentPoints;

  // Resource naming
  private String resourceName;

  // Character resources and combat stats
  private double maxHealthPoints;
  private double healthPoints;
  private double maxResourcePoints;
  private double resourcePoints;
  private double healthRegen;
  private double resourceRegen;
  private double attackDamage;
  private double magicDamage;
  private double attackSpeed;
  private double castSpeed;
  private double criticalStrikeChance;
  private double criticalDamageMultiplier;
  private double armor;
  private double magicResistance;
  private double dodge;
  private double accuracy;

  // Character base stats that affect derived stats
  private int staminaPoints;
  private int intelligencePoints;
  private int agilityPoints;
  private int strengthPoints;
  private int wisdomPoints;

  // Position on the hex grid
  private int positionX;
  private int positionY;
  private int movementRange;

  // Character abilities
  private final List<Ability> regularAbilities; // 3 regular abilities
  private Ability ultimateAbility; // 1 ultimate ability

  // Talent tree and active talents
  private final TalentTree talentTree;
  private final Set<Talent> activeTalents;

  // Status effects currently applied to this character
  private final List<StatusEffect> statusEffects;

  // Combat state
  private boolean isAlive;
  private CombatState combatState;
  private int ultimateChargePoints;

  // Visual representation
  private Sprite sprite;
  private String spriteAssetPath;

  /** Constructor for a new character. */
  public Character(String characterName, CharacterClass characterClass) {
    this.characterName = characterName;
    this.characterClass = characterClass;
    this.level = 1;
    this.experiencePoints = 0;
    this.talentPoints = 0;

    // Default resource name - can be overridden by subclasses
    this.resourceName = "Resource";

    // Initialize default stats based on class
    initializeBaseStats();

    // Initialize derived stats based on base stats
    calculateDerivedStats();

    // Set up ability slots
    this.regularAbilities = new ArrayList<>(3);

    // Initialize other components
    this.talentTree = createTalentTree();
    this.activeTalents = new HashSet<>();
    this.statusEffects = new ArrayList<>();

    // Set initial state
    this.isAlive = true;
    this.combatState = CombatState.IDLE;
    this.ultimateChargePoints = 0;

    // Set resources to max
    this.healthPoints = this.maxHealthPoints;
    this.resourcePoints = this.maxResourcePoints;

    // Initialize sprite with a default path
    this.spriteAssetPath = getDefaultSpriteAsset();
  }

  /**
   * Initialize base stats according to character class.
   * Each derived class should implement this.
   */
  protected abstract void initializeBaseStats();

  /**
   * Create class-specific talent tree.
   * Each derived class should implement this.
   */
  protected abstract TalentTree createTalentTree();

  /**
   * Calculate derived stats based on base stats and talents.
   */
  public void calculateDerivedStats() {
    // Base calculations from stats
    this.maxHealthPoints = 100 + (staminaPoints * 10);
    this.maxResourcePoints = 100 + (intelligencePoints * 5) + (wisdomPoints * 5);
    this.healthRegen = staminaPoints * 0.5;
    this.resourceRegen = wisdomPoints * 0.5;
    this.attackDamage = 10 + (staminaPoints * 2);
    this.magicDamage = 10 + (intelligencePoints * 2);
    this.attackSpeed = 1.0 + (agilityPoints * 0.05);
    this.castSpeed = 1.0 + (intelligencePoints * 0.03) + (agilityPoints * 0.02);
    this.criticalStrikeChance = 0.05 + (agilityPoints * 0.01);
    this.criticalDamageMultiplier = 1.5 + (strengthPoints * 0.02);
    this.armor = strengthPoints + (staminaPoints * 0.5);
    this.magicResistance = intelligencePoints * 0.5 + wisdomPoints;
    this.dodge = agilityPoints * 0.01;
    this.accuracy = 0.9 + (agilityPoints * 0.005);
    this.movementRange = 3 + (int) (agilityPoints * 0.1); // Not sure about this yet

    applyTalentModifiers();
  }

  /** Apply stat modifiers from active talents. */
  private void applyTalentModifiers() {
    if (activeTalents == null) {
      return; // Skip if activeTalents is null
    }

    for (Talent talent : activeTalents) {
      talent.applyStatModifiers(this);
    }
  }

  /** Level up the character. */
  public void levelUp() {
    this.level++;
    this.talentPoints += 1;
    // Additional level-up logic (stat increases, etc.)
    calculateDerivedStats();
  }

  /**
   * Add experience points and check for level up.
   */
  public void gainExperience(int amount) {
    this.experiencePoints += amount;
    int requiredExp = calculateRequiredExperience();
    if (this.experiencePoints >= requiredExp) {
      this.experiencePoints -= requiredExp;
      levelUp();
    }
  }

  /** Calculate required experience for next level. */
  private int calculateRequiredExperience() {
    return 100 * level * level;
  }

  /** Increase a base stat by the specified amount */
  public void increaseStat(@NotNull StatType statType, int amount) {
    switch (statType) {
      case STAMINA:
        this.staminaPoints += amount;
        break;
      case INTELLIGENCE:
        this.intelligencePoints += amount;
        break;
      case AGILITY:
        this.agilityPoints += amount;
        break;
      case STRENGTH:
        this.strengthPoints += amount;
        break;
      case WISDOM:
        this.wisdomPoints += amount;
        break;
    }
    calculateDerivedStats();
  }

  /** Learn a new ability (maximum 3 regular abilities). */
  public boolean learnRegularAbility(Ability ability) {
    if (regularAbilities.size() < 3 && ability.isRegularAbility()) {
      regularAbilities.add(ability);
      return true;
    }
    return false;
  }

  /** Set the ultimate ability. */
  public void setUltimateAbility(@NotNull Ability ability) {
    if (!ability.isRegularAbility()) {
      this.ultimateAbility = ability;
    }
  }

  /** Unlock a talent in the talent tree. */
  public boolean unlockTalent(Talent talent) {
    if (talentPoints > 0 && talentTree.canUnlockTalent(talent)) {
      activeTalents.add(talent);
      talentPoints--;
      calculateDerivedStats();
      return true;
    }
    return false;
  }

  /** Update the character for the current game tick. */
  public void update(double deltaTime) {
    regenerateResources(deltaTime);
    updateStatusEffects(deltaTime);
    updateAbilityCooldowns(deltaTime);
    decideNextAction();

    // Update sprite position to match character position
    updateSpritePosition();
  }

  /** Regenerate health and resource points over time. */
  private void regenerateResources(double deltaTime) {
    healthPoints = Math.min(maxHealthPoints, healthPoints + (healthRegen * deltaTime));
    resourcePoints = Math.min(maxResourcePoints, resourcePoints + (resourceRegen * deltaTime));
  }

  /** Update all status effects and remove expired ones. */
  private void updateStatusEffects(double deltaTime) {
    Iterator<StatusEffect> iterator = statusEffects.iterator();
    while (iterator.hasNext()) {
      StatusEffect effect = iterator.next();
      effect.update(deltaTime);
      if (effect.isExpired()) {
        effect.removeEffect(this);
        iterator.remove();
      }
    }
  }

  /** Update cooldowns for all abilities. */
  private void updateAbilityCooldowns(double deltaTime) {
    for (Ability ability : regularAbilities) {
      ability.updateCooldown(deltaTime);
    }
    if (ultimateAbility != null) {
      ultimateAbility.updateCooldown(deltaTime);
    }
  }

  /** AI or automatic decision for next action. */
  protected abstract void decideNextAction();

  /** Take damage after applying armor and other defensive calculations. */
  public void takeDamage(double rawDamage, DamageType damageType, Character source) {
    double damageReduction = 0;

    // Calculate damage reduction based on damage type
    switch (damageType) {
      case PHYSICAL:
        damageReduction = calculatePhysicalDamageReduction();
        break;
      case MAGICAL:
        damageReduction = calculateMagicalDamageReduction();
        break;
      case TRUE:
        damageReduction = 0;
        break;
    }

    // Apply damage reduction
    double actualDamage = Math.max(0, rawDamage * (1 - damageReduction));

    // Apply damage to health
    healthPoints = Math.max(0, healthPoints - actualDamage);

    // Check if character died
    if (healthPoints <= 0) {
      die();
    }

    gainUltimateCharge(actualDamage * 0.5);
  }

  /** Calculate physical damage reduction from armor. */
  private double calculatePhysicalDamageReduction() {
    return armor / (armor + 100.0);
  }

  /** Calculate magical damage reduction from magic resistance. */
  private double calculateMagicalDamageReduction() {
    return magicResistance / (magicResistance + 100);
  }

  /** Use an ability if it's available. */
  public boolean useAbility(@NotNull Ability ability, Character target) {
    if (ability.isOnCooldown() || resourcePoints < ability.getResourceCost()) {
      return false;
    }

    // Use the ability
    ability.activate(this, target);

    // Consume resources
    resourcePoints -= ability.getResourceCost();

    // Start cooldown
    ability.startCooldown();

    // Gain ultimate charge from using abilities
    gainUltimateCharge(10);

    return true;
  }

  /** Try to use the ultimate ability. */
  public boolean useUltimateAbility(Character target) {
    if (ultimateAbility == null || ultimateChargePoints < 100) {
      return false;
    }

    // Use the ultimate ability
    ultimateAbility.activate(this, target);

    // Reset ultimate charge
    ultimateChargePoints = 0;

    return true;
  }

  /** Add ultimate charge points. */
  public void gainUltimateCharge(double amount) {
    this.ultimateChargePoints = Math.min(100, this.ultimateChargePoints + (int) amount);
  }

  /** Apply a status effect to this character. */
  public void applyStatusEffect(StatusEffect effect) {
    // Check if this type of effect already exists
    for (StatusEffect existingEffect : statusEffects) {
      if (existingEffect.getClass().equals(effect.getClass())) {
        // Refresh the duration if stackable or replace if not
        if (existingEffect.isStackable()) {
          existingEffect.stack(effect);
        } else {
          existingEffect.refresh(effect);
        }
        return;
      }
    }

    // Apply new effect
    statusEffects.add(effect);
    effect.applyEffect(this);
  }

  /** Move the character on the hex grid. */
  public void moveTo(int x, int y) {
    this.positionX = x;
    this.positionY = y;
  }

  /** Calculate distance to another character on hex grid. */
  public int distanceTo(Character other) {
    // Hex grid distance calculation
    int dx = Math.abs(this.positionX - other.positionX);
    int dy = Math.abs(this.positionY - other.positionY);
    return Math.max(dx, dy);
  }

  /** Handle character death. */
  private void die() {
    this.isAlive = false;
    this.combatState = CombatState.DEAD;
    // Additional death logic
  }

  /** Heal the character for the specified amount. */
  public void heal(double amount) {
    this.healthPoints = Math.min(maxHealthPoints, healthPoints + amount);
  }

  /** Restore resource points by the specified amount */
  public void restoreResource(double amount) {
    this.resourcePoints = Math.min(maxResourcePoints, resourcePoints + amount);
  }

  /**
   * Get the default sprite asset path for this character class.
   * Each derived class should override this to provide proper assets.
   */
  protected String getDefaultSpriteAsset() {
    // Default placeholder sprite for any character
    return "src/main/resources/assets/characters/placeholder.png";
  }

  /**
   * Initialize the sprite for rendering.
   * Called when the character is placed in a scene/world.
   */
  public void initializeSprite() {
    if (sprite == null) {
      // Create sprite at character's position (convert hex grid to screen coords)
      float screenX = positionX * 100; // 100 pixels per hex cell (just an example)
      float screenY = positionY * 100;
      sprite = new Sprite(spriteAssetPath, screenX, screenY, 64, 64); // Default size

      // Colorize based on class if necessary
      colorizeByClass();
    }
  }

  /**
   * Apply a color tint based on character class.
   */
  private void colorizeByClass() {
    if (sprite != null) {
      switch (characterClass) {
        case WARRIOR:
          sprite.setColor(0.8f, 0.2f, 0.2f, 1.0f); // Red tint
          break;
        case MAGE:
          sprite.setColor(0.2f, 0.2f, 0.8f, 1.0f); // Blue tint
          break;
        case DRUID:
          sprite.setColor(0.2f, 0.8f, 0.2f, 1.0f); // Green tint
          break;
        case PALADIN:
          sprite.setColor(0.8f, 0.8f, 0.2f, 1.0f); // Yellow tint
          break;
        case ROGUE:
          sprite.setColor(0.5f, 0.5f, 0.5f, 1.0f); // Gray tint
          break;
        case WARLOCK:
          sprite.setColor(0.5f, 0.2f, 0.5f, 1.0f); // Purple tint
          break;
        case MONK:
          sprite.setColor(0.8f, 0.4f, 0.0f, 1.0f); // Orange tint
          break;
        case SHAMAN:
          sprite.setColor(0.0f, 0.8f, 0.8f, 1.0f); // Cyan tint
          break;
      }
    }
  }

  /**
   * Update the sprite position to match the character's grid position.
   */
  public void updateSpritePosition() {
    if (sprite != null) {
      float screenX = positionX * 100; // 100 pixels per hex cell
      float screenY = positionY * 100;
      sprite.setX(screenX);
      sprite.setY(screenY);
    }
  }

  // Getters and setters
  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getCharacterName() {
    return characterName;
  }

  public void setCharacterName(String characterName) {
    this.characterName = characterName;
  }

  public CharacterClass getCharacterClass() {
    return characterClass;
  }

  public int getLevel() {
    return level;
  }

  public double getHealthPoints() {
    return healthPoints;
  }

  public double getMaxHealthPoints() {
    return maxHealthPoints;
  }

  public double getMaxResourcePoints() {
    return maxResourcePoints;
  }

  public double getResourcePoints() {
    return resourcePoints;
  }

  public void setResourcePoints(double amount) {
    this.resourcePoints = amount;
  }

  public double getAttackDamage() {
    return attackDamage;
  }

  public double getMagicDamage() {
    return magicDamage;
  }

  public double getAttackSpeed() {
    return attackSpeed;
  }

  public double getCastSpeed() {
    return castSpeed;
  }

  public double getCriticalStrikeChance() {
    return criticalStrikeChance;
  }

  public double getCriticalDamageMultiplier() {
    return criticalDamageMultiplier;
  }

  public int getStaminaPoints() {
    return staminaPoints;
  }

  public int getIntelligencePoints() {
    return intelligencePoints;
  }

  public int getAgilityPoints() {
    return agilityPoints;
  }

  public int getStrengthPoints() {
    return strengthPoints;
  }

  public int getWisdomPoints() {
    return wisdomPoints;
  }

  public boolean isAlive() {
    return isAlive;
  }

  public CombatState getCombatState() {
    return combatState;
  }

  public void setCombatState(CombatState combatState) {
    this.combatState = combatState;
  }

  public int getUltimateChargePoints() {
    return ultimateChargePoints;
  }

  public List<StatusEffect> getStatusEffects() {
    return statusEffects;
  }

  public int getPositionX() {
    return positionX;
  }

  public int getPositionY() {
    return positionY;
  }

  public int getMovementRange() {
    return movementRange;
  }

  public int getTalentPoints() {
    return talentPoints;
  }

  public Set<Talent> getActiveTalents() {
    return activeTalents;
  }

  public List<Ability> getRegularAbilities() {
    return regularAbilities;
  }

  public Ability getUltimateAbility() {
    return ultimateAbility;
  }

  public Sprite getSprite() {
    return sprite;
  }

  public void setSprite(Sprite sprite) {
    this.sprite = sprite;
  }

  public String getSpriteAssetPath() {
    return spriteAssetPath;
  }

  public void setSpriteAssetPath(String path) {
    this.spriteAssetPath = path;
    // If sprite already exists, update its texture
    if (sprite != null) {
      sprite.setTexture(path);
    }
  }
}