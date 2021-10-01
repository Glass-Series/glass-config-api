package net.glasslauncher.mods.api.gcapi.api;

import net.minecraft.client.render.TextRenderer;

import java.awt.*;
import java.awt.datatransfer.*;

/**
 * Some utility methods copied over from r1.2.5 for use in ExtensibleTextbox.
 * This should be useful for other things.
 */
public class CharacterUtils {

    /**
     * Custom function for converting an JWJGL colour into minecraft's weird ARGB system.
     * Uses AWT Colour class because LWJGL's one doesn't exist on server, so it saves me a headache.
     */
    public static int getIntFromColour(Color colour) {
        return ((colour.getAlpha() & 255) << 24) | ((colour.getRed() & 255) << 16) | ((colour.getGreen() & 255) << 8) | (colour.getBlue() & 255);
    }

    /**
     * Susceptible to overflows, but honestly, I am not too concerned.
     * https://stackoverflow.com/a/237204
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Susceptible to overflows, but honestly, I am not too concerned. Modified to look for floats instead.
     * https://stackoverflow.com/a/237204
     */
    public static boolean isFloat(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-' || str.charAt(0) == '.' || str.charAt(str.length()-1) == '.') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if ((c < '0' || c > '9') && c != '.') {
                return false;
            }
        }
        return true;
    }

    public static boolean isCharacterValid(char c) {
        return c != 167 && (net.minecraft.util.CharacterUtils.validCharacters.indexOf(c) >= 0 || c > ' ');
    }

    public static String stripInvalidChars(String string) {
        StringBuilder var1 = new StringBuilder();
        char[] var2 = string.toCharArray();

        for (char var5 : var2) {
            if (isCharacterValid(var5)) {
                var1.append(var5);
            }
        }

        return var1.toString();
    }

    public static void setClipboardText(String string) {
        try {
            StringSelection var1 = new StringSelection(string);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, null);
        } catch (Exception ignored) {
        }

    }

    public static String getClipboardText() {
        try {
            Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)var0.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception ignored) {
        }

        return "";
    }

    public static String getRenderableString(String string, int maxPixelWidth, boolean flag, TextRenderer textRenderer) {
        StringBuilder var4 = new StringBuilder();
        int currentPixelWidth = 0;
        int var6 = flag ? string.length() - 1 : 0;
        int var7 = flag ? -1 : 1;
        boolean var8 = false;
        boolean var9 = false;

        for(int var10 = var6; var10 >= 0 && var10 < string.length() && currentPixelWidth < maxPixelWidth; var10 += var7) {
            char var11 = string.charAt(var10);
            int var12 = textRenderer.getTextWidth(Character.toString(var11));
            if (var8) {
                var8 = false;
                if (var11 != 'l' && var11 != 'L') {
                    if (var11 == 'r' || var11 == 'R') {
                        var9 = false;
                    }
                } else {
                    var9 = true;
                }
            } else if (var12 < 0) {
                var8 = true;
            } else {
                currentPixelWidth += var12;
                if (var9) {
                    ++currentPixelWidth;
                }
            }

            if (currentPixelWidth > maxPixelWidth) {
                break;
            }

            if (flag) {
                var4.insert(0, var11);
            } else {
                var4.append(var11);
            }
        }

        return var4.toString();
    }
}
