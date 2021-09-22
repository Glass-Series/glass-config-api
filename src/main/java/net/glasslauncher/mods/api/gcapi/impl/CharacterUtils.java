package net.glasslauncher.mods.api.gcapi.impl;

import net.minecraft.client.render.TextRenderer;

import java.awt.*;
import java.awt.datatransfer.*;

public class CharacterUtils {


    public static final boolean isCharacterValid(char c) {
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
        } catch (Exception var2) {
        }

    }

    public static String getClipboardText() {
        try {
            Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)var0.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception var1) {
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
