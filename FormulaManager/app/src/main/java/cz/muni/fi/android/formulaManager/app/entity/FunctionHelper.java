package cz.muni.fi.android.formulaManager.app.entity;

/**
 * Created by Martin on 4. 5. 2014.
 */
public class FunctionHelper {

    private String formulaForm;
    private String toast;

    public FunctionHelper(String formulaForm, String toast) {
        this.formulaForm = formulaForm;
        this.toast = toast;
    }

    public FunctionHelper(String formulaForm) {
        this.formulaForm = formulaForm;
    }

    public String getFormulaForm() {
        return formulaForm;
    }

    public String getToast() {
        return toast;
    }

}
