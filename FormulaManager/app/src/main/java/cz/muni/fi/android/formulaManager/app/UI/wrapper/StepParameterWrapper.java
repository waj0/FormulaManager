package cz.muni.fi.android.formulaManager.app.UI.wrapper;

import android.app.Activity;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.android.formulaManager.app.entity.Parameter;

/**
 * Created by Slavomír on 30.4.2014.
 */
public class StepParameterWrapper extends ParameterWrapper{
    private TextView startDescription;
    private EditText startValue;
    private TextView endDescription;
    private EditText endValue;
    private TextView stepDescription;
    private EditText stepValue;


    public StepParameterWrapper(Activity activity, ViewGroup calculationParametersLayout, Parameter parameter) {
        if(parameter.getType() != Parameter.ParameterType.STEP) {
            throw new IllegalArgumentException("Wrong type of parameter!");
        }

        startDescription= new TextView(activity);
        styleDescText(startDescription,"Lowest value of parameter " + parameter.getName());
        calculationParametersLayout.addView(startDescription);
        startValue = new EditText(activity);
        styleEditText(startValue);
        calculationParametersLayout.addView(startValue);

        endDescription= new TextView(activity);
        styleDescText(endDescription,"Highest value of parameter " + parameter.getName());
        calculationParametersLayout.addView(endDescription);
        endValue = new EditText(activity);
        styleEditText(endValue);
        calculationParametersLayout.addView(endValue);

        stepDescription= new TextView(activity);
        styleDescText(stepDescription,"Step size of parameter " + parameter.getName());
        calculationParametersLayout.addView(stepDescription);
        stepValue = new EditText(activity);
        styleEditText(stepValue);
        calculationParametersLayout.addView(stepValue);
    }
    public static void styleEditText(EditText editText){
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }


    public List<Double> getValues() {
        List<Double> list = new ArrayList<Double>();

        Double start = parseDoubleValue(startValue);
        Double  end = parseDoubleValue(endValue);
        Double step = parseDoubleValue(stepValue);
        if((start>end)|| start+step>end || step <0) {
            throw new IllegalArgumentException("Illegal values in step parameter ");
        } else
        {
            for(double i = start; i<end; i+=step){
                list.add(i);
            }
        }

        return list;
    }

    public static void styleDescText(TextView textView,Parameter parameter){
        textView.setText(parameter.getName());

    }

}