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
import bailey.rod.esportsreader.activity.ESportFeedActivity;
import bailey.rod.esportsreader.xml.atom.AtomCollectionEntry;

/**
 * Adapts a list of AtomCollectionEntry instances to be the data source for a ListView.
 * Each list item contains the "title" of a collection, which corresponds to a source of a single
 * news feed.
 */
public class AtomCollectionEntryListAdapter extends ArrayAdapter<AtomCollectionEntry> {

    private static final String TAG = AtomCollectionEntryListAdapter.class.getSimpleName();

    public AtomCollectionEntryListAdapter(Context context, List<AtomCollectionEntry> entryList) {
        super(context, 0, entryList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AtomCollectionEntry entry = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout
                                                                            .esports_feed_list_item,
                                                                    parent, false);
        }

        TextView firstLineTextView = (TextView) convertView.findViewById(R.id.text1);
        TextView secondLineTextView = (TextView) convertView.findViewById(R.id.text2);

        firstLineTextView.setText(entry.getTitle());
        secondLineTextView.setText(entry.getSummary());

        convertView.setOnClickListener(new ItemClickListener());
        convertView.setTag(entry.getLinkViaHref());

        return convertView;
    }

    private class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String feedHref = (String) view.getTag();
            Log.d(TAG, "Clicked on feed with href " + feedHref);

            Context context = AtomCollectionEntryListAdapter.this.getContext();

            Intent intent = new Intent(context, ESportFeedActivity.class);
            intent.putExtra(ESportFeedActivity.EXTRA_FEED_HREF, feedHref);
            context.startActivity(intent);
        }
    }
}
