package WorkerClasses;

import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

public class AppointmentMakerTest {

    private AppointmentMaker maker;

    @org.junit.Before
    public void setup() {
        maker = new AppointmentMaker("Calendar1.json", "Calendar2.json", "TimeDuration.json");

        System.out.println("Running tests...");
    }


    @Test
    public void addDurationToTime_Test_EXAMPLE() {
        String time = "11:00";
        long howMany = 0;
        Date date = new Date();
        try {
            date = maker.getFormat().parse(time);
            long timeInMinutes = (date.getTime() / 60000) + 60;
            long newTimeInMinutes = timeInMinutes + maker.getDurationInMinutes() * howMany;

            assertEquals((newTimeInMinutes / 60 + ":" + newTimeInMinutes % 60),maker.addDurationToTime(time,howMany));

        }catch (ParseException | NullPointerException e){
            System.out.println(e.getMessage());
            assertEquals("PARSE ERROR",maker.addDurationToTime(time,howMany));
        }

    }

    @Test
    public void addDurationToTime_Test_WRONG_FORMAT() {
        String time = "WRONG FORMAT";
        long howMany = 2;

        assertEquals("PARSE ERROR",maker.addDurationToTime(time,howMany));
    }

    @Test
    public void addDurationToTime_Test_NULL() {
        String time = null;
        long howMany = 2;

        assertEquals("PARSE ERROR",maker.addDurationToTime(time,howMany));
    }

    @Test
    public void isDuration_Test_EXAMPLE() {
        String in_str1 = "03:30";
        String in_str2 = "06:00";

        Date date1 = new Date();
        Date date2 = new Date();
        try {
            date1 = maker.getFormat().parse(in_str1);
            date2 = maker.getFormat().parse(in_str2);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        long durationInMinutes = maker.durationToMinutes();
        long firstTime = (date1.getTime() / 60000) + 60; // w minutach
        long secondTime = (date2.getTime() / 60000) + 60; // w minutach

        long newTime = secondTime - firstTime;


        long howManyMeetings = newTime / durationInMinutes;
        assertEquals(howManyMeetings, maker.isDuration(in_str1,in_str2));

    }

    @Test
    public void isDuration_Test_NULL() {
        String in_str1 = "03:33";
        String in_str2 = null;

        assertEquals(-1, maker.isDuration(in_str1, in_str2));

    }

    @Test
    public void isDuration_Test_WRONG_FORMAT() {
        String in_str1 = "WRONG FORMAT";
        String in_str2 = "12:35";

        assertEquals(-1, maker.isDuration(in_str1, in_str2));

    }


    @Test
    public void durationToMinutes_Test_WRONG_FORMAT() {
        AppointmentMaker makerTemp = new AppointmentMaker("Calendar1.json", "Calendar2.json", "TimeDuration.json");

        makerTemp.setMeeting_duration("NO DATE FORMAT");   // nie moze przeparsowac i rzuca wyjatek
        assertEquals(-1, makerTemp.durationToMinutes());
    }

    @Test
    public void durationToMinutes_Test_NULL() {
        AppointmentMaker makerTemp = new AppointmentMaker("Calendar1.json", "Calendar2.json", "TimeDuration.json");

        makerTemp.setMeeting_duration(null);            // nie moze przeparsowac i rzuca wyjatek
        assertEquals(-1, makerTemp.durationToMinutes());
    }

    @Test
    public void durationToMinutes_Test_EXAMPLE() {
        AppointmentMaker makerTemp = new AppointmentMaker("Calendar1.json", "Calendar2.json", "TimeDuration.json");

        makerTemp.setMeeting_duration("2:10");
        Date date = new Date();
        try {
            date = makerTemp.getFormat().parse(makerTemp.formatTime(makerTemp.getMeeting_duration()));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        long expected = (date.getTime() / 60000 + 60);
        System.out.println(expected);
        assertEquals(expected, makerTemp.durationToMinutes());
    }


    @Test
    public void formatTime_Test_ZERO() {
        String result = maker.formatTime("");
        assertEquals("0", result);
    }

    @Test
    public void formatTime_Test_NULL() {
        String result = maker.formatTime(null);
        assertEquals("UNKNOWN FORMAT", result);

    }

    @Test
    public void formatTime_Test_equal_4() {
        String[] array_in = {"2 4O", "3:3b", "13;9", "y3:7"};
        String[] array_exp = new String[3];
        String[] array_Results = new String[3];

        for (int i = 0; i <= 2; i++) {
            array_Results[i] = maker.formatTime(array_in[i]);
            if (array_in[i].matches("[0-9][0-9]:[0-9]")) {
                array_exp[i] = array_in[i] + "0";
                array_exp[i] = "PARSE ERROR";
            } else if (array_in[i].matches("[0-9]:[0-9][0-9]")) {
                array_exp[i] = "0" + array_in[i];
            } else {
                array_exp[i] = "PARSE ERROR";
            }

        }

        assertArrayEquals(array_exp, array_Results);
    }


    @Test
    public void formatTime_Test_equal_3() {
        String[] array_in = {"2:4", "_3b", "3;9", ":d "};
        String[] array_exp = new String[3];
        String[] array_Results = new String[3];

        for (int i = 0; i <= 2; i++) {
            array_Results[i] = maker.formatTime(array_in[i]);
            if (array_in[i].matches("[0-9]:[0-9]")) {
                array_exp[i] = "0" + array_in[i] + "0";
            } else {
                array_exp[i] = "PARSE ERROR";
            }
        }

        assertArrayEquals(array_exp, array_Results);
    }


    @Test
    public void formatTime_Test_LENGTH_equals_5() {
        String[] array_in = {"12:4:", "la_3b", "aa: d", ": d 0"};

        String[] array_exp = new String[4];
        String[] array_Results = new String[4];

        for (int i = 0; i <= 3; i++) {
            array_Results[i] = maker.formatTime(array_in[i]);
            if (array_in[i].matches("[0-9][0-9]:[0-9][0-9]")) {
                array_exp[i] = array_in[i];
            } else {
                array_exp[i] = "PARSE ERROR";
            }
        }

        assertArrayEquals(array_exp, array_Results);

    }


}
