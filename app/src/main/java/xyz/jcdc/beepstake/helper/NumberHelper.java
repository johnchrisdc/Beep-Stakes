package xyz.jcdc.beepstake.helper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by jcdc on 2/18/17.
 */

public class NumberHelper {

    public static String formatNumber(float number){
        DecimalFormat df = new DecimalFormat("#.##");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(Double.valueOf(df.format(number)));
    }

}
