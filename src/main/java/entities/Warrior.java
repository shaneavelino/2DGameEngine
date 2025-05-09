package entities;

/**
 * Warrior class:
 * A high-health, high-physical damage class that specializes in close combat.
 * */
public class Warrior extends Character {
    // Warrior-specific resource type (Rage)
    private double rageGenerationRate;
    private double rageDecayRate;

    // Special warrior stats
    private double armorPenetration;
    private double bleedChance;
    private double bleedDamageMultiplier;

    /**
     * Constructor for Warrior class.
     */
    public Warrior(String characterName) {
        super(characterName, CharacterClass.WARRIOR);

        // Initialize warrior-specific properties
        this.rageGenerationRate = 5;  // Rage generated per attack
        this.rageDecayRate = 1;       // Rage lost per second out of combat
        this.armorPenetration = 5;
        this.bleedChance = 0.15;
        this.bleedDamageMultiplier = 0.5;
    }

    @Override
    protected void initializeBaseStats() {
        // Warriors have high strength and stamina, moderate agility,
        // and lower intelligence and wisdom
        increaseStat(StatType.STRENGTH, 10);
        increaseStat(StatType.STAMINA, 9);
        increaseStat(StatType.AGILITY, 6);
        increaseStat(StatType.INTELLIGENCE, 3);
        increaseStat(StatType.WISDOM, 4);

        setResourceName("Rage");
    }

    @Override
    protected TalentTree createTalentTree() {
        TalentTree talentTree = new TalentTree();

        // Tier 1 Talents (Basic)
        talentTree.addTalent(new ImprovedStrength(), 1);
        talentTree.addTalent(new TacticalMastery(), 1);
        talentTree.addTalent(new ImprovedCharge(), 1);

        // Tier 2 Talents
        talentTree.addTalent(new AngerManagement(), 2);
        talentTree.addTalent(new DeepWounds(), 2);
        talentTree.addTalent(new ImprovedCleave(), 2);

        // Tier 3 Talents
        talentTree.addTalent(new BloodthirstyStrike(), 3);
        talentTree.addTalent(new DefensiveMastery(), 3);
        talentTree.addTalent(new ImprovedExecute(), 3);

        // Tier 4 Talents
        talentTree.addTalent(new MortalStrike(), 4);
        talentTree.addTalent(new SecondWind(), 4);
        talentTree.addTalent(new ShieldWall(), 4);

        // Tier 5 Talents (Ultimate)
        talentTree.addTalent(new BattleCry(), 5);
        talentTree.addTalent(new RavagingStorm(), 5);
        talentTree.addTalent(new LastStand(), 5);

        return talentTree;
    }

    @Override
    protected void decideNextAction() {
        // Simple AI for warrior automatic combat
        // In a real implementation, this would be much more sophisticated

        // Check if we're in combat
        if (getCombatState() == CombatState.DEAD || getCombatState() == CombatState.STUNNED) {
            return;  // Can't do anything if dead or stunned
        }

        // Find nearest enemy (this would be provided by the game engine)
        Character target = findNearestEnemy();

        if (target == null) {
            setCombatState(CombatState.IDLE);
            return;
        }

        // Check distance to target
        int distance = distanceTo(target);

        // If too far, move closer
        if (distance > 1) {
            moveTowardsTarget(target);
            setCombatState(CombatState.MOVING);
            return;
        }

        // Try to use ultimate ability if fully charged
        if (getUltimateChargePoints() >= 100) {
            if (useUltimateAbility(target)) {
                setCombatState(CombatState.CASTING);
                return;
            }
        }

        // Try to use regular abilities
        for (Ability ability : getRegularAbilities()) {
            if (!ability.isOnCooldown() && getResourcePoints() >= ability.getResourceCost()) {
                if (useAbility(ability, target)) {
                    setCombatState(CombatState.CASTING);
                    return;
                }
            }
        }

        // If no abilities available, perform basic attack
        basicAttack(target);
        setCombatState(CombatState.ATTACKING);
    }

    /**
     * Warrior-specific basic attack implementation.
     * Generates rage on hit and has chance to cause bleeding.
     */
    private void basicAttack(Character target) {
        // Calculate attack damage
        double damage = getAttackDamage();

        // Check for critical hit
        boolean isCritical = Math.random() < getCriticalStrikeChance();
        if (isCritical) {
            damage *= getCriticalDamageMultiplier();
        }

        // Apply damage to target
        target.takeDamage(damage, DamageType.PHYSICAL, this);

        // Generate rage from attacking
        generateRage(isCritical ? rageGenerationRate * 2 : rageGenerationRate);

        // Chance to apply bleed effect
        if (Math.random() < bleedChance) {
            applyBleedEffect(target);
        }

        // Gain ultimate charge
        gainUltimateCharge(5);
    }

    /**
     * Apply a warrior-specific bleed effect to the target.
     */
    private void applyBleedEffect(Character target) {
        double bleedDamage = getAttackDamage() * bleedDamageMultiplier;
        StatusEffect bleedEffect = new BleedStatusEffect("Warrior's Bleed",
                "Bleeding from warrior attack",
                5.0, true, bleedDamage);
        target.applyStatusEffect(bleedEffect);
    }

    /**
     * Find the nearest enemy character.
     * This would be implemented with the game's entity management system.
     */
    private Character findNearestEnemy() {
        // In a real implementation, this would query the game state
        // for the nearest enemy character
        // For example purposes, we'll return null
        return null;
    }

    /**
     * Move towards a target character.
     */
    private void moveTowardsTarget(Character target) {
        // In a real implementation, this would use pathfinding
        // to move towards the target on the hex grid
        // For example, we could use A* pathfinding

        // For now, we'll just simulate moving one hex towards the target
        int currentX = getPositionX();
        int currentY = getPositionY();
        int targetX = target.getPositionX();
        int targetY = target.getPositionY();

        // Determine direction (simplified)
        int dx = Integer.compare(targetX, currentX);
        int dy = Integer.compare(targetY, currentY);

        // Move one step (this is a simplification)
        moveTo(currentX + dx, currentY + dy);
    }

    /**
     * Generate rage for the warrior.
     */
    public void generateRage(double amount) {
        double currentRage = getResourcePoints();
        double maxRage = getMaxResourcePoints();
        double newRage = Math.min(maxRage, currentRage + amount);
        setResourcePoints(newRage);
    }

    /**
     * Update warrior-specific logic.
     */
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        // Additional warrior-specific updates
        // For example, rage decay when out of combat
        if (getCombatState() == CombatState.IDLE) {
            double currentRage = getResourcePoints();
            double newRage = Math.max(0, currentRage - (rageDecayRate * deltaTime));
            setResourcePoints(newRage);
        }
    }

    // Warrior-specific getters
    public double getArmorPenetration() {
        return armorPenetration;
    }

    public double getBleedChance() {
        return bleedChance;
    }

    public double getBleedDamageMultiplier() {
        return bleedDamageMultiplier;
    }

    // Warriors might redefine calculateDerivedStats() to include their specific stats
    @Override
    public void calculateDerivedStats() {
        super.calculateDerivedStats();

        // Warrior-specific stat calculations
        this.armorPenetration = 5 + (getStrengthPoints() * 0.2);
        this.bleedChance = 0.15 + (getStrengthPoints() * 0.005);
        this.bleedDamageMultiplier = 0.5 + (getStrengthPoints() * 0.01);
    }
}

/**
 * A status effect for warrior's bleed damage over time.
 */
class BleedStatusEffect extends StatusEffect {
    private double damagePerSecond;
    private double timeSinceLastTick;
    private static final double TICK_INTERVAL = 1.0; // Damage tick every 1 second

    public BleedStatusEffect(String name, String description, double duration, boolean isStackable, double damagePerSecond) {
        super(name, description, duration, isStackable);
        this.damagePerSecond = damagePerSecond;
        this.timeSinceLastTick = 0;
    }

    @Override
    public void applyEffect(Character target) {
        // Just apply the effect, damage will be done over time
    }

    @Override
    public void removeEffect(Character target) {
        // No cleanup needed when the effect ends
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        // Accumulate time since last damage tick
        timeSinceLastTick += deltaTime;

        // Apply damage every tick interval
        if (timeSinceLastTick >= TICK_INTERVAL) {
            // This would need access to the affected character
            // In a real implementation, we would store a reference to the target
            timeSinceLastTick -= TICK_INTERVAL;

            // Damage logic would go here
            // target.takeDamage(damagePerSecond, DamageType.PHYSICAL, null);
        }
    }

    @Override
    public void stack(StatusEffect additionalEffect) {
        if (additionalEffect instanceof BleedStatusEffect) {
            BleedStatusEffect bleedEffect = (BleedStatusEffect) additionalEffect;

            // Increase damage and refresh duration
            this.damagePerSecond = Math.max(this.damagePerSecond, bleedEffect.damagePerSecond);
            super.stack(additionalEffect);
        }
    }
}

/**
 * Example Warrior talents below
 */

// Tier 1 Talents

class ImprovedStrength extends Talent {
    public ImprovedStrength() {
        super("Improved Strength", "Increases Strength by 3 per rank.", 3);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Add 3 strength per rank
        character.increaseStat(StatType.STRENGTH, 3 * getCurrentRank());
    }
}

class TacticalMastery extends Talent {
    public TacticalMastery() {
        super("Tactical Mastery", "Increases critical strike chance by 2% per rank.", 3);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // This would require a special method to modify critical strike chance
        // In a real implementation, we'd store these bonuses and apply them
        // For now, this is just a placeholder
    }
}

class ImprovedCharge extends Talent {
    public ImprovedCharge() {
        super("Improved Charge", "Increases movement range by 1 per rank.", 2);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // This would require a special method to modify movement range
        // In a real implementation, we'd store these bonuses and apply them
    }
}

// Tier 2 Talents

class AngerManagement extends Talent {
    public AngerManagement() {
        super("Anger Management", "Reduces rage decay by 20% per rank when out of combat.", 2);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // This would require access to warrior-specific stats
        // In a real implementation, we'd cast to Warrior and modify rageDecayRate
    }
}

class DeepWounds extends Talent {
    public DeepWounds() {
        super("Deep Wounds", "Increases bleed damage by 15% per rank.", 3);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // This would require access to warrior-specific stats
        if (character instanceof Warrior) {
            Warrior warrior = (Warrior) character;
            // Apply modifier (simplified)
            // In reality, we'd track this as a separate multiplier
        }
    }
}

class ImprovedCleave extends Talent {
    public ImprovedCleave() {
        super("Improved Cleave", "Increases the damage of Cleave ability by 10% per rank.", 3);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // This would modify a specific ability's damage
        // Would require access to the ability instance
    }
}

// Tier 3-5 Talents (simplified for brevity)

class BloodthirstyStrike extends Talent {
    public BloodthirstyStrike() {
        super("Bloodthirsty Strike", "Unlocks the Bloodthirsty Strike ability, which deals damage and heals you for 50% of damage dealt.", 1);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would unlock a new ability when this talent is learned
    }
}

class DefensiveMastery extends Talent {
    public DefensiveMastery() {
        super("Defensive Mastery", "Increases armor by 10% per rank.", 3);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would modify armor
    }
}

class ImprovedExecute extends Talent {
    public ImprovedExecute() {
        super("Improved Execute", "Execute ability deals 15% more damage per rank.", 3);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would modify a specific ability
    }
}

class MortalStrike extends Talent {
    public MortalStrike() {
        super("Mortal Strike", "Unlocks the Mortal Strike ability, which deals heavy damage and reduces healing received by the target.", 1);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would unlock a new ability
    }
}

class SecondWind extends Talent {
    public SecondWind() {
        super("Second Wind", "When below 30% health, gain 10% of max health per rank.", 2);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would add a passive effect
    }
}

class ShieldWall extends Talent {
    public ShieldWall() {
        super("Shield Wall", "Unlocks the Shield Wall ability, reducing damage taken by 60% for 6 seconds.", 1);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would unlock a new ability
    }
}

class BattleCry extends Talent {
    public BattleCry() {
        super("Battle Cry", "Unlocks the Battle Cry ultimate ability, increasing all allies' damage by 20% for 8 seconds.", 1);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would unlock an ultimate ability
    }
}

class RavagingStorm extends Talent {
    public RavagingStorm() {
        super("Ravaging Storm", "Unlocks the Ravaging Storm ultimate ability, dealing damage to all enemies in a 3 hex radius.", 1);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would unlock an ultimate ability
    }
}

class LastStand extends Talent {
    public LastStand() {
        super("Last Stand", "Unlocks the Last Stand ultimate ability, becoming invulnerable for 5 seconds but unable to attack.", 1);
    }

    @Override
    public void applyStatModifiers(Character character) {
        // Would unlock an ultimate ability
    }
}