package cz.muni.fi.android.formulaManager.app.UI.wrapper;

import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Slavomír on 30.4.2014.
 */
public class ParameterWrapper {

    protected static void styleEditText(EditText editText){
        // TODO play with styling
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
    protected static void styleDescText(TextView textView,String description){
        // TODO play with styling
        textView.setText(description);
    }
}
