package cz.muni.fi.android.formulaManager.app.manager;

import cz.muni.fi.android.formulaManager.app.entity.Formula;
import cz.muni.fi.android.formulaManager.app.entity.Parameter;

/**
 * Created by Martin on 2. 5. 2014.
 */
public interface FormulaManager {

    /**
     * Adds parameter to formula
     *
     * @param parameter parameter to be added
     * @param formula formula to where parameter will be inserted
     */
    public void addParameter(Formula formula,Parameter parameter);

    /**
     * Edit parameter of formula
     *
     * @param parameter parameter to be edited
     * @param formula formula to where parameter will be edited
     */
    public void editParameter(Formula formula, Parameter parameter);

    /**
     * Delete parameter from formula
     *
     * @param parameter parameter to be deleted
     * @param formula formula to where parameter will be deleted
     */
    public void deleteParameter(Formula formula, Parameter parameter);

}
