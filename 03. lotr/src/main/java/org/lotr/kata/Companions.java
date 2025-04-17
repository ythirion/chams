package org.lotr.kata;

import java.util.ArrayList;
import java.util.List;

import static org.lotr.kata.Characters.*;

public class Companions extends ArrayList<String> {
    private final CharacterServiceInterface characterService;

    private Companions(CharacterServiceInterface characterService, List<String> companions) {
        super(companions);
        this.characterService = characterService;
    }

    public static Companions from(CharacterServiceInterface characterService, String characterName, List<String> companions) {
        verifyCharacterAndCompanionsAreAvailable(characterService, characterName, companions);
        return new Companions(characterService, companions);
    }

    private static void verifyCharacterAndCompanionsAreAvailable(CharacterServiceInterface characterService, String characterName, List<String> companions) {
        if (!characterService.isCharacterAvailable(characterName)) {
            throw new IllegalStateException("Character " + characterName + " is not available for quests.");
        }

        var unavailableCompanions = unavailableCompanions(characterService, companions);
        if (!unavailableCompanions.isEmpty()) {
            throw new IllegalStateException("Companion " + unavailableCompanions.getFirst() + " is not available for quests.");
        }
    }

    private static List<String> unavailableCompanions(CharacterServiceInterface characterService, List<String> companions) {
        return companions.stream()
                .filter(companion -> !characterService.isCharacterAvailable(companion))
                .toList();
    }

    boolean containsElfAndDwarf() {
        return containsSpecialCombination(ELF, DWARF);
    }

    public boolean containsHobbitAndWizard() {
        return containsSpecialCombination(HOBBIT, WIZARD);
    }

    private boolean containsSpecialCombination(String type1, String type2) {
        return contains(type1) && contains(type2);
    }

    private boolean contains(String targetType) {
        return this.stream()
                .anyMatch(companion -> characterService.getCharacterType(companion).equals(targetType));
    }

    public double getBaseChance() {
        return size() * 0.05;
    }

    void completeQuest(boolean success) {
        forEach(companion -> characterService.completeQuest(companion, success));
    }
}
