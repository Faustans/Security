package com.security;

public class Card {

    private final String[] suitNames = {"spades", "hearts", "clubs", "diamonds"};
    private final String[] valueNames = {"Unused", "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private int suit;
    private int value;

    public Card(int value, int suit) {
        if (value < 1 || value > 13) {
            throw new RuntimeException("Illegal card value attempted.  The " +
                    "acceptable range is 1 to 13.  You tried " + value);
        }
        if (suit < 0 || suit > 3) {
            throw new RuntimeException("Illegal suit attempted.  The  " +
                    "acceptable range is 0 to 3.  You tried " + suit);
        }
        this.suit = suit;
        this.value = value;
    }

    public String getSuitName(){
        return suitNames[this.suit];
    }
    public String getvalueName(){
        return valueNames[this.value];
    }

}
