package in.nowke.courseview.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import in.nowke.courseview.AddDocumentActivity;
import in.nowke.courseview.R;
import in.nowke.courseview.classes.Constants;
import in.nowke.courseview.classes.DocumentDownloaderTask;
import in.nowke.courseview.classes.Helpers;
import in.nowke.courseview.classes.OnTaskCompleted;
import in.nowke.courseview.model.Document;

/**
 * Created by nav on 23/12/15.
 */
public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.DocumentListViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    private List<Document> data;
    private View emptyView;
    private CourseviewDBAdapter helper;

    public DocumentListAdapter(Context context, List<Document> data, View emptyView) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.emptyView = emptyView;
        emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        this.helper = new CourseviewDBAdapter(context);
    }

    @Override
    public DocumentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.row_document_list, parent, false);
        DocumentListViewHolder holder = new DocumentListViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(DocumentListViewHolder holder, int position) {
        Document current = data.get(position);

        holder.documentTitle.setText(current.title);
        holder.documentOwner.setText(Html.fromHtml("Uploaded by <i><b>" + current.owner + "</b></i>"));
        holder.documentId.setText(String.valueOf(current.originalId));

        // Check if document exists
        if (helper.isDocumentExists((int) current.originalId)) {

            if (helper.getDocumentModified((int) current.originalId).equals(current.modified)) {
                holder.downloadButton.setVisibility(View.GONE);
                holder.documentAvailable.setVisibility(View.VISIBLE);
            } else {
                holder.downloadButton.setText(R.string.btn_update);
                holder.downloadButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_update_black_18dp, 0, 0, 0);

                long documentId = helper.getDocumentIdFromOriginalId(current.originalId);
                holder.documentExistingId.setText(String.valueOf(documentId));
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DocumentListViewHolder extends RecyclerView.ViewHolder {

        TextView documentTitle;
        TextView documentOwner;
        TextView documentId;

        Button downloadButton;
        TextView documentAvailable;
        TextView documentExistingId;

        public DocumentListViewHolder(View itemView) {
            super(itemView);

            documentTitle = (TextView) itemView.findViewById(R.id.documentTitle);
            documentOwner = (TextView) itemView.findViewById(R.id.documentOwner);
            documentId = (TextView) itemView.findViewById(R.id.documentId);

            downloadButton = (Button) itemView.findViewById(R.id.documentDownload);
            documentAvailable = (TextView) itemView.findViewById(R.id.documentAvailable);

            documentExistingId = (TextView) itemView.findViewById(R.id.documentExistingId);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int docId = Integer.parseInt(documentId.getText().toString());
                    new DocumentDownloaderTask(context, new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(long createdDocumentId) {
                            downloadButton.setVisibility(View.GONE);
                            documentAvailable.setVisibility(View.VISIBLE);

                            // Delete old doc if updated
                            if (downloadButton.getText().equals(context.getResources().getString(R.string.btn_update))) {
                                long documentIdToDelete = Long.parseLong(documentExistingId.getText().toString());
                                helper.deleteDocument(documentIdToDelete);

                                // update current doc id
                                SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
                                long previousDocumentId = preferences.getLong(Constants.PREVIOUS_DOC_PREF, -1);
                                if (previousDocumentId == documentIdToDelete) {
                                    Helpers.setPreviousDocument(preferences, createdDocumentId);
                                }
                            }

                            Intent intent = new Intent(Constants.ACTION_INTENT_DOCUMENT);
                            intent.putExtra(Constants.INTENT_UPDATE, true);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }).execute(docId);
                }
            });
        }
    }
}
