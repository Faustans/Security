package com.security;

public enum GameState {
    /**
     * Waiting for players.
     */
    WAITING_FOR_PLAYERS ("Wait"),

    /**
     * Announcing game is starting.
     */
    PROVIDE_IDENTITY_TO_ALL ("Identity"),

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
    COMPLAIN_ABOUT_CHEATERS ("Cheataria"),

    /**
     * Pay to the winner.
     */
    MONEY_ACCOUNTING ("Pay"),

    /**
     * Pay to the winner.
     */
    ACCEPT_OUTCOME ("Accept"),

    /**
     * End the game.
     */
    END_GAME ("End");

    private final String description;

    private GameState(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return this.description;
    }
}