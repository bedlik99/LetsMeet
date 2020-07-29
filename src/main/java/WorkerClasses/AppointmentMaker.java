package WorkerClasses;

import DataModel.Data;
import DataModel.ModelClasses.ImportantHours;
import com.sun.security.auth.NTUserPrincipal;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Klasa tworzaca mozliwe spotkania pracownikow, na podstawie ich kalendarzy.
 */
public class AppointmentMaker {

    /**
     * Czas trwania spotkania
     */
    private String meeting_duration;

    /**
     * Czas pracy 1-szego pracownika
     */
    private final ImportantHours working_hours1;

    /**
     * Lista zaplanowanych juz spotkan 1-szego pracownika
     */
    private final List<ImportantHours> planned_meeting1;

    /**
     * Lista wolnych przedzialow czasowych 1-szego pracownika - stworzona na podstawie kalendarza pracownika
     */
    private final List<ImportantHours> freeTime1 = new ArrayList<>();

    /**
     * Czas pracy 2-giego pracownika
     */
    private final ImportantHours working_hours2;

    /**
     * Lista zaplanowanych juz spotkan 2-giego pracownika
     */
    private final List<ImportantHours> planned_meeting2;

    /**
     * Lista wolnych przedzialow czasowych 2-giego pracownika - stworzona na podstawie kalendarza pracownika
     */
    private final List<ImportantHours> freeTime2 = new ArrayList<>();

    /**
     * Lista czasow w ktorych pracownicy moga sie spotkac - biorac pod uwage ich kalendarz w czasie dnia
     */
    private final List<ImportantHours> availableTimes = new ArrayList<>();
    /**
     * Obiekt pozwalajacy na ustawienie formatu czasu
     */
    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    /**
     * Czas trwania spoktania podany w minutach
     */
    private long durationInMinutes;


    /**
     * Konstruktor klasy AppointmentMaker pozwalajacy na wczytanie plikow wejsciowych oraz zainicjalizowanie zmiennych
     *
     * @param calendar1  Nazwa pliku z kalendarzaem 1-szego pracownika
     * @param calendar2  Nazwa pliku kalendarzem 2-ego pracownika
     * @param meetingDur Nazwa pliku z informacja o dlugosci spotkania
     */
    public AppointmentMaker(String calendar1, String calendar2, String meetingDur) {

        Data.loadCalendar(calendar1);
        Data.loadCalendar(calendar2);
        Data.loadCalendar(meetingDur);

        meeting_duration = Data.getDurationOfAppointment().getMeeting_duration().substring(1, 5);
        durationInMinutes = durationToMinutes();

        working_hours1 = Data.getCalendar1().getWorking_hours();
        planned_meeting1 = Data.getCalendar1().getPlanned_meeting();

        working_hours2 = Data.getCalendar2().getWorking_hours();
        planned_meeting2 = Data.getCalendar2().getPlanned_meeting();
    }

    /**
     * Metoda pracujaca na listach zawierajacych okresy czasowe, w ktorych pracownicy maja wolne
     * Metoda wyszukuje czasy rozpoczecia wolnych okresow u obu osob i przy pomocy innych funkcji posrednio
     * tworzy Liste odpowiednich czasow spotkan
     */
    public void showAvailableTimes() {
        // Wypelniam zmienne freeTime1, freeTime2 - wolnymi przedzialami czasu odpowiednich pracownikow
        makeFreeTimeSchedule(working_hours1, planned_meeting1, freeTime1);
        makeFreeTimeSchedule(working_hours2, planned_meeting2, freeTime2);
        List<ImportantHours> shorterSchedule = freeTime1.size() <= freeTime2.size() ? freeTime1 : freeTime2;
        List<ImportantHours> longerSchedule = freeTime1.size() <= freeTime2.size() ? freeTime2 : freeTime1;
        long howManyMeetings;

        for (int i = 0; i <= shorterSchedule.size() - 1; i++) {
            // Ktorego pracownika wolny czas zaczyna sie wczesniej?
            if (shorterSchedule.get(i).getStart().compareTo(longerSchedule.get(i).getStart()) <= 0) {
                // Czy start wolnego czasu, dla pracownika ktory zaczal pozniej, rozpoczyna sie przed koncem
                // wolnego czasu pierwszego pracownika?
                if (longerSchedule.get(i).getStart().compareTo(shorterSchedule.get(i).getEnd()) < 0) {
                    // Obaj pracownicy maja wolny czas. Czy wystarczy czasu na pelne spotkanie?
                    if ((howManyMeetings = isDuration(longerSchedule.get(i).getStart(), shorterSchedule.get(i).getEnd())) != 0) {
                        addPossibleMeetingTime(howManyMeetings, longerSchedule.get(i).getStart());
                    }

                }

            } else {
                if (shorterSchedule.get(i).getStart().compareTo(longerSchedule.get(i).getEnd()) < 0) {
                    if ((howManyMeetings = isDuration(shorterSchedule.get(i).getStart(), longerSchedule.get(i).getEnd())) != 0) {
                        addPossibleMeetingTime(howManyMeetings, shorterSchedule.get(i).getStart());
                    }

                }
            }
        }


        System.out.println("\n--------------------------------");
        System.out.println("Wolne terminy w ktorych pracownicy moga sie spotkac: ");
        for (ImportantHours hours : availableTimes) {
            System.out.print("[\"" + hours.getStart() + "\",\"" + hours.getEnd() + "\"], ");
        }
        System.out.println();

    }

    /**
     * Metoda inicjalizujaca liste 'availableTimes' z okresami czasowymi w ktorych pracownicy moga odbyc spotkanie
     * @param howManyMeetings Liczba calkowita okreslajaca ile razy okresy spotkan zmieszcza sie w konkretnym przedziale czasowym
     * @param start           String okreslajacy czas rozpoczecia
     */
    public void addPossibleMeetingTime(long howManyMeetings, String start) {

            String earlierWorkingHours = working_hours1.getEnd().compareTo(working_hours2.getEnd()) <= 0 ? working_hours1.getEnd() : working_hours2.getEnd();
            String expectedEnd = formatTime(addDurationToTime(start, howManyMeetings));

            if (expectedEnd.compareTo(earlierWorkingHours) < 0) {
                availableTimes.add(new ImportantHours(start, expectedEnd));
            } else {
                availableTimes.add(new ImportantHours(start, earlierWorkingHours));
            }
    }

    /**
     * Funkcja zwracajaca czas koncowy dla mozliwego do spotkania okresu czasowego.
     * Do czasu startowego dodaje okres CALKOWITA wielokrotnosc trwania spotkania,
     * ktora jest w stanie sie zmiescic w danym okresie czasowym
     *
     * @param time    Czas startowy
     * @param howMany Calkowita liczba, mowiaca o calkowitych wielokrotnosciach czasu trwania spotkania
     * @return Czas koncowy
     */
    public String addDurationToTime(String time, long howMany) {

        try {
            Date date = format.parse(time);
            long timeInMinutes = (date.getTime() / 60000) + 60;
            long newTimeInMinutes = timeInMinutes + durationInMinutes * howMany;

            return (newTimeInMinutes / 60 + ":" + newTimeInMinutes % 60);

        }catch (ParseException | NullPointerException e){
            System.out.println(e.getMessage());
            return "PARSE ERROR";
        }


    }

    /**
     * Funkcja wyliczajaca czy w danych przedzialach czasowych mozliwe jest stworzenie spotkania o zadanej dlugosci
     *
     * @param time1 Czas startowy
     * @param time2 Czas koncowy
     * @return zwaraca calkowita wielokrotnosc(zaokroglana do calosci) ; np. >1 to znaczy ze uda sie utworzyc spotkanie , 0 - znaczy ze nie uda sie
     */
    public long isDuration(String time1, String time2) {
        try {
            Date date1 = format.parse(time1);
            Date date2 = format.parse(time2);

            long firstTime = (date1.getTime() / 60000) + 60; // w minutach
            long secondTime = (date2.getTime() / 60000) + 60; // w minutach

            long newTime = secondTime - firstTime;
            long howManyMeetings = newTime / durationInMinutes;

            if (newTime >= durationInMinutes) {
                return howManyMeetings;
            }

        } catch (ParseException | NullPointerException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return 0; // zero spotkan
    }

    /**
     * Funkcja wyliczajaca minuty z podanego czasu trwania spotkania
     *
     * @return Czas trwania spotkania w minutach
     */
    public long durationToMinutes() {

        try {
            Date date = format.parse(formatTime(meeting_duration));
            return (date.getTime() / 60000 + 60); // w minutach
        } catch (ParseException e) {

            System.out.println(e.getMessage());
            return -1L;
        }

    }


    /**
     * Funkcja formatujaca czas na odpowiednio wypelniony zerami
     *
     * @param calendarTime dowolny czas w formacie HH:mm
     * @return sformatowany string czasu
     */
    public String formatTime(String calendarTime) {

        if (calendarTime == null)
            return "UNKNOWN FORMAT";

        if (calendarTime.trim().length() != 5) {

            if (calendarTime.matches("[0-9]:[0-9]")) {
                calendarTime = "0" + calendarTime + "0";
                return calendarTime;

            } else if (calendarTime.length() == 3) {
                return "PARSE ERROR";

            } else if (calendarTime.matches("[0-9]:[0-9][0-9]")) {
                calendarTime = "0" + calendarTime;
                return calendarTime;

            } else if (calendarTime.matches("[0-9][0-9]:[0-9]")) {
                calendarTime = calendarTime + "0";
                return calendarTime;

            }

                return "PARSE ERROR";

        }

        if(calendarTime.matches("[0-9][0-9]:[0-9][0-9]"))
        return calendarTime;


        return "PARSE ERROR";
}


    /**
     * Funkcja tworzy liste z czasami ktore odpowiadaja przedzialom WOLNEGO czasu pracownika w danym dniu
     * Dtworzona lista jest wykorzystywana do znalezienia czasu wolnego dla obu pracownikow i o odpowiedniej dlugosci czasowej.
     *
     * @param working_hours   Czas trwania pracy pracownika
     * @param planned_meeting Okresy czasowe na ktorych zaplanowane są juz spotkania pracownika
     * @param freeTime        Okresy czasowe wolne od pracy  danego pracownika
     */
    private void makeFreeTimeSchedule(ImportantHours working_hours,
                                      List<ImportantHours> planned_meeting, List<ImportantHours> freeTime) {

        //Szukamy wolnego czasu miedzy czasem rozpoczecia pracy i pierwszym terminem zaplanowanym
        if (!working_hours.getStart().equals(planned_meeting.get(0).getStart()) &&
                isDuration(working_hours.getStart(), planned_meeting.get(0).getStart()) != 0) {
            freeTime.add(new ImportantHours(working_hours.getStart(), planned_meeting.get(0).getStart()));
        }

        // W pętli szukane są wolne czasy miedzy terminami zaplanowanymi
        for (int i = 0; i <= planned_meeting.size() - 2; i++) {
            if (!planned_meeting.get(i).getEnd().equals(planned_meeting.get(i + 1).getStart())
                    && isDuration(planned_meeting.get(i).getEnd(), planned_meeting.get(i + 1).getStart()) != 0) {

                freeTime.add(new ImportantHours(planned_meeting.get(i).getEnd(), planned_meeting.get(i + 1).getStart()));
            }
        }

        //Szukamy wolnego czasu miedzy czasem zakonczenia ostatniego zaplanowanego spotkania
        // a zakonczeniem pracy w danym dniu
        if (!planned_meeting.get(planned_meeting.size() - 1).getEnd().equals(working_hours.getEnd())
                && isDuration(planned_meeting.get(planned_meeting.size() - 1).getEnd(), working_hours.getEnd()) != 0) {

            freeTime.add(new ImportantHours(planned_meeting.get(planned_meeting.size() - 1).getEnd(), working_hours.getEnd()));
        }

    }

    // LISTA GETTEROW/SETTEROW - UZYWANE W TESTACH

    public void setMeeting_duration(String meetDur){
        meeting_duration = meetDur;
    }

    public String getMeeting_duration() {
        return meeting_duration;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public ImportantHours getWorking_hours1() {
        return working_hours1;
    }

    public List<ImportantHours> getPlanned_meeting1() {
        return planned_meeting1;
    }

    public List<ImportantHours> getFreeTime1() {
        return freeTime1;
    }

    public ImportantHours getWorking_hours2() {
        return working_hours2;
    }

    public List<ImportantHours> getPlanned_meeting2() {
        return planned_meeting2;
    }

    public List<ImportantHours> getFreeTime2() {
        return freeTime2;
    }

    public List<ImportantHours> getAvailableTimes() {
        return availableTimes;
    }

    public SimpleDateFormat getFormat() {
        return format;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }


}











