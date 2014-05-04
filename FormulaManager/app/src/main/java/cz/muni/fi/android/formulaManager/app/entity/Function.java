package cz.muni.fi.android.formulaManager.app.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Martin on 4. 5. 2014.
 */
public class Function {

    /* Helper for some functions */
    private static final String DERIVATION_TOAST = "Format of derivation function is " +
            "D[ function , variable ]. \n" +
            "e.g D[x^2,x]";
    private static final String INTEGRATE_TOAST = "Format of integral function is " +
            "Integrate[ function , variable ]. \n" +
            "e.g Integrate[x^(-1),x]";
    private static final String LIMIT_TOAST = "Format of limit function is " +
            "Limit[ function , variable -> value which variable approaches to ]\n" +
            "e.g. Limit[Sin[Pi*x]/(Pi*x),x->0]";
    private static final String SUM_TOAST = "Format of sum function is " +
            "Sum[ function , {variable, [optional start], end , [optional step]}\n" +
            "e.g Sum[i,{i,10}] = 55 , start = 1 , step = 1 \n" +
            "e.g Sum[x,{x,1,1}] = 1 , step = 1 \n" +
            "e.g.Sum[x,{x,3,2,-1}] = 5";
    private static final String BINOMIAL_TOAST = "Format of binomial coefficient is \n" +
            "Binomial[n,k], n choose k \n" +
            "Binomial[10,3] = 120";
    private static final String SIMPLIFY_TOAST = "Simplify function will simplify given expression \n" +
            "e.g Simplify[1/2*(2*x+2)] = x+1";
    private static final String GCD_TOAST = "Format of greater common divisor is \n" +
            "GCD[x,y,...], you can put more than 2 values to GCD\n" +
            "e.g GCD[6,27] = 3 \n" +
            "e.g GCD[12,54,66] = 6";
    private static final String LCM_TOAST = "Format of least common multiple is \n" +
            "LCM[x,y,...], you can put more than 2 values to LCM \n" +
            "e.g LCM[4,3] = 12 \n" +
            "e.g LCM[14,6,9] = 126";


    private static final Map<String, FunctionHelper> functions;

    static {
        Map<String, FunctionHelper> map = new HashMap<String, FunctionHelper>();

        /* Constants */
        map.put("pi", new FunctionHelper("Pi"));
        map.put("e", new FunctionHelper("E"));
        map.put("false", new FunctionHelper("False"));
        map.put("true", new FunctionHelper("True"));
        map.put("degree", new FunctionHelper("Degree"));
        map.put("golden ratio", new FunctionHelper("GoldenRatio"));
        map.put("infinity", new FunctionHelper("Infinity"));

        /* Trigonometric functions */
        map.put("sine", new FunctionHelper("Sin[]"));
        map.put("cosine", new FunctionHelper("Cos[]"));
        map.put("tangent", new FunctionHelper("Tan[]"));
        map.put("cotangent", new FunctionHelper("Cot[]"));
        map.put("arc sine", new FunctionHelper("ArcSin[]"));
        map.put("arc cosine", new FunctionHelper("ArcCos[]"));
        map.put("arc tangent", new FunctionHelper("ArcTan[]"));
        map.put("arc cotangent", new FunctionHelper("ArcCot[]"));

        /* Relational operators */
        map.put("equal to", new FunctionHelper("=="));
        map.put("not equal to", new FunctionHelper("!="));
        map.put("greater than", new FunctionHelper(">"));
        map.put("less than", new FunctionHelper("<"));
        map.put("greater than or equal to", new FunctionHelper(">="));
        map.put("less than or equal to", new FunctionHelper("<="));

        /* Rounding */
        map.put("ceiling", new FunctionHelper("Ceiling[]"));
        map.put("floor", new FunctionHelper("Floor[]"));
        map.put("round", new FunctionHelper("Round[]"));

        /* Analysis */
        map.put("derivation", new FunctionHelper("D[,]",DERIVATION_TOAST));
        map.put("integrate", new FunctionHelper("Integrate[,]",INTEGRATE_TOAST));
        map.put("limit", new FunctionHelper("Limit[, -> ]",LIMIT_TOAST));

        /* General Functions */
        map.put("sum", new FunctionHelper("Sum[, {,,,,}]",SUM_TOAST));
        map.put("exponential function", new FunctionHelper("Exp[]"));
        map.put("logarithm", new FunctionHelper("Log[]"));
        map.put("binomial", new FunctionHelper("Binomial[,]",BINOMIAL_TOAST));
        map.put("simplify", new FunctionHelper("Simplify[]",SIMPLIFY_TOAST));
        map.put("absolute", new FunctionHelper("Abs[]"));
        map.put("factorial", new FunctionHelper("!"));

        /* Divisor and Multiple*/
        map.put("great common divisor", new FunctionHelper("GCD[,]",GCD_TOAST));
        map.put("least common multiple", new FunctionHelper("LCM[,]", LCM_TOAST));

        functions = Collections.unmodifiableMap(map);
    }

    private Function() {

    }

    public static final FunctionHelper getFunction(String functionName) {
        return functions.get(functionName);
    }

}
