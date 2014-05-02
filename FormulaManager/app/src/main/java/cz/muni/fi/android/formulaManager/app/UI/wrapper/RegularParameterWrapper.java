package cz.muni.fi.android.formulaManager.app.UI.wrapper;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import cz.muni.fi.android.formulaManager.app.entity.Parameter;

/**
 * Created by Slavomír on 30.4.2014.
 */
public class RegularParameterWrapper extends ParameterWrapper{
    private TextView description;
    private EditText valueField;

    public RegularParameterWrapper(Activity activity,ViewGroup calculationParametersLayout,Parameter parameter) {
        if(parameter.getType() != Parameter.ParameterType.REGULAR) {
            throw new IllegalArgumentException("Wrong type of parameter!");
        }
        description= new TextView(activity);
        styleDescText(description,parameter.getName());
        calculationParametersLayout.addView(description);
        valueField = new EditText(activity);
        styleEditText(valueField);
        calculationParametersLayout.addView(valueField);
    }
}
