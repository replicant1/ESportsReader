package bailey.rod.esportsreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bailey.rod.esportsreader.R;
import bailey.rod.esportsreader.activity.ESportFeedEntryActivity;
import bailey.rod.esportsreader.xml.ESportsFeedEntry;

/**
 * Adapts a list of ESportsFeedEntry instances to be the data source of a ListView.
 * Each list item contains:
 * - The title of the entry
 * - The pub/update date/time of the entry
 * - A synopsis of the entry, if available.
 */
public class ESportsFeedEntrySynopsisListAdapter extends ArrayAdapter<ESportsFeedEntry> {

    private static final String TAG = ESportsFeedEntrySynopsisListAdapter.class.getSimpleName();

    public ESportsFeedEntrySynopsisListAdapter(Context context, List<ESportsFeedEntry> entryList) {
        super(context, 0, entryList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ESportsFeedEntry entry = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.esports_feed_entry_synopsis_list_item, parent, false);
        }

        TextView firstLineTextView = (TextView) convertView.findViewById(R.id.text1);
        TextView secondLineTextView = (TextView) convertView.findViewById(R.id.text2);
        TextView thirdLineTextView = (TextView) convertView.findViewById(R.id.text3);

        firstLineTextView.setText(entry.getTitle());
        secondLineTextView.setText(entry.getPublished());

        String synopsis = entry.getSynopsis();
        if (synopsis != null) {
            thirdLineTextView.setText(Html.fromHtml(synopsis));
        } else {
            thirdLineTextView.setText("No synopsis available");
        }

        convertView.setTag(entry.getLink());
        convertView.setOnClickListener(new ItemClickListener());

        return convertView;
    }

    private class ItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String itemHref = (String) view.getTag();
            Log.d(TAG, "Click on feed item with href " + itemHref);

            Context context = ESportsFeedEntrySynopsisListAdapter.this.getContext();

            Intent intent = new Intent(context, ESportFeedEntryActivity.class);
            intent.putExtra(ESportFeedEntryActivity.EXTRA_HTML_CONTENT_URL, itemHref);
            context.startActivity(intent);
        }
    }
}
