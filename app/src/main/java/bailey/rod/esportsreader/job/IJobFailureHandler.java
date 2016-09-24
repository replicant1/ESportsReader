package bailey.rod.esportsreader.job;

/**
 * @see IJob
 * @see JobEngineSingleton
 */
public interface IJobFailureHandler {

    /**
     * Invoked by JobSingletonEnginer if a job has failed to do it's work.
     *
     * @param failure Receivers will know how to interpret this.
     */
    public void onFailure(String failureMsg);
}
