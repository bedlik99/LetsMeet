package DataModel.ModelClasses;

import java.util.ArrayList;
import java.util.List;

public class WorkersCalendar {

        /**
         * Zmienna mowiaca o waznych czasach startowych i koncowych - tutaj czas pracy pracownika w danym dniu
         */
        private ImportantHours working_hours = new ImportantHours();
        /**
         * Zaplanowane spotkania pracownika (wazne okresy czasowe)
         */
        private List<ImportantHours> planned_meeting = new ArrayList<>();


        // Getters
        public ImportantHours getWorking_hours() {
                return working_hours;
        }

        public List<ImportantHours> getPlanned_meeting() {
                return planned_meeting;
        }

}
