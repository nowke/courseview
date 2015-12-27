package in.nowke.courseview;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import in.nowke.courseview.adapters.CourseviewDBAdapter;
import in.nowke.courseview.adapters.DocumentEditListAdapter;
import in.nowke.courseview.classes.ClickListener;
import in.nowke.courseview.classes.RecyclerTouchListener;
import in.nowke.courseview.model.Document;

public class EditSubjectActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView documentListRecycler;
    private DocumentEditListAdapter adapter;
    private CourseviewDBAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        setupToolbar();
        setupRecycler();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_edit_subject);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecycler() {
        documentListRecycler = (RecyclerView) findViewById(R.id.documentListRecycler);
        documentListRecycler.setLayoutManager(new LinearLayoutManager(this));

        // setup database
        helper = new CourseviewDBAdapter(this);
        List<Document> documentList = helper.getDocumentList();
        adapter = new DocumentEditListAdapter(this, documentList);
        documentListRecycler.setAdapter(adapter);
        documentListRecycler.addOnItemTouchListener(new RecyclerTouchListener(this, documentListRecycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.i("LOL", "im clicked");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

}
