package de.fhe.fhemobile.utils;

public class TimeTableUtils {

    /**
     * if there are more than one lecturer with this name the department e.g. SciTec is added in brackets
     * this prettifies the person's name if a whitespace between name and bracket is missing
     * @param person string already corrected for umlauts
     * @return name string
     */
    public static String prettifyName(String person){
        //method added by Nadja - 05.01.2021

        if (person.matches("[a-zA-Z- ,._]+[^ ][(][a-zA-Z-]+[)]")){
            person = person.replaceFirst("[(]", " (");
        }
        return person;
    }

    /**
     * Replaces incorrect german umlauts in the given string
     * @param str name as string
     * @return corrected name string
     */
    public static String correctUmlauts(String str){
        //method added by Nadja - 05.01.2021

        str = str.replaceAll("Ã„", "Ä");
        str = str.replaceAll("Ã\u009C", "Ü");
        str = str.replaceAll("Ã–", "Ö");
        str = str.replaceAll("Ã\u009F", "ß");
        str = str.replaceAll("Ã¼", "ü");
        str = str.replaceAll("Ã¶", "ö");
        str = str.replaceAll("Ã¤", "ä");

        return str;
    }
}
