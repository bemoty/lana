package dev.bemoty.lana;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class StringTransformer {
    private static StringTransformer INSTANCE;

    public static StringTransformer getInstance() {
        if (StringTransformer.INSTANCE == null) {
            StringTransformer.INSTANCE = new StringTransformer();
        }
        return StringTransformer.INSTANCE;
    }

    private final String[] normalAlphabet;
    private final String[] reversedAlphabet;

    private StringTransformer() {
        normalAlphabet = new String[]{
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X,", "Y", "Z",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "'", ".", ",", "!", "?", "<", ">", "[", "]", "(", ")", "{", "}", ":", "/", "%", "&", ";", "\"", "’"
        };
        reversedAlphabet = new String[]{
                "Ɐ", "ᗺ", "Ɔ", "ᗡ", "Ǝ", "Ⅎ", "⅁", "H", "I", "Ր", "Ʞ", "Ꞁ", "W", "N", "O", "Ԁ", "Ꝺ", "ᴚ", "S", "⟘", "∩", "Ʌ", "M", "X", "⅄", "Z",
                "ɐ", "q", "ɔ", "p", "ǝ", "ɟ", "ᵷ", "ɥ", "ᴉ", "ɾ", "ʞ", "ꞁ", "ɯ", "u", "o", "d", "b", "ɹ", "s", "ʇ", "n", "ʌ", "ʍ", "x", "ʎ", "z",
                "0", "⥝", "ᘔ", "Ɛ", "߈", "ϛ", "9", "ㄥ", "8", "6",
                ",", "˙", "'", "¡", "¿", ">", "<", "]", "[", ")", "(", "}", "{", ":", "/", "%", "⅋", "⸵", ",,", ","
        };
    }

    public String getReversedString(final String inputString) {
        final StringBuilder newString = new StringBuilder();
        for (int i = 0; i < inputString.length(); i++) {
            final char letter = inputString.charAt(i);
            final int a = IntStream.range(0, normalAlphabet.length).filter(j -> letter == normalAlphabet[j].charAt(0)).findFirst().orElse(-1);
            newString.append((a != -1) ? reversedAlphabet[a] : letter);
        }
        newString.reverse();

        // Variable detection
        char[] charArray = newString.toString().toCharArray();
        List<Integer> variableIndices = new LinkedList<>();
        boolean percentSVariable = false;
        boolean dollarVariable = false;
        for (int i = 0; i < charArray.length; i++) {
            char currentChar = charArray[i];
            if (percentSVariable && currentChar == '$') {
                if (!dollarVariable && variableIndices.size() != 0) {
                    throw new IllegalStateException("Cannot parse $ and non-$ variables in one string");
                }
                dollarVariable = true;
                variableIndices.add(i);
            }
            if (percentSVariable && currentChar == '%') {
                if (dollarVariable) {
                    throw new IllegalStateException("Cannot parse $ and non-$ variables in one string");
                }
                variableIndices.add(i);
            }
            percentSVariable = (currentChar == 's');
        }

        // Reverse order and index of variables
        if (variableIndices.size() > 1) {
            int c = variableIndices.size();
            for (Integer i : variableIndices) {
                int m = variableIndices.size() - c;
                if (dollarVariable) {
                    newString.replace(i - 1, i + 3, "%" + c + "$s");
                } else {
                    newString.replace(i + 2 * m - 1, i + 2 * m + 1, "%" + c + "$s");
                }
                c--;
            }
        } else if (variableIndices.size() != 0) {
            if (dollarVariable) {
                newString.replace(variableIndices.get(0) - 1, variableIndices.get(0) + 3, "%1$s");
            } else {
                newString.replace(variableIndices.get(0) - 1, variableIndices.get(0) + 1, "%s");
            }
        }
        return newString.toString();
    }

    public void setClipboardValue(final String value) {
        final StringSelection stringSelection = new StringSelection(value);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    public String getClipboardValue() throws UnsupportedFlavorException, IOException {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (String) clipboard.getData(DataFlavor.stringFlavor);
    }
}
