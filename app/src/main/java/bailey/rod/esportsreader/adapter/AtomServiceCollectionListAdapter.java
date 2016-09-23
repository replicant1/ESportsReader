package bailey.rod.esportsreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.activity.ESportFeedListActivity;
import bailey.rod.esportsreader.xml.atom.AtomServiceCollection;

/**
 * Adapts a list of AtomServiceCollection instances to be the data source for a ListView.
 * Each list item contains the "title" of a collection.
 */
public class AtomServiceCollectionListAdapter extends ArrayAdapter<AtomServiceCollection> {

    private static final String TAG = AtomServiceCollectionListAdapter.class.getSimpleName();

    public AtomServiceCollectionListAdapter(Context context, List<AtomServiceCollection> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AtomServiceCollection collection = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.esports_list_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text1);
        textView.setText(collection.getTitle());

        convertView.setOnClickListener(new ItemClickListener());
        convertView.setTag(collection.getCollectionDocumentHref());

        return convertView;
    }

    /**
     * When a list item is clicked, starts an Intent to go the next activity which will display a list of
     * all the feeds in the feed collection just clicked on.
     *
     * @#see ESportFeedListActivity
     */
    private class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String collectionDocumentHref = (String) view.getTag();
            Log.d(TAG, "Clicked on collection document with href " + collectionDocumentHref);

            Context context = AtomServiceCollectionListAdapter.this.getContext();

            Intent intent = new Intent(context, ESportFeedListActivity.class);
            intent.putExtra(ESportFeedListActivity.EXTRA_ATOM_COLLECTION_DOCUMENT_HREF, collectionDocumentHref);
            context.startActivity(intent);
        }
    }
}
