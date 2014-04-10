package cz.fi.android.formulamanager.app;

/**
 * Created by Majo on 9. 4. 2014.
 * Just example formula class, it is not used yet
 */
public class Formula {
    private String name;
    private String[] params;

    private String parsable;
    private String decsription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String getParsable() {
        return parsable;
    }

    public void setParsable(String parsable) {
        this.parsable = parsable;
    }

    public String getDecsription() {
        return decsription;
    }

    public void setDecsription(String decsription) {
        this.decsription = decsription;
    }
}
