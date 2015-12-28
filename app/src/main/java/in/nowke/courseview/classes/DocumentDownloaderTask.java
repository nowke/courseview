package in.nowke.courseview.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import in.nowke.courseview.AddDocumentActivity;
import in.nowke.courseview.R;
import in.nowke.courseview.adapters.CourseviewDBAdapter;
import in.nowke.courseview.model.Document;
import in.nowke.courseview.model.Subject;

/**
 * Created by nav on 23/12/15.
 */
public class DocumentDownloaderTask extends AsyncTask<Integer, String, String> {

    private ProgressDialog progressDialog;
    private Context context;
    private OnTaskCompleted listener;
    private AlertDialog.Builder downloadFailedDialog;

    public DocumentDownloaderTask(Context context, OnTaskCompleted listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Download");
        progressDialog.setMessage("Fetching document...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        downloadFailedDialog = new AlertDialog.Builder(context);
        downloadFailedDialog.setTitle(context.getResources().getString(R.string.dialog_title_download_failed));
    }

    protected String doInBackground(Integer... documentId) {
        String downloadURL = Constants.getSubjectsForDocument(documentId[0]);
        try {
            URL documentURL = new URL(downloadURL);
            InputStream input = new BufferedInputStream(documentURL.openStream(), 8192);
            String fileContent = Helpers.convertStreamToString(input);

           return fileContent;

        } catch (IOException  e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String fileContent) {
        long documentId = -1;
        if (progressDialog != null ) {
            progressDialog.setMessage("Writing contents...");
        }
        try {
            JSONArray documentWrapper = new JSONArray(fileContent);
            JSONObject documentListWrapper = documentWrapper.getJSONObject(0);
            JSONArray subjectList = documentListWrapper.getJSONArray("subjects");

            List<Subject> subjects = new ArrayList<>();

            for (int i=0; i < subjectList.length(); i++) {
                Subject subject = new Subject();
                JSONObject subjectObj = subjectList.getJSONObject(i);

                subject.code = subjectObj.getString("code");
                subject.credits = subjectObj.getDouble("credits");
                subject.title = subjectObj.getString("title");
                subject.content = subjectObj.getString("content");

                subjects.add(subject);
            }

            // Database
            CourseviewDBAdapter helper = new CourseviewDBAdapter(context);

            // Create document
            Document document = new Document();
            document.originalId = documentListWrapper.getInt("id");
            document.title = documentListWrapper.getString("title");
            document.owner = documentListWrapper.getString("owner");
            document.modified = documentListWrapper.getString("modified");

            documentId = helper.addDocument(document);
            Log.i("LOL", String.valueOf(documentId));
            // Fill subjects
            long curSubId = helper.addSubjects(subjects, documentId);
            helper.updateCurrentSubjectToDocument(documentId, curSubId);


        } catch (Exception e) {
            e.printStackTrace();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            // Show "Download failed" dialog
            downloadFailedDialog.setMessage(R.string.desc_download_failed);
            downloadFailedDialog.setPositiveButton(R.string.button_ok, null);
            AlertDialog alert = downloadFailedDialog.create();
            alert.show();
            return;
        }

        // Successfully downloaded
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        listener.onTaskCompleted(documentId);
    }
}
