package com.avapir.roguelike.core.gui;

import java.awt.Graphics2D;

import javax.swing.JFrame;

import com.avapir.roguelike.core.Game;

//TODO
public class InventoryWindow extends JFrame {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	

	public InventoryWindow(Game game) {
		getContentPane().add(new InventoryPanel(game));
	}

	private static final class InventoryPanel extends AbstractGamePanel {

		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		InventoryPanel(Game game) {
		}

		@Override
		protected void paintGUI(Graphics2D g2) {
			// TODO Auto-generated method stub

		}

	}

}
