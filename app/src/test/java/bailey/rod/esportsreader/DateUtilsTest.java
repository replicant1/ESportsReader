package bailey.rod.esportsreader;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import bailey.rod.esportsreader.util.DateUtils;


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 * "Sun, 25 Sep 2016 07:51:47 Z"
 */
public class DateUtilsTest {

    @Test
    public void testParseFromNamedStringEndingInZ() throws Exception {
        long seconds = DateUtils.parseFromNamedStr("Sun, 25 Sep 2016 07:51:47 Z");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(seconds));

        // When converted to GMT+10:00 the time is Sun, 25 Sep 2016 17:51:47
        Assert.assertEquals(25, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
        Assert.assertEquals(2016, cal.get(Calendar.YEAR));
        Assert.assertEquals(17, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(51, cal.get(Calendar.MINUTE));
        Assert.assertEquals(47, cal.get(Calendar.SECOND));
        Assert.assertEquals(cal.getTimeZone().getID(), "Australia/Sydney");
    }

    @Test
    public void testParseFromNamedStringEndingInNumericTimeZone() throws Exception {
        long seconds= DateUtils.parseFromNamedStr("Sun, 25 Sep 2016 18:45:48 +0000");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(seconds));

        // When converted to GMT+10:00 (Syd/Melb), the time becomes Mon 26 Sep 2016 04:45:48
        Assert.assertEquals(26, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
        Assert.assertEquals(2016, cal.get(Calendar.YEAR));
        Assert.assertEquals(4, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(45, cal.get(Calendar.MINUTE));
        Assert.assertEquals(48, cal.get(Calendar.SECOND));
    }

    @Test
    public void testParseFromNumberedWithMillisStrEndingInZ() throws Exception {
        long seconds = DateUtils.parseFromNumberedStr("2016-09-25T07:51:47.248Z");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(seconds));

        // When converted to GMT+10, time is 2016-09-26T14:45:48
        Assert.assertEquals(25, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
        Assert.assertEquals(2016, cal.get(Calendar.YEAR));
        Assert.assertEquals(17, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(51, cal.get(Calendar.MINUTE));
        Assert.assertEquals(47, cal.get(Calendar.SECOND));
        Assert.assertEquals(248, cal.get(Calendar.MILLISECOND));
        Assert.assertEquals(cal.getTimeZone().getID(), "Australia/Sydney");
    }

    @Test
    public void testParseFromNumberedWithMillisStrEndingInNumericTimeZone() throws Exception {
        long seconds = DateUtils.parseFromNumberedStr("2016-09-25T18:45:48.141+00:00");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(seconds));

        // When converted to GMT + 10, the time becomes Mon 26 Sep 2016 04:45:48
        Assert.assertEquals(26, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
        Assert.assertEquals(2016, cal.get(Calendar.YEAR));
        Assert.assertEquals(4, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(45, cal.get(Calendar.MINUTE));
        Assert.assertEquals(48, cal.get(Calendar.SECOND));
        Assert.assertEquals(141, cal.get(Calendar.MILLISECOND));
        Assert.assertEquals(cal.getTimeZone().getID(), "Australia/Sydney");
    }

    @Test
    public void testParseFromNumberedWithoutMillisStrEndingInZ() throws ParseException {
        long seconds = DateUtils.parseFromNumberedStr("2016-09-18T07:51:54Z");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(seconds));

        // When converted to GMT+10 the time is 2016-09-18T17:51:54
        Assert.assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
        Assert.assertEquals(2016, cal.get(Calendar.YEAR));
        Assert.assertEquals(17, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(51, cal.get(Calendar.MINUTE));
        Assert.assertEquals(54, cal.get(Calendar.SECOND));
        Assert.assertEquals(cal.getTimeZone().getID(), "Australia/Sydney");
    }

    @Test
    public void testParseFromNumberedWithoutMillisStrEndingInNumbericTimeZone() throws ParseException {
        long seconds = DateUtils.parseFromNumberedStr("2016-09-21T23:56:03.431+08:00");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(seconds));

        // When converted to GMT+10 the time tis 2016-09-22T01:56:03.431
        Assert.assertEquals(22, cal.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
        Assert.assertEquals(2016, cal.get(Calendar.YEAR));
        Assert.assertEquals(1, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(56, cal.get(Calendar.MINUTE));
        Assert.assertEquals(3, cal.get(Calendar.SECOND));
        Assert.assertEquals(431, cal.get(Calendar.MILLISECOND));
        Assert.assertEquals(cal.getTimeZone().getID(), "Australia/Sydney");
    }

}