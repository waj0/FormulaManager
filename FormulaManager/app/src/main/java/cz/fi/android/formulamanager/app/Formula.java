package cz.fi.android.formulamanager.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majo on 9. 4. 2014.
 * Just example formula class, it is not used yet
 */
public class Formula implements Parcelable {

    private Long id;
    private String name;
    private List<Parameter> params;
    private String rawFormula;

    public Formula() {
        params = new ArrayList<Parameter>();
    }

    public Formula(Parcel parcel) {
        this.id = parcel.readLong();
        this.name = parcel.readString();
        this.rawFormula = parcel.readString();

        params = parcel.readArrayList(getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.rawFormula);

        parcel.writeList(params);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Formula createFromParcel(Parcel in) {
            return new Formula(in);
        }

        public Formula[] newArray(int size) {
            return new Formula[size];
        }
    };

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

    public String getRawFormula() {
        return rawFormula;
    }

    public void setRawFormula(String rawFormula) {
        this.rawFormula = rawFormula;
    }

    /**
     * Adds parameter to formula, if it exists, method will edit it
     * @param p parameter to be added/edited
     */
    public void addParam(Parameter p) {
        if(p == null) {
            return;
        }
        int index = params.indexOf(p);
        if (index > -1){
            Parameter ex = params.get(index);
            ex.setName(p.getName());
            ex.setType(p.getType());
        } else {
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
