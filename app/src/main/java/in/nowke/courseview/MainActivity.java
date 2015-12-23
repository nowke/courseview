package in.nowke.courseview;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import us.feras.mdv.MarkdownView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
//    private MarkdownView markdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupNavigation();

//        markdownView = (MarkdownView) findViewById(R.id.markdownView);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().add(R.id.grp1, Menu.NONE, 200, "Item 1");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void addDocument(View view) {
        startActivity(new Intent(this, AddDocumentActivity.class));
    }

//    public void testDownload(View view) {
//        String mdStr = TestHelper.testFunction();
//        markdownView.loadMarkdown(mdStr, "file:///android_asset/alt.css");
//    }
//
//    public void testDownload2(View view) {
//        String mdStr = TestHelper.testFunction();
//        markdownView.loadMarkdown(mdStr, "file:///android_asset/classic.css");
//    }
//
//    public void testDownload3(View view) {
//        String mdStr = TestHelper.testFunction();
//        markdownView.loadMarkdown(mdStr, "file:///android_asset/foghorn.css");
//    }
//
//    public void testDownload4(View view) {
//        String mdStr = TestHelper.testFunction();
//        markdownView.loadMarkdown(mdStr, "file:///android_asset/paperwhite.css");
//    }
}
