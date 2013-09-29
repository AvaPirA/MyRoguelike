package com.avapir.roguelike.core;

import java.awt.Graphics;
import java.util.Deque;
import java.util.LinkedList;

public class GamePanel implements IGamePanel {

	public class Log {

		public Deque<String> messagesQueue = new LinkedList<String>();
		
		public void write(String string) {
			messagesQueue.add(string);
			if(messagesQueue.size()>15) {
				messagesQueue.poll();
			}
		}

	}
	
	public GamePanel(GameWindow parentWindow, Map worldMap, int tilesX, int tilesY) {
		
	}

	@Override
	public void drawLog(Graphics g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawComponent(Graphics g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGUI(Graphics g) {
		// TODO Auto-generated method stub

	}

}
