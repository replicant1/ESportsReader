package bailey.rod.esportsreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import bailey.rod.esportsreader.R;

/**
 * Base class for any activity that uses the layout in activity_with_list_view.xml to do display a progress monitor
 * while asynchronous requesting occurs, and to display an error message if the reqeust fails.
 */
public class ESportAsyncRequestingActivity extends AppCompatActivity {

    private static final String TAG = ESportAsyncRequestingActivity.class.getSimpleName();

    protected TextView errorMessage;

    protected ListView listView;

    protected TextView progressMessage;

    protected View progressMonitor;

    /**
     * Show the error message panel while hiding the listView and progressMonitor. This occurs when an attempt to
     * load XML fails due to network problems.
     */
    protected void showErrorMessage(String msg) {
        progressMonitor.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.setText(msg);
    }


    /**
     * Show the listView while hiding the progressMonitor and errorMessage. This occurs when loading of XML completes
     * successfully.
     */
    protected void showListView() {


        progressMonitor.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    /**
     * Show the progressMonitor and set a given progressMessage. Hide all else. This occurs when data loading is
     * underway but not yet complete.
     */
    protected void showProgressMessage(String msg) {
        listView.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        progressMonitor.setVisibility(View.VISIBLE);
        progressMessage.setText(msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_with_list_view);

        progressMonitor = findViewById(R.id.progress_monitor);
        listView = (ListView) findViewById(R.id.esport_list_view);
        errorMessage = (TextView) findViewById(R.id.error_message);
        progressMessage = (TextView) findViewById(R.id.progress_message);
    }
}
