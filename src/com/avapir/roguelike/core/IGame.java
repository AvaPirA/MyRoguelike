package com.avapir.roguelike.core;

/**
 * Any object, which implements a game app must implement this interface. It has only 3 steps which describes main
 * game-app loading steps.
 *
 * The main thing is that this interface is only a соглашение todo
 */
public interface IGame {

    /**
     * All the things happening before first "loading..." animation must be done in this method.
     */
    void start();

    /**
     * Here game will load some important resources.
     */
    void init();

    /**
     * All "loading" must be done at this point. E.g. here will be started game cycle or something.
     */
    void done();
}
