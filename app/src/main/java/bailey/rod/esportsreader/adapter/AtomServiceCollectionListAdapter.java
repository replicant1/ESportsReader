package bailey.rod.esportsreader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

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
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(collection.getTitle());

        return convertView;
    }

}
