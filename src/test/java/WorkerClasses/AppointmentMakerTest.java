package WorkerClasses;

import DataModel.ModelClasses.ImportantHours;
import org.junit.Test;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

public class AppointmentMakerTest {

    private static AppointmentMaker maker;
    private AppointmentMaker makerTemp;

    @org.junit.BeforeClass
    public static void beforeClass() {
        maker = new AppointmentMaker("Calendar1.json", "Calendar2.json", "TimeDuration.json");

        System.out.println("Running tests...");
    }

    @org.junit.Before
    public void setup(){
         makerTemp = new AppointmentMaker("Calendar1.json", "Calendar2.json", "TimeDuration.json");
    }


    @Test
    public void addPossibleMeetingTime_Test_EXAMPLE() {

        if(makerTemp.getWorking_hours1().getEnd().compareTo(
                makerTemp.getWorking_hours2().getEnd()) > 0) {
            System.out.println("Leaving office hour: " + makerTemp.getWorking_hours2().getEnd());
        }else {
            System.out.println("Leaving office hour: " + makerTemp.getWorking_hours1().getEnd());

        }


        System.out.println("Duration of meeting[min]: " + makerTemp.getDurationInMinutes());
        int[] howMany = {1,3,5,2,4,6};
        int i =0;

        makerTemp.addPossibleMeetingTime(howMany[0],"10:30");
        makerTemp.addPossibleMeetingTime(howMany[1],"14:00");
        makerTemp.addPossibleMeetingTime(howMany[2],"2:30");

        if(makerTemp.getWorking_hours1().getEnd().compareTo(
                makerTemp.getWorking_hours2().getEnd()) > 0) {
            System.out.println("Leaving office hour(MAX HOUR): " + makerTemp.getWorking_hours2().getEnd());

        }else {
            System.out.println("Leaving office hour(MAX HOUR): " + makerTemp.getWorking_hours1().getEnd());
        }

        System.out.println("1. Possible times: " + makerTemp.getAvailableTimes().size());
        for(ImportantHours el: makerTemp.getAvailableTimes()){
            System.out.print(el.toString() + " --> " + howMany[i] + "x"+makerTemp.getDurationInMinutes()+"min");
            if( el.getEnd().equals(makerTemp.getWorking_hours2().getEnd())){
                System.out.print("  +  " + "End time equals Leaving office hour: " + makerTemp.getWorking_hours2().getEnd());
            }else if( el.getEnd().equals(makerTemp.getWorking_hours1().getEnd())){
                System.out.print("  +  " + "End time equals Leaving office hour: " + makerTemp.getWorking_hours1().getEnd());
            }
            System.out.println();
            i++;
        }

        assertEquals(3, makerTemp.getAvailableTimes().size());



        makerTemp.getAvailableTimes().clear();
        System.out.println("\n");

        makerTemp.setMeeting_duration("01:15");
        makerTemp.setDurationInMinutes(makerTemp.durationToMinutes());

        if(makerTemp.getWorking_hours1().getEnd().compareTo(
                makerTemp.getWorking_hours2().getEnd()) > 0) {
            System.out.println("Leaving office hour: " + makerTemp.getWorking_hours2().getEnd());
        }else {
            System.out.println("Leaving office hour: " + makerTemp.getWorking_hours1().getEnd());

        }

        System.out.println("Duration of meeting[min]: " + makerTemp.getDurationInMinutes());
      makerTemp.addPossibleMeetingTime(howMany[3],"09:55");
      makerTemp.addPossibleMeetingTime(howMany[4],"16:15");
      makerTemp.addPossibleMeetingTime(howMany[5],"14:30");

        System.out.println("Another 3 possible times: " + makerTemp.getAvailableTimes().size());
        for(ImportantHours el: makerTemp.getAvailableTimes()){
            System.out.println(el.toString() + " --> " + howMany[i] + "x"+makerTemp.getDurationInMinutes()+"min");
            if(el.getEnd().equals(makerTemp.getWorking_hours2().getEnd())){
                System.out.println("  +  " + "End time equals Leaving office hour: " + makerTemp.getWorking_hours2().getEnd());
            }else if(el.getEnd().equals(makerTemp.getWorking_hours1().getEnd())){
                System.out.println("  +  " + "End time equals Leaving office hour: " + makerTemp.getWorking_hours1().getEnd());
            }
            i++;
        }

        assertEquals(3, makerTemp.getAvailableTimes().size());
    }

    @Test
    public void addDurationToTime_Test_EXAMPLE() {
        String time = "11:00";
        long howMany = 1;
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

        assertEquals(0, maker.isDuration(in_str1, in_str2));

    }

    @Test
    public void isDuration_Test_WRONG_FORMAT() {
        String in_str1 = "WRONG FORMAT";
        String in_str2 = "12:35";

        assertEquals(0, maker.isDuration(in_str1, in_str2));

    }


    @Test
    public void durationToMinutes_Test_WRONG_FORMAT() {

        makerTemp.setMeeting_duration("NO DATE FORMAT");   // nie moze przeparsowac i rzuca wyjatek
        assertEquals(-1, makerTemp.durationToMinutes());
    }

    @Test
    public void durationToMinutes_Test_NULL() {

        makerTemp.setMeeting_duration(null);            // nie moze przeparsowac i rzuca wyjatek
        assertEquals(-1, makerTemp.durationToMinutes());
    }

    @Test
    public void durationToMinutes_Test_EXAMPLE() {

        makerTemp.setMeeting_duration("2:10");
        Date date = new Date();
        try {
            date = makerTemp.getFormat().parse(makerTemp.formatTime(makerTemp.getMeeting_duration()));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        long expected = (date.getTime() / 60000 + 60);
        assertEquals(expected, makerTemp.durationToMinutes());
    }

    @Test
    public void formatTime_Test_ZERO() {
        String result = maker.formatTime("");
        assertEquals("PARSE ERROR", result);
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
