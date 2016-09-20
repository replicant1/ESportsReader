package bailey.rod.esportsreader.xml;

import java.util.Date;

/**
 * Created by rodbailey on 19/09/2016.
 */
public interface IESportsFeedEntry {

    public String getTitle();

    public String getContent();

    public String getSynopsis();

    public Date getUpdated();

    public String getLink();

    public Date getPublished();
}
