package cz.muni.fi.android.formulaManager.app.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Slavomír on 22.5.2014.
 */
public class CombinationUtils {


    public static List<Map<String, Double>> makeCombinations(Map<String, Double> oneCombination, List<Double> stepParameterValues, String stepParamName) {
        List<Map<String, Double>> combinations = new ArrayList<Map<String, Double>>();
        for (Double stepParameterValue : stepParameterValues) {
            Map<String, Double> combination = new HashMap<String, Double>(oneCombination);
            combination.put(stepParamName,stepParameterValue);
            combinations.add(combination);
        }

        return combinations;
    }
}
