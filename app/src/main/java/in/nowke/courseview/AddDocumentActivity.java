package in.nowke.courseview;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import in.nowke.courseview.adapters.DocumentListAdapter;
import in.nowke.courseview.classes.Constants;
import in.nowke.courseview.classes.Helpers;
import in.nowke.courseview.model.Document;

public class AddDocumentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView documentListRecycler;
    private DocumentListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        setupToolbar();
        setupRecycler();
        new ListAllDocuments().execute(Constants.DOCUMENTS_URL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Document");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryText), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void setupRecycler() {
        documentListRecycler = (RecyclerView) findViewById(R.id.documentListRecycler);
        documentListRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    class ListAllDocuments extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {
            try {
                URL document_url = new URL(url[0]);
                InputStream input = new BufferedInputStream(document_url.openStream(), 8192);
                String fileContent = Helpers.convertStreamToString(input);
                return fileContent;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String fileContent) {

            try {
                JSONArray documentWrapper = new JSONArray(fileContent);
                JSONObject documentListWrapper = documentWrapper.getJSONObject(0);
                JSONArray documentList = documentListWrapper.getJSONArray("docs");

                List<Document> documents = new ArrayList<>();

                for (int i=0; i<documentList.length(); i++) {
                    Document document = new Document();
                    JSONObject documentObj = documentList.getJSONObject(i);

                    document.id = documentObj.getInt("id");
                    document.title = documentObj.getString("title");
                    document.owner = documentObj.getString("owner");

                    documents.add(document);
                }

                adapter = new DocumentListAdapter(AddDocumentActivity.this, documents);
                documentListRecycler.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}