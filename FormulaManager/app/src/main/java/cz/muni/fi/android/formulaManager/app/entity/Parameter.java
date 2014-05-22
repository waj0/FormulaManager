package cz.muni.fi.android.formulaManager.app.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Majo on 13. 4. 2014.
 */
public class Parameter implements Parcelable {

    //types of parameters TODO sync with DB
    public static enum ParameterType {
    REGULAR(0),  //regular one value param
    INDEX (1), //index in summation etc
    STEP(2); //increment after each calculation

        private int value;

        ParameterType(int value) {
            this.value = value;
        }

        public int getIntValue() {
            return value;
        }
        private static final Map<Integer, ParameterType> intToTypeMap = new HashMap<Integer, ParameterType>();
        static {
            for (ParameterType type : ParameterType.values()) {
                intToTypeMap.put(type.value, type);
            }
        }

        public static ParameterType fromIntValue(int value){
            ParameterType type = intToTypeMap.get(Integer.valueOf(value));
            if (type == null)
                throw new IllegalArgumentException("value " + value +" is out of range");
            return type;
        }


    }
    private Long id;
    private String name;
    private ParameterType type;

    public Parameter() {
    }

    public Parameter(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.type = ParameterType.fromIntValue(in.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeInt(this.type.getIntValue());
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

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
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

    @Override
    public String toString() {
        return "Parameter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
