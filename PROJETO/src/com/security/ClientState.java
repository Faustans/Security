package com.security;

public enum ClientState {
    /**
     * Authentication with citizen's card.
     */
    AUTHENTICATION ("Auth"),
    /**
     * Answer the server whether player wants to create or join a table.
     */
    CREATE_OR_JOIN_TABLE ("Ccjt"),
    /**
     * Creating a table with a set number of players.
     */
    CREATING_TABLE ("Create"),
    /**
     * Joining an already existent table.
     */
    JOINING_TABLE ("Join"),
    /**
     * Waiting for players.
     */
    WAITING_FOR_PLAYERS ("Wait"),

    /**
     * Announcing game is starting.
     */
    GAME_STARTING ("Starting"),

    /**
     * Waiting for bets from players.
     */
    BETTING ("Bet"),

    /**
     * Shuffling the deck.
     */
    SHUFFLING ("Shuffle"),

    /**
     * Distributing the cards through all players.
     */
    CARD_DISTRIBUTION ("Dist"),

    /**
     * Play.
     */
    PLAY ("Play"),

    /**
     * Pay to the winner.
     */
    MONEY_ACCOUNTING ("Pay"),

    /**
     * End the game.
     */
    END_GAME ("end");

    private final String description;

    private ClientState(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return this.description;
    }
}