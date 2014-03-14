package com.avapir.roguelike.core;

/**
 * User: Alpen Ditrix
 * Date: 14.03.14
 * Time: 13:15
 */
public interface ResourceLoader {

    /**
     * Checks if data isn't corrupted or lost. May be some checks of MD5 or just file size or something
     * @throws java.lang.RuntimeException if there is some errors. There may be added some more detailed message.
     */
    public void checkFiles();

    /**
     * Loads resources to RAM
     */
    public void loadResources();

}
