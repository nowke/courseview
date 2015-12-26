package in.nowke.courseview.classes;

import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by nav on 23/12/15.
 */
public class Helpers {
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void setPreviousDocument(SharedPreferences preferences, long documentId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(Constants.PREVIOUS_DOC_PREF, documentId);
        editor.apply();
    }
}
