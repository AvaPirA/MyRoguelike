package com.avapir.roguelike.core.resources;

import com.avapir.roguelike.core.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Alpen Ditrix
 * Date: 14.03.14
 * Time: 13:18
 */
public class ImageResources {

    private static final Map<String, BufferedImage> images = new HashMap<>();

    static {
        final String[] imgKeys = {"background", "empty", "gameover", "grass",
//                "hero",
                "inventory_bg", "item_test_sword", "item_test_vest", "no_pic", "rip", "slime", "tree", "wFog"};
        for (String imgKey : imgKeys) {
            images.put(imgKey, loadImage(imgKey));
        }
    }

    public static BufferedImage getImage(final String key) {
        BufferedImage img = images.get(key);
        if (img != null) {
            return img;
        } else {
            Log.e("Image '" + key + "' not found. Trying to load...");
            img = loadImage(key);
            if (img != null) {
                Log.e("Image '" + key + "' successfully reloaded");
                images.put(key, img);
            } else {
                Log.e("Image '" + key + "' not found. Throwing error");
                throw new RuntimeException("Image '" + key + "' could not be load");
            }
        }
        return img;
    }

    public static String makeImageFilename(String key) {
        return "/sprite/".concat(key.endsWith(".png") ? key : key.concat("" + ".png"));
    }


    private static BufferedImage loadImage(final String key) {
        String filename = makeImageFilename(key);
        InputStream imageStream = ImageResources.class.getResourceAsStream(filename);
        try {
            byte[] imageBytes = new byte[imageStream.available()];
            if (imageStream.read(imageBytes) <= 0) {
                throw new RuntimeException("Zero bytes read at " + filename);
            }
            return createImageFromBytes(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BufferedImage createImageFromBytes(final byte[] imageData) {
        ByteArrayInputStream stream = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}