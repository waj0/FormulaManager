package cz.muni.fi.android.formulaManager.app.UI;


import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParseException;
import com.larvalabs.svgandroid.SVGParser;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.parser.client.math.ArithmeticMathException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.muni.fi.android.formulaManager.app.entity.Formula;
import cz.muni.fi.android.formulaManager.app.entity.Parameter;
import cz.muni.fi.android.formulaManager.app.R;
import cz.muni.fi.android.formulaManager.app.UI.wrapper.ParameterWrapper;
import cz.muni.fi.android.formulaManager.app.UI.wrapper.RegularParameterWrapper;
import cz.muni.fi.android.formulaManager.app.UI.wrapper.StepParameterWrapper;
import cz.muni.fi.android.formulaManager.app.utils.CombinationUtils;
import cz.muni.fi.android.formulaManager.app.utils.Compute;

/**
 * Created by Slavomír on 10. 4. 2014.
 */
public class CalculationFragment extends Fragment {

    private static final String TAG = "cz.fi.android.formulamanager.CalculationFragment";

    private Formula formula;
    private Map<String,RegularParameterWrapper> parametersMap;
    private StepParameterWrapper stepParameter;
    private String stepParamName;
    private static final ViewGroup.MarginLayoutParams MATCH_PARENT = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


    public CalculationFragment() {
        formula = new Formula();
        formula.setId(0l);
        formula.setName("");
        formula.setCategory("");
        formula.setParams(new ArrayList<Parameter>());
        formula.setRawFormula("");
        parametersMap = new HashMap<String, RegularParameterWrapper>(); {
        }
    }

    public CalculationFragment(Formula formula) {
        this.formula = formula;
        parametersMap = new HashMap<String, RegularParameterWrapper>();
    }
    //TODO formula not loading after context switch (from other map)
    //TODO button to share is visible if only list is on screen - vertical list fragment - we should remove it somehow in calculation fragment lifecycle

    public long getShownId() {

        return (formula == null)? null : formula.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(formula==null) {
            //TODO message add some formulas
            return  null;
        }
        View view  = inflater.inflate(R.layout.calculation_layout, container, false);

        TextView formulaName = (TextView) view.findViewById(R.id.calculation_formula_name);
        formulaName.setText(formula.getName());
        TextView formulaCategory= (TextView) view.findViewById(R.id.calculation_formula_category);
        formulaCategory.setText(formula.getCategory());


        Button button = (Button) view.findViewById(R.id.calculation_compute_button);
        button.setOnClickListener(new ComputeButtonListener(this));

        ViewGroup calculationFormulaContent = (ViewGroup)  view.findViewById(R.id.calculation_formula_content_layout);
        boolean useRawFormulaFlag = !formula.hasSvgFormula();
        Log.d(TAG, formula.hasSvgFormula() ? formula.getSvgFormula() : "null");
        if(!useRawFormulaFlag)
            try {
                    createFormulaImage(calculationFormulaContent);
                } catch (SVGParseException  ex) {
//                    svg is broken fallback to textview
                useRawFormulaFlag = true;
        }
        if(useRawFormulaFlag) {
            createFormulaText(calculationFormulaContent);
        }
        ViewGroup calculationParametersLayout = (ViewGroup)  view.findViewById(R.id.calculation_formula_parameters);
        for (Parameter parameter : formula.getParams()) {
            createParameterEditField(calculationParametersLayout, parameter);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO check this
        this.stepParamName = null;
        this.stepParameter = null;
        this.parametersMap.clear();
        Log.i(TAG,"onPause called");

    }

    private void createParameterEditField(ViewGroup calculationParametersLayout, Parameter parameter) {

        switch(parameter.getType()){

            case REGULAR:
                RegularParameterWrapper parameterWrapper = new RegularParameterWrapper(getActivity(),calculationParametersLayout,parameter);
                parametersMap.put(parameter.getName(),parameterWrapper);
                break;
            case STEP:

                if(stepParameter!= null) {
                    Log.e(TAG,"SECOND STEP PARAMETER!!");
                    //TODO should throw exception
                   // throw new RuntimeException("Second step parameter");
                }
                 stepParameter= new StepParameterWrapper(getActivity(),calculationParametersLayout,parameter);
                stepParamName = parameter.getName();
                break;
        }

    }

    private void createFormulaText(ViewGroup calculationFormulaContent) {
        TextView formulaText = new TextView(getActivity());
        formulaText.setText(formula.getRawFormula());
        formulaText.setLayoutParams(MATCH_PARENT);
        formulaText.setBackgroundColor(Color.WHITE);
        formulaText.setTextColor(Color.BLACK);
        formulaText.setTextSize(40);
        calculationFormulaContent.addView(formulaText);
    }

    private void createFormulaImage(ViewGroup calculationFormulaContent) {
        SVG svg = SVGParser.getSVGFromString(formula.getSvgFormula());
        ImageView formulaSvgImage = new ImageView(getActivity());
        resizeSvgToMatchParent(calculationFormulaContent, svg);
        formulaSvgImage.setImageDrawable(svg.createPictureDrawable());
        formulaSvgImage.setLayoutParams(MATCH_PARENT);
        calculationFormulaContent.addView(formulaSvgImage);
    }

    private void resizeSvgToMatchParent(ViewGroup calculationFormulaContent, SVG svg) {
        if( calculationFormulaContent.getWidth() == 0) {return;}
        RectF limits = svg.getLimits();
        float height = limits.height();
        float width = limits.width();
        float ratio = height/width;
        float maxHeight = dipToPixels(300);
        float maxWidth = calculationFormulaContent.getWidth();
        int computedWidth;
        int computedHeight;
        if(maxHeight*ratio > maxWidth) {
            computedHeight= (int) (maxWidth / ratio);
            computedWidth= (int) maxWidth;
        }
        else {
            computedHeight = (int) maxHeight;
            computedWidth = (int) (maxHeight * ratio);
        }
        svg.resizePicture(computedHeight,computedWidth);
    }

    private float dipToPixels(float dipValue) {
        DisplayMetrics metrics = getActivity().getBaseContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private class ComputeButtonListener implements View.OnClickListener {
        private CalculationFragment fragment;

        private ComputeButtonListener(CalculationFragment fragment) {
            this.fragment = fragment;
        }

            @Override
            public void onClick(View v)
            {
                fragment.compute();
            }

    }

    private void compute() {
        List<Map<String, Double>> params =  retrieveParams();
        if(params== null || params.isEmpty()) {
             return;
        }
        try {
            String resultString = Compute.compute(formula.getRawFormula(), params);
            TextView result = (TextView) this.getActivity().findViewById(R.id.calculation_result_text_view);
            result.setText(resultString);
        } catch
                (ArithmeticMathException ex ){
            Toast.makeText(getActivity(), "Wrong formula format!", Toast.LENGTH_LONG).show();
        }
    }

    private List<Map<String, Double>> retrieveParams() {
        try {
            Map<String, Double> oneCombination = new HashMap<String, Double>();
            for (String paramName : parametersMap.keySet()) {
                RegularParameterWrapper parameterWrapper = parametersMap.get(paramName);
                oneCombination.put(paramName,parameterWrapper.getValue());
            }
            if(stepParameter != null) {
                List<Double> stepParameterValues = stepParameter.getValues();
                return CombinationUtils.makeCombinations(oneCombination, stepParameterValues, stepParamName);

            } else {
                List<Map<String, Double>> combinations = new ArrayList<Map<String, Double>>();
                combinations.add(oneCombination);
                return combinations;
            }


        } catch ( NumberFormatException ex) {
            Toast.makeText(getActivity(), "You need to fill all parameters!", Toast.LENGTH_LONG).show();
            return null;
        } catch (IllegalArgumentException ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG,ex.toString());

            return null;
        }
    }




}
