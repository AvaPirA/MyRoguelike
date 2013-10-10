package com.avapir.roguelike.core.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Hero;

//TODO 

public class NewLevelWindow extends JFrame {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public NewLevelWindow(Game game) {
		setTitle("Level Up!");
		setSize(AbstractGamePanel.SCREEN_WIDTH / 2, AbstractGamePanel.SCREEN_HEIGHT / 3);
		setResizable(false);
		getContentPane().add(new NewLevelPanel(game));
	}

	private static final class NewLevelPanel extends AbstractGamePanel {

		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		private final Game			game;

		NewLevelPanel(Game game) {
			this.game = game;
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.orange);
			for (int i = 0; i < Hero.PrimaryStats.PRIMARY_STATS_AMOUNT; i++) {
				drawString(g2, 40, 20 * (i + 1), Integer.toString(game.getHero().getStats().values(i)));
			}
		}

		@Override
		protected void paintGUI(Graphics2D g2) {
			// TODO Auto-generated method stub

		}
	}

}
