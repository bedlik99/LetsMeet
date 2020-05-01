package DataModel.ModelClasses;

public class ImportantHours {

    /**
     * Czas rozpoczynajacy wazny okres czasowy
     */
    private String start;
    /**
     * Czas konczacy, wazy okres czasowy
     */
    private String end;


    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public ImportantHours(){}
    public ImportantHours(String start, String end){
        this.start = start;
        this.end = end;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
