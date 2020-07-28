package DataModel;

import DataModel.ModelClasses.DurationOfAppointment;
import DataModel.ModelClasses.WorkersCalendar;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Klasa abstrakcyjna, ze wszystkimi polami statycznymi. Sluzaca do wczytania plikow o formacie *.json i przypisania
 * ich do odpowiednich zmiennych statycznych, do ktorych bedziemy miec latwy dostep.
 */
public abstract class Data {

    private static ObjectMapper mapper = new ObjectMapper();

    private static JSONParser jsonParser = new JSONParser();

    private static WorkersCalendar calendar1, calendar2;

    private static DurationOfAppointment durationOfAppointment;

    public static JsonNode readJson(String src) throws IOException{
        return mapper.readTree(src);
    }


    public static <A> A fromJson(JsonNode node, Class<A> clazz) throws JsonProcessingException {
        return mapper.treeToValue(node, clazz);
    }


    public static DurationOfAppointment getDurationOfAppointment() {
        return durationOfAppointment;
    }

    public static WorkersCalendar getCalendar1() {
        return calendar1;
    }

    public static WorkersCalendar getCalendar2() {
        return calendar2;
    }


    /**
     * Metoda wczytujaca dane wejsciowe z konkretnych plikow wejsciowych
     * @param fileName Nazwa pliku wejsciowego
     */
    public static void loadCalendar(String fileName)  {


        try {
            URL res = Data.class.getResource("/" + fileName);
            File file = Paths.get(res.toURI()).toFile();
            String toLoadFile = file.getAbsolutePath();

            FileReader jsonFile = new FileReader(toLoadFile);
            JSONObject obj = (JSONObject) jsonParser.parse(jsonFile);
            String jsonString = obj.toString();
            JsonNode node = readJson(jsonString);


            if (fileName.equals("Calendar1.json")) {
                calendar1 = fromJson(node, WorkersCalendar.class);
            } else if (fileName.equals("Calendar2.json")) {
                calendar2 = fromJson(node, WorkersCalendar.class);
            }else if(fileName.equals("TimeDuration.json")){
                durationOfAppointment = fromJson(node, DurationOfAppointment.class);
            }

        } catch (ParseException | URISyntaxException |IOException e) {

            e.printStackTrace();
        }


    }


}
