package in.nowke.courseview.adapters;

import android.content.Context;
import android.content.Intent;
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
            holder.downloadButton.setVisibility(View.GONE);
            holder.documentAvailable.setVisibility(View.VISIBLE);
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

        public DocumentListViewHolder(View itemView) {
            super(itemView);

            documentTitle = (TextView) itemView.findViewById(R.id.documentTitle);
            documentOwner = (TextView) itemView.findViewById(R.id.documentOwner);
            documentId = (TextView) itemView.findViewById(R.id.documentId);

            downloadButton = (Button) itemView.findViewById(R.id.documentDownload);
            documentAvailable = (TextView) itemView.findViewById(R.id.documentAvailable);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int docId = Integer.parseInt(documentId.getText().toString());
                    new DocumentDownloaderTask(context, new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted() {
                            // WARNING: This executes even if not downloaded properly !!
                            downloadButton.setVisibility(View.GONE);
                            documentAvailable.setVisibility(View.VISIBLE);
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
