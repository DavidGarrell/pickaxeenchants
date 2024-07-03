package de.pickaxeenchants.api;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.math.RoundingMode;

public class Format {
    public String format(Apfloat zahl) {
        String[] prefixes = {"", "k", "m", "b", "t", "aa", "ab"};
        int index = 0;

        Apfloat n = new Apfloat(String.valueOf(ApfloatMath.roundToInteger(zahl, RoundingMode.UP)));
        n.truncate();

        while (n.compareTo(new Apfloat("1000")) >= 0 && index < prefixes.length - 1) {
            n = n.divide(new Apfloat("1000"));
            index++;
        }

        String prefix = prefixes[index];

        // Verhindern Sie die wissenschaftliche Schreibweise
        String formattedNumber = n.toString(true);

        String formattedDNumber = new Apfloat(String.valueOf(ApfloatMath.roundToInteger(zahl, RoundingMode.UP))).toString();

        if (formattedNumber.length() == 3) {
            formattedNumber = formattedNumber.substring(0, 3);
        } else if(formattedNumber.length() == 2){
            formattedNumber = formattedNumber.substring(0, 2);
        }
        else if(formattedNumber.length() == 1){
            formattedNumber = formattedNumber.substring(0, 1);
        }



        return formattedNumber + prefix;
    }
}
