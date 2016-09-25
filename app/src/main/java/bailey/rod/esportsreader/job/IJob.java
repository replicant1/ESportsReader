package bailey.rod.esportsreader.job;

/**
 *  Basic unit of work
 */
public interface IJob {

    /**
     * Does some work in a synchronous manner.
     */
    public Object doJob() throws Throwable;
}
