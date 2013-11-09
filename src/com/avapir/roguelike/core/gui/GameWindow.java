package com.avapir.roguelike.core.gui;

import com.avapir.roguelike.core.Game;

/**
 * Главное окно
 *
 * @author Alpen
 */
@SuppressWarnings("serial")
public class GameWindow extends javax.swing.JFrame {

    public GameWindow(final String title, final Game game) {
        setTitle(title);
        setSize(AbstractGamePanel.getScreenWidth(), AbstractGamePanel.getScreenHeight());
        setResizable(false);
        getContentPane().add(new GamePanel(game));
    }

}
