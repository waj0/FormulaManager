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

        if(parameter == null || formula == null) {
            return;
        }

        formula.getParams().add(parameter);
    }

    @Override
    public void editParameter(Formula formula, Parameter parameter) {

        if(parameter == null || formula == null) {
            return;
        }

        List<Parameter> parameters = formula.getParams();
        int indexOfParameter = parameters.indexOf(parameter);

        if(indexOfParameter == -1) {
            return;
        }

        Parameter editedParameter = parameters.get(indexOfParameter);
        editedParameter.setName(parameter.getName());
        editedParameter.setType(parameter.getType());
    }
}
