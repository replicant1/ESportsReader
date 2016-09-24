package bailey.rod.esportsreader.job;

/**
 *  Basic unit of work
 */
public interface IJob {

    /**
     * Does some work in a synchronous manner.
     *
     * @param args Arguments that parameterize the work to be done.
     */
    public String doJob() throws Throwable;
}
