package com.avapir.roguelike.core;

import java.util.Arrays;

public class RoguelikeMain {

    public static final boolean BORG = false;

    public static void main(final String[] args) {
        final IGame game = new Game("MyRoguelike" + (args.length > 0 ? Arrays.toString(args) : ""));
        game.init();
        game.start();
        game.done();
    }

    public static void unimplemented() {
        throw new RuntimeException("Unimplemented feature");
        // либо показывает диалогоовое окно. Лень запрогать
    }
}
