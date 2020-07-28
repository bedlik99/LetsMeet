package LetsMeet;

import WorkerClasses.AppointmentMaker;

public class Main {

    public static void main(String[] args) {
        AppointmentMaker appointmentMaker = new AppointmentMaker("Calendar1.json","Calendar2.json","TimeDuration.json");
        appointmentMaker.showAvailableTimes();
    }

}
