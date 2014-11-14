package com.avapir.roguelike.core;

import com.avapir.roguelike.core.gui.GameWindow;

import javax.swing.*;
import java.util.Arrays;

public class Main {

    public static final String     TITLE    = "MyRoguelike";
    /**
     * Main window of the game. Here everything will be painted.
     */
    public static final GameWindow renderer = new GameWindow();

    /**
     * Main method what creates and executes the game
     *
     * @param args console arguments
     */
    public static void main(final String[] args) {
        System.out.println("Uses Toolkit: " + System.getProperty("awt.toolkit"));

        startRenderer(TITLE + (args.length > 0 ? Arrays.toString(args) : ""));

        GameStateManager.getInstance().start();
    }

    private static void startRenderer(String title) {
        renderer.setTitle(title);
        renderer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        renderer.setVisible(true);
    }

}
