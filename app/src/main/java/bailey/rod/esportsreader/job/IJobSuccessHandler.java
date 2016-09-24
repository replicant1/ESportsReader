package bailey.rod.esportsreader.job;

/**
 * @see JobEngineSingleton
 */
public interface IJobSuccessHandler {

    /**
     * Called by the JobEngineSingleton whenever a job has completed successfully.
     *
     * @param result The result of the job's work. Callers will know how to cast.
     */
    public void onSuccess(String result);
}
