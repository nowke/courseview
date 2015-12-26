package in.nowke.courseview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import in.nowke.courseview.adapters.CourseviewDBAdapter;
import in.nowke.courseview.classes.Constants;
import in.nowke.courseview.classes.Helpers;
import in.nowke.courseview.model.Document;
import in.nowke.courseview.model.Subject;
import us.feras.mdv.MarkdownView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private MarkdownView markdownView;
    private DrawerLayout subjectDrawer;
    private DrawerLayout drawer;
    private LinearLayout emptyDocLayout;

    private CourseviewDBAdapter helper;
    private SharedPreferences preferences;

    private long previousDocumentId;
    private long noOfDocuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPreviousDocumentId();

        helper = new CourseviewDBAdapter(this);
        noOfDocuments = helper.getDocumentCount();

        setupToolbar();
        setupNavigation();
        loadDocumentOrShowEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_content:
                if (subjectDrawer != null) {
                    if (subjectDrawer.isDrawerOpen(GravityCompat.END)) {
                        subjectDrawer.closeDrawers();
                    } else {
                        subjectDrawer.openDrawer(GravityCompat.END);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPreviousDocumentId() {
        preferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        previousDocumentId = preferences.getLong(Constants.PREVIOUS_DOC_PREF, -1);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (noOfDocuments > 0) {
            navigationView.getMenu().add(R.id.groupDocuments, Menu.NONE, Menu.NONE, "Documents").setCheckable(false).setEnabled(false);
        }
        List<Document> documents = helper.getDocumentList();
        for (int i=0; i<documents.size(); i++) {
            Document document = documents.get(i);
            if ((i==0 && previousDocumentId == -1) || (previousDocumentId == document.id)) {
                navigationView.getMenu().add(R.id.groupDocuments, Menu.NONE, Menu.NONE, document.title).setCheckable(true).setChecked(true);

                Helpers.setPreviousDocument(preferences, document.id);
                previousDocumentId = document.id;
            }
            else {
                navigationView.getMenu().add(R.id.groupDocuments, Menu.NONE, Menu.NONE, document.title).setCheckable(true);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        if (item.isChecked()) {
            drawer.closeDrawers();
            return false;
        }

        if (item.getItemId() == R.id.menu_add) {
            drawer.closeDrawers();
            startActivity(new Intent(this, AddDocumentActivity.class));
            return true;
        }

        item.setChecked(true);
        drawer.closeDrawers();

        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(300);
                final Document document = helper.getDocument(String.valueOf(item.getTitle()));
                Helpers.setPreviousDocument(preferences, document.id);
                previousDocumentId = document.id;

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadDocument(document.id);
                    }
                });

            }
        }.start();

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (subjectDrawer != null && subjectDrawer.isDrawerOpen(GravityCompat.END)) {
            subjectDrawer.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public void addDocument(View view) {
        startActivity(new Intent(this, AddDocumentActivity.class));
    }

    private void loadDocumentOrShowEmpty() {
        markdownView = (MarkdownView) findViewById(R.id.markdownView);
        emptyDocLayout = (LinearLayout) findViewById(R.id.empty_doc_layout);
        subjectDrawer = (DrawerLayout) findViewById(R.id.drawer_layout_right);

        if (noOfDocuments == 0) {
            showEmptyView();
            return;
        } else {
            showMarkdownView();
        }

        loadDocument(previousDocumentId);
    }

    private void showEmptyView() {
        subjectDrawer.setVisibility(View.GONE);
        emptyDocLayout.setVisibility(View.VISIBLE);
    }

    private void showMarkdownView() {
        subjectDrawer.setVisibility(View.VISIBLE);
        emptyDocLayout.setVisibility(View.GONE);
    }

    private void loadDocument(long documentId) {
        List<Subject> documentSubjects = helper.getAllSubjects(documentId);
        String mdStr = helper.getSubjectContent(documentSubjects.get(0).id);
        setMarkDownContent(mdStr);
        loadContentsChooser(documentSubjects);
    }

    private void loadContentsChooser(List<Subject> documentSubjects) {
        final NavigationView nav_view_right = (NavigationView) findViewById(R.id.nav_view_right);
        nav_view_right.getMenu().clear();

        nav_view_right.getMenu().add(R.id.groupContent, Menu.NONE, Menu.NONE, "Contents").setCheckable(false).setEnabled(false);

        for (int i=0; i<documentSubjects.size(); i++) {
            Subject subject = documentSubjects.get(i);
            if (i == 0) {
                nav_view_right.getMenu().add(R.id.groupContent, Menu.NONE, Menu.NONE, subject.title).setCheckable(true).setChecked(true);
            } else {
                nav_view_right.getMenu().add(R.id.groupContent, Menu.NONE, Menu.NONE, subject.title).setCheckable(true);
            }
        }
        nav_view_right.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                item.setChecked(true);

                subjectDrawer.closeDrawers();
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(300);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String mdContent = helper.getSubjectContent(String.valueOf(item.getTitle()));
                                setMarkDownContent(mdContent);
                            }
                        });
                    }
                }.start();
                return true;
            }
        });

    }

    private void setMarkDownContent(String content) {
        markdownView.loadMarkdown(content, "file:///android_asset/classic.css");
    }
}
