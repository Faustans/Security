package com.security;

public enum CroupierState {
    /**
     * Waiting for players.
     */
    WAITING_FOR_CLIENTS ("CWait"),

    /**
     * Player secure authentication through citizen's card.
     */
    AUTHENTICATION ("Auth"),

    /**
     * Request the player whether he wants to create or join a table.
     */
    CREATE_OR_JOIN_TABLE ("Ccjt"),

    /**
     * Create a unique table and add user to it.
     */
    CREATING_TABLE ("Create"),

    /**
     * Join a table requested by the player.
     */
    JOIN_TABLE ("Join"),

    /**
     * Connecting client to the reserved table.
     */
    CONNECTING_CLIENT_TO_TABLE ("Connecting"),

    /**
     * End the game.
     */
    END_GAME ("end");

    private final String description;

    private CroupierState(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return this.description;
    }
}