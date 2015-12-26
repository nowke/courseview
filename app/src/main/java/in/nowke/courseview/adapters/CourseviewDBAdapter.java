package in.nowke.courseview.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.nowke.courseview.model.Document;
import in.nowke.courseview.model.Subject;

/**
 * Created by nav on 23/12/15.
 */
public class CourseviewDBAdapter {

    CourseDBHelper helper;

    public CourseviewDBAdapter(Context context) {
        helper = new CourseDBHelper(context);
    }

    public long addDocument(Document document) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CourseDBHelper.DOCUMENT_TITLE, document.title);
        contentValues.put(CourseDBHelper.DOCUMENT_OWNER, document.owner);

        long id = db.insert(CourseDBHelper.TABLE_DOCUMENT, null, contentValues);

        return id;
    }

    public void addSubjects(List<Subject> subjects, long documentId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        for (Subject subject : subjects) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CourseDBHelper.SUBJECT_DOCUMENT_ID, documentId);
            contentValues.put(CourseDBHelper.SUBJECT_TITLE, subject.title);
            contentValues.put(CourseDBHelper.SUBJECT_CODE, subject.code);
            contentValues.put(CourseDBHelper.SUBJECT_CREDITS, subject.credits);
            contentValues.put(CourseDBHelper.SUBJECT_CONTENT, subject.content);

            db.insert(CourseDBHelper.TABLE_SUBJECT, null, contentValues);
        }
    }

    public List<Subject> getAllSubjects(long documentId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {CourseDBHelper.SUBJECT_ID, CourseDBHelper.SUBJECT_TITLE};
        Cursor cursor = db.query(CourseDBHelper.TABLE_SUBJECT, columns, CourseDBHelper.SUBJECT_DOCUMENT_ID + "=" + documentId, null, null, null, null);

        List<Subject> subjectList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(CourseDBHelper.SUBJECT_ID);
            int index2 = cursor.getColumnIndex(CourseDBHelper.SUBJECT_TITLE);

            long subjectId = cursor.getLong(index1);
            String subjectTitle = cursor.getString(index2);

            Subject subject = new Subject();
            subject.id = subjectId;
            subject.title = subjectTitle;

            subjectList.add(subject);
        }
        cursor.close();
        return subjectList;
    }

    public String getSubjectContent(long subjectId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {CourseDBHelper.SUBJECT_CONTENT};
        Cursor cursor = db.query(CourseDBHelper.TABLE_SUBJECT, columns, CourseDBHelper.SUBJECT_ID + "=" + subjectId, null, null, null, null);
        String subjectContent = ""
                ;
        while (cursor.moveToNext()) {
            int contentIndex = cursor.getColumnIndex(CourseDBHelper.SUBJECT_CONTENT);
            subjectContent = cursor.getString(contentIndex);
        }

        cursor.close();
        return subjectContent;
    }

    public String getSubjectContent(String subjectTitle) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {CourseDBHelper.SUBJECT_CONTENT};
        Cursor cursor = db.query(CourseDBHelper.TABLE_SUBJECT, columns, CourseDBHelper.SUBJECT_TITLE + "='" + subjectTitle + "'", null, null, null, null);
        String subjectContent = ""
                ;
        while (cursor.moveToNext()) {
            int contentIndex = cursor.getColumnIndex(CourseDBHelper.SUBJECT_CONTENT);
            subjectContent = cursor.getString(contentIndex);
        }

        cursor.close();
        return subjectContent;
    }

    public List<Document> getDocumentList() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {CourseDBHelper.DOCUMENT_ID, CourseDBHelper.DOCUMENT_TITLE};
        Cursor cursor = db.query(CourseDBHelper.TABLE_DOCUMENT, columns, null, null, null, null, null);

        List<Document> documents = new ArrayList<>();
        while (cursor.moveToNext()) {
            Document document = new Document();

            int index1 = cursor.getColumnIndex(CourseDBHelper.DOCUMENT_ID);
            int index2 = cursor.getColumnIndex(CourseDBHelper.DOCUMENT_TITLE);

            long documentId = cursor.getLong(index1);
            String documentTitle = cursor.getString(index2);

            document.id = documentId;
            document.title = documentTitle;

            documents.add(document);
        }
        cursor.close();
        return documents;
    }

    public long getDocumentCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, CourseDBHelper.TABLE_DOCUMENT);
    }

    public Document getDocument(String documentTitle) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {CourseDBHelper.DOCUMENT_ID};
        Cursor cursor = db.query(CourseDBHelper.TABLE_DOCUMENT, columns, CourseDBHelper.DOCUMENT_TITLE + "='" + documentTitle + "'", null, null, null, null);

        Document document = new Document();
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(CourseDBHelper.DOCUMENT_ID);
            long documentId = cursor.getLong(index);
            document.id = documentId;
        }
        cursor.close();
        return document;
    }



    static class CourseDBHelper extends SQLiteOpenHelper  {

        // DATABASES
        private static final String DATABASE_NAME = "CourseDb";
        private static final int DATABASE_VERSION = 1;

        // TABLES
        private static final String TABLE_DOCUMENT = "Document";
        private static final String TABLE_SUBJECT = "Subject";

        // COLUMNS
        private static final String DOCUMENT_ID = "_id";
        private static final String DOCUMENT_TITLE = "title";
        private static final String DOCUMENT_OWNER = "owner";
        private static final String DOCUMENT_CREATED = "created";
        private static final String DOCUMENT_MODIFIED = "modified";

        private static final String SUBJECT_ID = "_id";
        private static final String SUBJECT_DOCUMENT_ID = "document";
        private static final String SUBJECT_TITLE = "title";
        private static final String SUBJECT_CODE = "code";
        private static final String SUBJECT_CREDITS = "credits";
        private static final String SUBJECT_CONTENT = "content";

        // CREATE DB STATEMENTS
        private static final String CREATE_DOCUMENT_TABLE = "CREATE TABLE " + TABLE_DOCUMENT + " (" +
                                                                DOCUMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                DOCUMENT_TITLE + " VARCHAR(100), " +
                                                                DOCUMENT_OWNER + " VARCHAR(20), " +
                                                                DOCUMENT_CREATED + " DATETIME, " +
                                                                DOCUMENT_MODIFIED + " DATETIME);";

        private static final String CREATE_SUBJECT_TABLE = "CREATE TABLE " + TABLE_SUBJECT + " (" +
                                                                SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                SUBJECT_DOCUMENT_ID + " INTEGER, " +
                                                                SUBJECT_TITLE + " VARCHAR(100), " +
                                                                SUBJECT_CODE + " VARCHAR(15), " +
                                                                SUBJECT_CREDITS + " DOUBLE, " +
                                                                SUBJECT_CONTENT + " TEXT, " +
                                                                "FOREIGN KEY (" + SUBJECT_DOCUMENT_ID + ") REFERENCES " +
                                                                    TABLE_DOCUMENT + "(" + DOCUMENT_ID + "));";

        // DROP TABLE
        private static final String DROP_DOCUMENT = "DROP TABLE IF EXISTS " + TABLE_DOCUMENT;
        private static final String DROP_SUBJECTS = "DROP TABLE IF EXISTS " + TABLE_SUBJECT;

        private Context context;


        CourseDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_DOCUMENT_TABLE);
                db.execSQL(CREATE_SUBJECT_TABLE);
            }
            catch (SQLException e) {
                Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_SUBJECTS);
                db.execSQL(DROP_DOCUMENT);
                onCreate(db);
            }
            catch (SQLException e) {
                Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
            }
        }
    }
}
