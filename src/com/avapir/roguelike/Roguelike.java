package com.avapir.roguelike;

import java.util.ArrayList;
import java.util.List;

import com.avapir.roguelike.core.Game;


public class Roguelike {

	public static final List<Integer> al = new ArrayList<Integer>(){
		{
			add(1);
			add(2);
			add(3);
		}
	};
	
	public static void main(String[] args) {
//		 Game game = new Game();
//		 game.init();
//		 game.run();
//		 game.done();
		List<Integer> l  = al;
		al.add(1);
		l.add(2);
		System.out.println(al);
		System.out.println(l);
	}
}