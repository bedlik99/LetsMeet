package DataModel.ModelClasses;

public class DurationOfAppointment {

    /**
     * String mowiacy o czasie trwania spotkania. Zainicjalizowany w momencie wczytania pliku wejsciowego.
     */
    private String meeting_duration;


    public String getMeeting_duration() {
        return meeting_duration;
    }

    public void setMeeting_duration(String meeting_duration) {
        this.meeting_duration = meeting_duration;
    }
}
