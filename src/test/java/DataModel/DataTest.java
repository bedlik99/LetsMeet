package DataModel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class DataTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Testuje podanie zlej nazwy pliku do wczytania(np. zla nazwa kalendarza pracownika)
     */
    @org.junit.Test(expected = NullPointerException.class)
    public void loadCalendar_NoSuchFileTest()  {
        String stringToTest = "Calendar2.json";
        Data.loadCalendar(stringToTest);  // Tutaj pierwsze sprawdzenie funkcji dla prawidlowej nazwy - brak wyrzucenia wyjatku

        stringToTest = "Calendar00bad.json";
        Data.loadCalendar(stringToTest); // nie ma takiego pliku - wyrzucony nullpointer
    }

    /**
     * Testujemy pusty String, w tym przypadku jest to rownowazne z wybraniem calego katalogu. ( Powinien zostac wybrany plik.json)
     * @throws IOException wyjatek
     */
    @org.junit.Test(expected = FileNotFoundException.class)
    public void loadCalendar_NoFileName() throws IOException {
        String emptyString = "";
        //exceptionRule.expect(NullPointerException.class);
        Data.loadCalendar(emptyString);
    }

    /**
     * Przekazujemy nulla jako parametr i oczekujemy wyrzucenia wyjatku
     */
    @org.junit.Test(expected = NullPointerException.class)
    public void loadCalendar_NullPointer()  {
        Data.loadCalendar(null);
    }




}