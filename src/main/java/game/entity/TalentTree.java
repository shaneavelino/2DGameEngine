package game.entity;

import java.util.*;

/**
 * Represents a character's talent tree, organizing talents into tiers.
 */
public class TalentTree {
  private final Map<Integer, List<Talent>> tiers;
  private final Set<Talent> unlockedTalents;

  public TalentTree() {
    this.tiers = new HashMap<>();
    this.unlockedTalents = new HashSet<>();

    // Initialize tiers
    for (int i = 1; i <= 5; i++) {
      tiers.put(i, new ArrayList<>());
    }
  }

  /**
   * Add a talent to a specific tier of the talent tree.
   * 
   * @param talent The talent to add
   * @param tier   The tier to add the talent to (1-5)
   */
  public void addTalent(Talent talent, int tier) {
    tiers.get(tier).add(talent);
  }

  /**
   * Check if a talent can be unlocked based on prerequisites.
   * 
   * @param talent The talent to check
   * @return true if the talent can be unlocked, false otherwise
   */
  public boolean canUnlockTalent(Talent talent) {
    // Find the tier of this talent
    int talentTier = 0;
    for (Map.Entry<Integer, List<Talent>> entry : tiers.entrySet()) {
      if (entry.getValue().contains(talent)) {
        talentTier = entry.getKey();
        break;
      }
    }

    if (talentTier == 0) {
      return false; // Talent not found in the tree
    }

    // Tier 1 talents are always unlockable
    if (talentTier == 1) {
      return true;
    }

    // Check if prerequisites are met (must have at least one talent from previous
    // tier)
    int previousTier = talentTier - 1;
    List<Talent> previousTierTalents = tiers.get(previousTier);

    for (Talent previousTalent : previousTierTalents) {
      if (unlockedTalents.contains(previousTalent)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Mark a talent as unlocked.
   * 
   * @param talent The talent to unlock
   */
  public void unlockTalent(Talent talent) {
    unlockedTalents.add(talent);
  }

  /**
   * Get the set of all unlocked talents.
   * 
   * @return An unmodifiable set of unlocked talents
   */
  public Set<Talent> getUnlockedTalents() {
    return Collections.unmodifiableSet(unlockedTalents);
  }

  /**
   * Get the map of all tiers and their talents.
   * 
   * @return The tiers map
   */
  public Map<Integer, List<Talent>> getTiers() {
    return tiers;
  }
}