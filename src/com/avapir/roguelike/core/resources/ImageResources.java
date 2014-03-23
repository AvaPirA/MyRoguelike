package com.avapir.roguelike.core.resources;

import java.io.File;

/**
 * User: Alpen Ditrix
 * Date: 14.03.14
 * Time: 13:18
 */
public class ImageResources {

    private static String[] s = {"..."}; //some list of images

    public static class Loader implements ResourceLoader {
        @Override
        public void checkFiles() {
            //TODO

            for (String path : s) {
                if (!new File(path).exists()) {
                    throw new RuntimeException("Error with file at " + path);
                }
            }
        }

        @Override
        public void loadResources() {

        }
    }


}