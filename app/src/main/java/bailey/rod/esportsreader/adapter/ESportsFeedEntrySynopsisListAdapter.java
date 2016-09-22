package bailey.rod.esportsreader.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bailey.rod.esportsreader.R;
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

        return convertView;
    }
}
