package cz.muni.fi.android.formulaManager.app.UI.wrapper;

import android.app.Activity;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
        startValue = new EditText(activity);
        styleEditText(startValue);

        endDescription= new TextView(activity);
        styleDescText(endDescription,"Highest value of parameter " + parameter.getName());
        endValue = new EditText(activity);
        styleEditText(endValue);

        stepDescription= new TextView(activity);
        styleDescText(stepDescription,"Step size of parameter " + parameter.getName());
        stepValue = new EditText(activity);
        styleEditText(stepValue);
    }
    public static void styleEditText(EditText editText){
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
    public static void styleDescText(TextView textView,Parameter parameter){
        textView.setText(parameter.getName());

    }

}