package com.avapir.roguelike.core.gui;

@SuppressWarnings("serial")
public class GameWindow extends javax.swing.JFrame {

    public GameWindow() {
        setSize(AbstractGamePanel.getScreenWidth(), AbstractGamePanel.getScreenHeight());
        setResizable(false);
        getContentPane().add(new GamePanel());
    }

}
