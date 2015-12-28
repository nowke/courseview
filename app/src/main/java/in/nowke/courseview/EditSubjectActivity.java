package in.nowke.courseview;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.UserDictionary;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.nowke.courseview.adapters.CourseviewDBAdapter;
import in.nowke.courseview.adapters.DocumentEditListAdapter;
import in.nowke.courseview.classes.ClickListener;
import in.nowke.courseview.classes.Constants;
import in.nowke.courseview.classes.Helpers;
import in.nowke.courseview.classes.RecyclerTouchListener;
import in.nowke.courseview.model.Document;
import in.nowke.courseview.model.Subject;

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
            public void onClick(View view, final int position) {
                TextView documentIdText = (TextView) view.findViewById(R.id.documentId);
                long documentId = Long.parseLong(documentIdText.getText().toString());
                final List<Subject> subjectList = helper.getAllSubjects(documentId, true);

                List<String> subjectArrayList = new ArrayList<String>();
                final List<Boolean> subjectDisplayedArrayList = new ArrayList<Boolean>();

                for (int i=0; i<subjectList.size(); i++) {
                    Subject subject = subjectList.get(i);

                    subjectArrayList.add(Helpers.toTitleCase(subject.title));
                    subjectDisplayedArrayList.add(subject.isDisplayed);
                }

                String[] subjectArray =  subjectArrayList.toArray(new String[subjectArrayList.size()]);
                boolean[] subjectDisplayedArray = Helpers.toPrimitiveArray(subjectDisplayedArrayList);

                // Build dialog

                AlertDialog.Builder builder = new AlertDialog.Builder(EditSubjectActivity.this);
                builder.setTitle(R.string.dialog_title_subjects)
                        .setMultiChoiceItems(subjectArray, subjectDisplayedArray, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                subjectDisplayedArrayList.set(which, isChecked);
                            }
                        })
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int trueCount = 0;
                                for (int i=0; i<subjectDisplayedArrayList.size(); i++) {
                                    if (subjectDisplayedArrayList.get(i))
                                        trueCount++;
                                }

                                if (trueCount == 0) {
                                    AlertDialog.Builder selectOneDialog = new AlertDialog.Builder(EditSubjectActivity.this);
                                    selectOneDialog.setMessage(R.string.dialog_select_at_lease_one);
                                    selectOneDialog.setPositiveButton(R.string.button_ok, null);
                                    AlertDialog selectAlert = selectOneDialog.create();
                                    selectAlert.show();
                                    return;
                                }

                                for (int i=0; i<subjectDisplayedArrayList.size(); i++) {
                                    long subjectId = subjectList.get(i).id;
//                                    Log.i("LOL", "subjectId:" + subjectId);
//                                    Log.i("LOL", "isDisplayed: " + subjectDisplayedArrayList.get(i));
                                    helper.changeDisplaySubject(subjectId, subjectDisplayedArrayList.get(i));
                                }
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(Constants.INTENT_UPDATE, true);
                                setResult(Constants.RESULT_DOCUMENT_UPDATE, resultIntent);

                                adapter.updateSubjectCount(position, trueCount);
                            }
                        })
                        .setNegativeButton(R.string.button_cancel, null);

                AlertDialog alert = builder.create();
                alert.show();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

}
