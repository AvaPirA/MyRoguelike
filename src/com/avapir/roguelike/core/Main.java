package com.avapir.roguelike.core;

import java.util.Arrays;

public class Main {

    public static final boolean BORG  = false;
    public static final String  TITLE = "MyRoguelike";

    /**
     * Main method what creates and executes the game
     *
     * @param args console arguments
     */
    public static void main(final String[] args) {
        final IGame game = new Game(TITLE + (args.length > 0 ? Arrays.toString(args) : ""));
        game.init();
        game.start();
        game.done();
    }

}
