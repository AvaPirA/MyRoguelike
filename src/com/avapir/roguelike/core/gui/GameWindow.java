package com.avapir.roguelike.core.gui;

import com.avapir.roguelike.core.Game;

@SuppressWarnings("serial")
public class GameWindow extends javax.swing.JFrame {

    public GameWindow(final Game game) {
        setSize(AbstractGamePanel.getScreenWidth(), AbstractGamePanel.getScreenHeight());
        setResizable(false);
        getContentPane().add(new GamePanel(game));
    }

}
