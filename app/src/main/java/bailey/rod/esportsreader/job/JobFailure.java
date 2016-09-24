package bailey.rod.esportsreader.job;

/**
 * Created by rodbailey on 24/09/2016.
 */
public class JobFailure {

    private final String reason;

    public JobFailure(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
