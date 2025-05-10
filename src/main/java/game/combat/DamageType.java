package game.combat;

/**
 * Enum representing the different types of damage that can be dealt.
 */
public enum DamageType {
  PHYSICAL, // Reduced by armor
  MAGICAL, // Reduced by magic resistance
  TRUE // Cannot be reduced by defenses
}