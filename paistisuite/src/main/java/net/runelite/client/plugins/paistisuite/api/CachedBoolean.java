package net.runelite.client.plugins.paistisuite.api;

import lombok.Getter;

public abstract class CachedBoolean {

    private State state = State.NULL;

    enum State {
        TRUE(true),
        FALSE(false),
        NULL(false);

        @Getter
        private final boolean bool;

        State(boolean b) {
            bool = b;
        }

        public static State booleanToState(boolean b) {
            if (b) {
                return State.TRUE;
            }
            return State.FALSE;
        }
    }

    public abstract boolean checkState();

    public void resetState() {
        state = State.NULL;
    }

    public boolean getBoolean() {
        if (state == State.NULL) {
            state = State.booleanToState(checkState());
        }
        return state.bool;
    }
}
