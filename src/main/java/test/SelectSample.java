package test;

import com.itss.t24runtime.Record;
import com.itss.t24runtime.Record.Field;
import com.itss.t24runtime.T24Runtime;
import com.itss.t24runtime.T24Standalone;

import java.util.List;

public class SelectSample {

    static String TAFJ_HOME = "D:\\mbr21\\TAFJ";

    public static void main(String[] args) {
        T24Standalone.run(TAFJ_HOME, MainSubTest.class);
    }

    public static class MainSubTest {
        public static void main(String[] args) {

            try (T24Runtime runtime = T24Runtime.getNotInitialized()) {

                for (String recId : runtime.select("F.VERSION")) {
                    if (recId.matches("^.*,.*\\.API\\..*\\.\\d+\\.\\d+\\.\\d+$")) {

                        Record record = runtime.readRecord("F.VERSION", recId);

                        Field id = record.get("@ID");
                        System.out.println(id);

                        List<Field> fieldNos = record.get("FIELD.NO").asListVm();
                        List<Field> texts = record.get("TEXT").asListVm();
                        List<Field> promptTexts = record.get("PROMPT.TEXT").asListVm();

                        int len = fieldNos.size();
                        for (int i = 0; i < len; i++) {

                            String text = i < texts.size() ? texts.get(i).toString() : "";
                            String promptText = i < promptTexts.size() ? promptTexts.get(i).toString() : "";

                            System.out.println(fieldNos.get(i) + " > " + text + " [" + promptText + "]");
                        }

                        System.out.println("--------------------------");


                    }
                }

            }

        }
    }

}
