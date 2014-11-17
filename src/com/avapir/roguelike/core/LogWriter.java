package com.avapir.roguelike.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 *
 */
public class LogWriter {

    private final BufferedWriter writer;

    public LogWriter(String title) {
        String path = String.format("%s.log", title.toLowerCase(Locale.UK));
        Path p = Paths.get(path);
        System.out.println(p);
        BufferedWriter w = null;
        try {
            w = Files.newBufferedWriter(p, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                                        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = w;
        format("\"%s\" log created at %s\n", title, ZonedDateTime.now().toLocalDateTime().toString());
    }

    public void format(String fmt, String... s) {
        write(String.format(fmt, s));
    }

    public void write(String s) {
        try {
            writer.write(s, 0, s.length());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
