package com.avapir.roguelike.core;

import java.io.Serializable;
import java.util.LinkedList;

public class Log implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * How much records will remain after end of turn
     */
    private static int REMAIN_RECORDS = 15;

    /**
     * {@link java.util.Formatter}-string for logging in {@link #g(String)}
     */
    private static final String FMT_GAME = "[%d:%d] %s";

    /**
     * {@link java.util.Formatter}-string for logging in {@link #g(String)}
     */
    private static final String FMT_GAME_FORMATTED = "[%d:%d] ";

    /**
     * Game instance for logging
     */
    private static transient Game game;

    /**
     * How many records were made ​​from the beginning of turn
     */
    private static transient int perTurn;

    /**
     * Storage of records
     */
    private static LinkedList<String> log;

    /**
     * @return is logger already connected to some instance of {@link com.avapir.roguelike.core.Game}
     */
    private static boolean isConnected() {
        return game != null;
    }

    /**
     * Connects logger to instance of {@link com.avapir.roguelike.core.Game}.
     *
     * @param game instance
     *
     * @throws java.lang.IllegalStateException if logger already connected
     */
    public static void connect(Game game) {
        if (isConnected()) {
            throw new IllegalStateException("Logger already connected to another game instance");
        }
        Log.game = game;
        log = new LinkedList<>();
    }

    /**
     * Disconnects logger from {@link com.avapir.roguelike.core.Game} instance.
     *
     * @return {@coed true} if succeed. {@code false} if logger was not connected yet
     */
    public static boolean disconnect() {
        if (isConnected()) {
            game = null;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sends default record for game
     *
     * @param s user data
     */
    public static void g(final String s) {
        checkConnectivity();
        log.add(String.format(FMT_GAME, perTurn, getTurnAndCheck(), s));
        removePreviousTurnRecord();
    }

    /**
     * throws IllegalStateException if logger is not connected
     */
    private static void checkConnectivity() {
        if (!isConnected()) {
            throw new IllegalStateException("Logger must be connected before making records");
        }
    }


    /**
     * Sends default record for game
     *
     * @param s formatting data string
     * @param s params data to insert
     */
    public static void g(final String s, final Object... params) {
        g(String.format(s, params));
    }

    /**
     * Increments amount of added in that turn records and removes records from another turns if log is full
     */
    private static void removePreviousTurnRecord() {
        perTurn++;
        if (log.size() > REMAIN_RECORDS && perTurn <= REMAIN_RECORDS) {
            log.poll();
        }
    }

    /**
     * Stores number of turn of last proceed record
     */
    private static int turn = -1;

    /**
     * Retrieves number of current game turn and invokes {@link #refresh()} if it's not equal to {@link Log#turn}
     *
     * @return {@link com.avapir.roguelike.core.Game#getTurnCounter()}
     */
    private static int getTurnAndCheck() {
        int currentTurn = game.getTurnCounter();
        if (turn != currentTurn) {
            refresh();
            turn = currentTurn;
        }
        return currentTurn;
    }

    /**
     * Removes extra records after end of turn.
     */
    private static void refresh() {
        perTurn = 0;
        int extra = log.size() - REMAIN_RECORDS;
        for (int i = 0; i < extra; i++) {
            log.poll();
        }
    }

    /**
     * Sets new value to {@link Log#REMAIN_RECORDS}, which must be more than zero
     *
     * @param count new value
     */
    public static void resetRemainRecordsAmount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Amount of records must be greater than zero");
        }
        REMAIN_RECORDS = count;
    }

    /**
     * @return the number of elements in the log
     */
    public static int getSize() {
        return log.size();
    }

    /**
     * @param i index of element to return
     * @return the element at the specified position
     */
    public static String get(int i) {
        return log.get(i);
    }
}
