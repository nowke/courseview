package in.nowke.courseview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import in.nowke.courseview.R;
import in.nowke.courseview.classes.DocumentDownloaderTask;
import in.nowke.courseview.model.Document;

/**
 * Created by nav on 23/12/15.
 */
public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.DocumentListViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    private List<Document> data;

    public DocumentListAdapter(Context context, List<Document> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
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
        holder.documentOwner.setText(current.owner);
        holder.documentId.setText(String.valueOf(current.id));
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

        public DocumentListViewHolder(View itemView) {
            super(itemView);

            documentTitle = (TextView) itemView.findViewById(R.id.documentTitle);
            documentOwner = (TextView) itemView.findViewById(R.id.documentOwner);
            documentId = (TextView) itemView.findViewById(R.id.documentId);

            downloadButton = (Button) itemView.findViewById(R.id.documentDownload);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int docId = Integer.parseInt(documentId.getText().toString());
                    new DocumentDownloaderTask(context).execute(docId);
                }
            });
        }
    }
}
