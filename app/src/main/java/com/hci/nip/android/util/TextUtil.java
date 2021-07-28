package com.hci.nip.android.util;

import android.text.Html;
import android.text.Spanned;

public class TextUtil {

    /**
     * ref: https://commonsware.com/blog/Android/2010/05/26/html-tags-supported-by-textview.html,
     * ref: https://developer.android.com/reference/android/text/Html.html
     * ref: https://developer.android.com/guide/topics/resources/string-resource#StylingWithHTML
     *
     * @param htmlString
     * @return the Android supported format
     */
    public static Spanned getFormattedHtmlString(String htmlString) {
        if (htmlString == null) {
            return null;
        }
        return Html.fromHtml(htmlString);
    }

    /**
     * <p> Find the Levenshtein distance between two Strings.</p>
     *
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     *
     * @param s the first String, must not be null
     * @param t the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input {@code null}
     *                                  <p>
     *                                  source: https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/StringUtils.java
     */
    public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        int n = s.length();
        int m = t.length();

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        final int[] p = new int[n + 1];
        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t
        int upper_left;
        int upper;

        char t_j; // jth character of t
        int cost;

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upper_left = p[0];
            t_j = t.charAt(j - 1);
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upper_left + cost);
                upper_left = upper;
            }
        }

        return p[n];
    }

}
