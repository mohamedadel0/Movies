package com.example.muhammad_adel.final_project_phase1_version2;

/**This class to convert string to array or array to string so i can put array in data base after convert it to string like trailers array and reviews array**/

public class ConvertTypes {
    public static String separator = "_,_";
    public static String convertArrayToString (String[] array) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            result += array[i];
            if (i < array.length - 1) {
                result += separator;
            }
        }
        return result;

    }
    public static String[] convertStringToArray (String str) {
        if (!(str.contains(separator))) {
           String[] arrFromString = new String[] {str};
            return arrFromString;
        } else {
            String arrFromString[] = str.split(separator);
            return arrFromString;
        }

    }
}
