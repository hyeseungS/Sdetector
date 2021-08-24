package com.example.sdetector;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MoreDataList implements Parcelable {
    ArrayList<MoreData> list;

    public MoreDataList() {
    }

    protected MoreDataList(Parcel in) {
        list = new ArrayList<>();
        in.readTypedList(list, MoreData.CREATOR);
    }

    public static final Creator<MoreDataList> CREATOR = new Parcelable.Creator<MoreDataList>() {
        @Override
        public MoreDataList createFromParcel(Parcel in) {
            return new MoreDataList(in);
        }

        @Override
        public MoreDataList[] newArray(int size) {
            return new MoreDataList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(list);
    }

    public ArrayList<MoreData> getList() {
        return list;
    }

    public void setList(ArrayList<MoreData> list) {
        this.list = list;
    }
}
