package cz.muni.fi.android.formulaManager.app.manager;

import java.util.List;

import cz.muni.fi.android.formulaManager.app.entity.Formula;
import cz.muni.fi.android.formulaManager.app.entity.Parameter;

/**
 * Created by Martin on 2. 5. 2014.
 */
public class FormulaManagerImpl implements FormulaManager {
    @Override
    public void addParameter(Formula formula, Parameter parameter) {

        checkValues(formula, parameter);
        formula.getParams().add(parameter);
    }

    @Override
    public void editParameter(Formula formula, Parameter parameter) {

        checkValues(formula, parameter);

        List<Parameter> parameters = formula.getParams();
        int indexOfParameter = existParameter(parameter, parameters);

        Parameter editedParameter = parameters.get(indexOfParameter);
        editedParameter.setName(parameter.getName());
        editedParameter.setType(parameter.getType());
    }

    private int existParameter(Parameter parameter, List<Parameter> parameters) {
        int indexOfParameter = parameters.indexOf(parameter);

        if(indexOfParameter == -1) {
            throw new IllegalArgumentException("Parameter: " + parameter + "does not exist!");
        }
        return indexOfParameter;
    }

    @Override
    public void deleteParameter(Formula formula, Parameter parameter) {

        checkValues(formula, parameter);

        List<Parameter> parameters = formula.getParams();
        int indexOfParameter = existParameter(parameter, parameters);

        parameters.remove(indexOfParameter);
    }

    private void checkValues(Formula formula, Parameter parameter) {
        if(parameter == null || formula == null) {
            throw  new IllegalArgumentException("Formula or parameter is null");
        }
    }


}
