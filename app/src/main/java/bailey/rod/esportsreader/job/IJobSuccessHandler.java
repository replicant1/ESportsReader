package bailey.rod.esportsreader.job;

/**
 * @see JobEngineSingleton
 */
public interface IJobSuccessHandler {

    /**
     * Called by the JobEngineSingleton whenever a job has completed successfully.
     */
    public void onSuccess(Object result);
}
