package uk.daviesl.rpg.util.inventory;

import uk.daviesl.rpg.gamestate.states.GameState;

public class Modifier {
    private final GameState game;
    private final int index;
    private final int value;
    private boolean applied = false;

    public Modifier(GameState game, int index, int value) {
        this.game = game;
        this.index = index;
        this.value = value;
    }

    public void applyModifier() {
        int modified = game.getCurrentGameData().getIntSaveData(index) + value;
        game.getCurrentGameData().setIntSaveData(index, modified);
    }

    public void removeModifier() {
        int modified = game.getCurrentGameData().getIntSaveData(index) - value;
        game.getCurrentGameData().setIntSaveData(index, modified);
    }
}
