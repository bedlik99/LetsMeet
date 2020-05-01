package LetsMeet;

import WorkerClasses.AppointmentMaker;
import java.text.ParseException;


public class Main {

    public static void main(String[] args) throws ParseException {
        AppointmentMaker appointmentMaker = new AppointmentMaker("Calendar1.json","Calendar2.json","TimeDuration.json");
        appointmentMaker.showAvailableTimes();
    }


}
