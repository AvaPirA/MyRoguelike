package com.avapir.roguelike.core;

import java.util.Arrays;

public class Mainn {

    public static final String  TITLE = "MyRoguelike";

    /**
     * Main method what creates and executes the game
     *
     * @param args console arguments
     */
    public static void main(final String[] args) {
        IGame game = Game.getInstance();
        game.init(TITLE + (args.length > 0 ? Arrays.toString(args) : ""));
        game.start();
        game.done();
    }

}
