package com.avapir.roguelike.core;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.LinkedList;

public class Log implements Serializable {

    /**
     * for logging in {@link #g(String)}
     */
    private static final String FMT_GAME  = "[%d:%d] %s";
    private static final String FMT_ERROR = "E[%s] %s";
    private static final Log    instance  = new Log();

    /**
     * How much records will remain after end of turn
     */
    private static int                REMAIN_RECORDS = 15;
    /**
     * Storage of records
     */
    private final  LinkedList<String> loggedList     = new LinkedList<>();
    /**
     * How many records were made ​​from the beginning of turn
     */
    private transient int perTurn;
    /**
     * Stores number of turn of last send/received record
     */
    private int turn = -1;

    private LogWriter logWriter;

    private Log() {
        if (isWritingLogToFile()) {
            logWriter = new LogWriter("Game");
        }
    }

    public static Log getInstance() {
        return instance;
    }

    public static void e(final String s) {
        Log.getInstance().error(s);
    }

    public static void g(final String s) {
        Log.getInstance().game(s);
    }

    public static void g(final String s, final Object... params) {
        Log.getInstance().game(s, params);
    }

    public static boolean isWritingLogToFile() {
        return Boolean.parseBoolean(System.getProperty("writeLog"));
    }

    private void error(String s) {
        String formatted = String.format(FMT_ERROR, ZonedDateTime.now().toLocalTime(), s);
        _write(formatted);
        removePreviousTurnRecord();
    }

    /**
     * Sends default record for game
     *
     * @param s user data
     */
    public void game(final String s) {
        String formatted = String.format(FMT_GAME, perTurn, getTurnAndCheck(), s);
        _write(formatted);
        removePreviousTurnRecord();
    }

    private void _write(String s) {
        loggedList.add(s);
        System.out.println(s);
        if (logWriter != null) {
            logWriter.write(s);
        }
    }

    /**
     * Sends default record about game
     *
     * @param s formatting data string
     */
    public void game(final String s, final Object... params) {
        game(String.format(s, params));
    }

    /**
     * Increments amount of records which was added in the current turn and removes records from another turns if log
     * is full
     */
    private void removePreviousTurnRecord() {
        perTurn++;
        if (loggedList.size() > REMAIN_RECORDS || perTurn > REMAIN_RECORDS) {
            loggedList.poll();
        }
    }

    /**
     * Retrieves number of current game turn and invokes {@link #refresh()} if it's not equal to {@link Log#turn}
     *
     * @return {@link GameStateManager#getTurnCounter()}
     */
    private int getTurnAndCheck() {
        int currentTurn = GameStateManager.getInstance().getTurnCounter();
        if (turn != currentTurn) {
            refresh();
            turn = currentTurn;
        }
        return currentTurn;
    }

    /**
     * Removes extra records after end of turn.
     */
    private void refresh() {
        perTurn = 0;
        int extra = loggedList.size() - REMAIN_RECORDS;
        for (int i = 0; i < extra; i++) {
            loggedList.poll();
        }
    }

    /**
     * @return the number of elements in the log
     */
    public int getSize() {
        return loggedList.size();
    }

    /**
     * @param i index of element to return
     *
     * @return the element at the specified position
     */
    public String get(int i) {
        return loggedList.get(i);
    }

    public void close() {
        if (isWritingLogToFile()) {
            logWriter.close();
        }
    }
}
