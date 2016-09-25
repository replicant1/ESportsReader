package bailey.rod.esportsreader.job;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by rodbailey on 24/09/2016.
 */
public class JobEngineSingleton {


    /**
     * Params = String
     * Progress = Void
     * Result = String
     */
    private class ExecuteJobTask extends AsyncTask<Void, Void, Object> {

        private final String TAG = ExecuteJobTask.class.getSimpleName();

        private String failureReason;

        private final IJob job;

        private final IJobFailureHandler failureHandler;

        private final IJobSuccessHandler successHandler;

        public ExecuteJobTask(IJob job, IJobSuccessHandler successHandler, IJobFailureHandler failureHandler) {
            this.job = job;
            this.successHandler = successHandler;
            this.failureHandler = failureHandler;
        }

        @Override
        protected void onCancelled() {
            failureHandler.onFailure("Cancelled");
        }

        @Override
        protected Object doInBackground(Void... voids) {
            Object result = null;

            try {
                result = job.doJob();
            }
            catch (Throwable thr) {
                Log.w(TAG, "Failed to do job in background", thr);
                failureReason = thr.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (failureReason == null) {
                successHandler.onSuccess(result);
            }
            else {
                failureHandler.onFailure(failureReason);
            }
        }
    }

    private static final JobEngineSingleton singleton = new JobEngineSingleton();

    public static JobEngineSingleton getInstance() {
        return singleton;
    }

    public void doJobAsync(IJob job, IJobSuccessHandler successHandler, IJobFailureHandler failureHandler) {
        ExecuteJobTask asyncTask = new ExecuteJobTask(job, successHandler, failureHandler);
        asyncTask.execute();
    }

    public void cancelAll() {
        // I would love to have a way of cancelling all outstanding HttpURLConnections.
        // This is a facility libs like Volley provide, which I miss.
    }
}
