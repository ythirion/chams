package org.lotr.kata;

import java.util.EnumMap;
import java.util.Map;

public class Characters {
    private Characters() {
        // Prevent instantiation
    }

    public static final String HOBBIT = "Hobbit";
    public static final String HUMAN = "Human";
    public static final String DWARF = "Dwarf";
    public static final String ELF = "Elf";
    public static final String WIZARD = "Wizard";

    static final Map<String, Map<QuestType, Double>> CHARACTER_BONUSES = Map.of(
            DWARF, Map.of(
                    QuestType.DESTROY_RING, 1.0,
                    QuestType.DEFEAT_ORCS, 1.4,
                    QuestType.ESCORT_HOBBIT, 1.0,
                    QuestType.FIND_ARTIFACT, 1.6,
                    QuestType.DIPLOMATIC_MISSION, 0.8
            ),
            HUMAN, Map.of(
                    QuestType.DESTROY_RING, 1.0,
                    QuestType.DEFEAT_ORCS, 1.3,
                    QuestType.ESCORT_HOBBIT, 1.2,
                    QuestType.FIND_ARTIFACT, 1.1,
                    QuestType.DIPLOMATIC_MISSION, 1.5
            ),
            HOBBIT, Map.of(
                    QuestType.DESTROY_RING, 1.5,
                    QuestType.DEFEAT_ORCS, 0.7,
                    QuestType.ESCORT_HOBBIT, 1.0,
                    QuestType.FIND_ARTIFACT, 1.0,
                    QuestType.DIPLOMATIC_MISSION, 1.1
            ),
            WIZARD, Map.of(
                    QuestType.DESTROY_RING, 1.3,
                    QuestType.DEFEAT_ORCS, 1.4,
                    QuestType.ESCORT_HOBBIT, 1.3,
                    QuestType.FIND_ARTIFACT, 1.6,
                    QuestType.DIPLOMATIC_MISSION, 1.7
            )
    );

    static final Map<QuestType, Integer> QUEST_BASE_REWARDS = new EnumMap<>(QuestType.class);
}
