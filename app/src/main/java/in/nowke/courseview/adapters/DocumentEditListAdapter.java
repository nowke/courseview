package in.nowke.courseview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.nowke.courseview.R;
import in.nowke.courseview.model.Document;

/**
 * Created by nav on 27/12/15.
 */
public class DocumentEditListAdapter extends RecyclerView.Adapter<DocumentEditListAdapter.DocumentViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    private List<Document> data;

    public DocumentEditListAdapter(Context context, List<Document> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.row_document_edit_list, parent, false);
        DocumentViewHolder holder = new DocumentViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        Document current = data.get(position);

        holder.documentTitle.setText(current.title);
        holder.documentOwner.setText(Html.fromHtml("Uploaded by <i><b>" + current.owner + "</b></i>"));
        holder.documentSubjects.setText("Displaying 5 of 6 documents");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DocumentViewHolder extends RecyclerView.ViewHolder {

        TextView documentTitle;
        TextView documentOwner;
        TextView documentId;
        TextView documentSubjects;

        public DocumentViewHolder(View itemView) {
            super(itemView);

            documentTitle = (TextView) itemView.findViewById(R.id.documentTitle);
            documentOwner = (TextView) itemView.findViewById(R.id.documentOwner);
            documentId = (TextView) itemView.findViewById(R.id.documentId);
            documentSubjects = (TextView) itemView.findViewById(R.id.documentDisplaySubjects);
        }
    }
}
