package cz.fi.android.formulamanager.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Majo on 13. 4. 2014.
 */
public class Parameter implements Parcelable {

    //types of parameters
    public static final int PARAM_REGULAR = 0; //regular one value param
    public static final int PARAM_INDEX = 1; //index in summation etc
    public static final int PARAM_STEP = 2; //increment after each calculation

    Long id;
    String name;
    int type;

    public Parameter() {
    }

    public Parameter(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.type = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeInt(this.type);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Parameter createFromParcel(Parcel in) {
            return new Parameter(in);
        }

        public Parameter[] newArray(int size) {
            return new Parameter[size];
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (!id.equals(parameter.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
