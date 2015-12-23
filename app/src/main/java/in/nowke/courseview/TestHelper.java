package in.nowke.courseview;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

/**
 * Created by nav on 22/12/15.
 */
public class TestHelper {
    public static String testFunction() {
       String mURL =  "http://192.168.1.104:5200/syll.md";
        try {
            String markdown = new DownloadFileFromURL().execute(mURL).get();
            return markdown;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        //Log.d("J", "hi");
        return null;
    }

    static class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {
            int count;

            try {
                URL file_url = new URL(url[0]);
                URLConnection connection = file_url.openConnection();
                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(file_url.openStream(), 8192);

//                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.md");

//                byte data[] = new byte[1024];
//
//                long total = 0;
//
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//                    // publishing the progress....
//                    // After this onProgressUpdate will be called
//                    publishProgress(""+(int)((total*100)/lenghtOfFile));
//
//                    // writing data to file
//                    output.write(data, 0, count);
//                }
//
//                // flushing output
//                output.flush();
//
//                output.close();
                String mStr = TestHelper.convertStreamToString(input);
                input.close();
                return mStr;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }
    }

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
}
