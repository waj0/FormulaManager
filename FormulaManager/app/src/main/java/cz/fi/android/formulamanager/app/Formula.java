package cz.fi.android.formulamanager.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Majo on 9. 4. 2014.
 * Just example formula class, it is not used yet
 */
public class Formula {

    private Long id;
    private String name;
    private List<Parameter> params;

    private String parsable;
    private String description;

    public Formula() {
        params = new ArrayList<Parameter>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParams() {
        return params;
    }

    public void setParams(List<Parameter> params) {
        this.params = params;
    }

    public String getParsable() {
        return parsable;
    }

    public void setParsable(String parsable) {
        this.parsable = parsable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addParam(Parameter p) {
        if(p != null) {
            params.add(p);
        }
    }

    public String getParamsAsString(){
        String ret = "";
        for(Parameter p : params) {
            ret = ret + p.getName() + "[" + p.getType() + "]\n";
        }
        return ret;
    }
}
