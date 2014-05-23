package cz.muni.fi.android.formulaManager.app.utils;

import org.matheclipse.parser.client.eval.DoubleEvaluator;

import java.util.List;
import java.util.Map;

/**
 * Created by Slavomír on 22.5.2014.
 */
public final class Compute {

    private static final DoubleEvaluator engine = new DoubleEvaluator();

    public synchronized static String  compute(String rawFormula,List<Map<String, Double>> parameters){

        StringBuilder builder = new StringBuilder();
        for (Map<String, Double> parameter : parameters) {
            builder.append(compute(rawFormula,parameter)).append("\n");
        }
        if(builder.length()>1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
    public static Double compute(String rawFormula,Map<String,Double> parameters){
        engine.clearVariables();
        if(parameters!= null) {
            for (String paramName : parameters.keySet()) {
                engine.defineVariable(paramName, parameters.get(paramName));
            }
        }
        return engine.evaluate(rawFormula);
    }

    public synchronized static boolean checkIfValid(String rawFormula,List<String> parameters){
        engine.clearVariables();
        if(parameters!= null) {
            for (String paramName : parameters) {
                engine.defineVariable(paramName);
            }
        }
        try {
            engine.evaluate(rawFormula);
            return true;
        } catch (Exception ex){
            return false;
        }

    }
}
