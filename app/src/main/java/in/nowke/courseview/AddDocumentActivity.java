package in.nowke.courseview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout emptyView;

    private ProgressBar documentProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(Constants.ACTION_INTENT_DOCUMENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        setContentView(R.layout.activity_add_document);

        setupProgressBar();
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

    private void setupProgressBar() {
        documentProgressBar = (ProgressBar) findViewById(R.id.progressBarDocument);
        documentProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Document");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
    }

    private void setupRecycler() {
        emptyView = (LinearLayout) findViewById(R.id.document_empty_view);
        documentListRecycler = (RecyclerView) findViewById(R.id.documentListRecycler);
        documentListRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Refresh Layout
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("REF", "refresh items");
                new ListAllDocuments().execute(Constants.DOCUMENTS_URL);
            }
        });

    }

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_INTENT_DOCUMENT.equals(intent.getAction())) {
                boolean isUpdated = intent.getBooleanExtra(Constants.INTENT_UPDATE, false);
                if (isUpdated) {
                    setResultForActivity();
                }
            }
        }
    };

    public void setResultForActivity() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.INTENT_UPDATE, true);
        setResult(Constants.RESULT_DOCUMENT_UPDATE, resultIntent);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    class ListAllDocuments extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!refreshLayout.isRefreshing()) {
                documentProgressBar.setVisibility(View.VISIBLE);
            }
        }

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

                    document.originalId = documentObj.getInt("id");
                    document.title = documentObj.getString("title");
                    document.owner = documentObj.getString("owner");

                    documents.add(document);
                }
                documentProgressBar.setVisibility(View.GONE);

                adapter = new DocumentListAdapter(AddDocumentActivity.this, documents, emptyView);
                documentListRecycler.setAdapter(adapter);
                refreshLayout.setRefreshing(false);

            } catch (Exception e) {
                Log.i("ERROR", "Internet connection error");
                Snackbar.make(coordinatorLayout, "Check your internet connection!", Snackbar.LENGTH_LONG).show();
                refreshLayout.setRefreshing(false);
                documentListRecycler.setAdapter(new DocumentListAdapter(AddDocumentActivity.this, new ArrayList<Document>(), emptyView));
                documentProgressBar.setVisibility(View.GONE);
            }

        }
    }
}
