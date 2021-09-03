package com.example.sdetector;

import android.os.Parcel;
import android.os.Parcelable;

public class MoreData implements Parcelable {

    public int index;
    public String name;
    public float time;

    public MoreData(int index, String name, float time) {
        this.index = index;
        this.name = name;
        this.time = time;
    }

    public MoreData(Parcel in) {
        index = in.readInt();
        name = in.readString();
        time = in.readFloat();
    }

    public static final Creator<MoreData> CREATOR = new Creator<MoreData>() {
        @Override
        public MoreData createFromParcel(Parcel in) {
            return new MoreData(in);
        }

        @Override
        public MoreData[] newArray(int size) {
            return new MoreData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeFloat(time);
    }

}
