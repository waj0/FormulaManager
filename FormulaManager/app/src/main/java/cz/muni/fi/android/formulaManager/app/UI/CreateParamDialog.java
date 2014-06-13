package cz.muni.fi.android.formulaManager.app.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cz.muni.fi.android.formulaManager.app.R;
import cz.muni.fi.android.formulaManager.app.entity.Parameter;

/**
 * Created by Majo on 13. 4. 2014.
 */
public class CreateParamDialog extends DialogFragment {

    public interface CreateParamDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        //TODO maybe we dont need negative method of this interface
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    CreateParamDialogListener mListener;

    Parameter.ParameterType selectedType;
    String paramName;


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CreateParamDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.create_param_dialog, null);

        Bundle arguments = getArguments();
        Parameter parameter = arguments.getParcelable("parameter");

        final EditText name = (EditText) dialog.findViewById(R.id.param_name);
        setSelectedType(Parameter.ParameterType.REGULAR);

        if (parameter != null) {
            name.setText(parameter.getName());
            Log.i(CreateParamDialog.class.getName(), parameter.toString());
            setSelectedType(parameter.getType());

            setCheckedParameterType(parameter,dialog);
        }

        RadioButton r = (RadioButton) dialog.findViewById(R.id.radioButton1);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedType(Parameter.ParameterType.REGULAR);
            }
        });
//        r = (RadioButton) dialog.findViewById(R.id.radioButton2);
//        r.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setSelectedType(Parameter.ParameterType.INDEX);
//            }
//        });
        r = (RadioButton) dialog.findViewById(R.id.radioButton3);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedType(Parameter.ParameterType.STEP);
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialog)
                .setTitle(R.string.title_param_create)
                .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        setParamName(name.getText().toString());
                        mListener.onDialogPositiveClick(CreateParamDialog.this);
                    }
                })
                .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(CreateParamDialog.this);
                    }
                });
        return builder.create();
    }

    private void setCheckedParameterType(Parameter parameter, View dialog) {
        RadioGroup group = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        switch (parameter.getType()) {
            case REGULAR:
                group.check(cz.muni.fi.android.formulaManager.app.R.id.radioButton1);
                break;
//            case INDEX:
//                group.check(R.id.radioButton2);
//                break;
            case STEP:
                group.check(R.id.radioButton3);
                break;
        }
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Parameter.ParameterType getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(Parameter.ParameterType selectedType) {
        this.selectedType = selectedType;
    }
}
