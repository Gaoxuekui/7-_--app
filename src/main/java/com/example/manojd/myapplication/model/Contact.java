package com.example.manojd.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    private int id;
    private String name;
    private String email;
    private String mobile;
    private String fenzu;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFenzu() {
        return fenzu;
    }

    public void setFenzu(String fenzu) {
        this.fenzu = fenzu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Contact() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.mobile);
        dest.writeString(this.fenzu);
        dest.writeString(this.image);
    }

    protected Contact(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.email = in.readString();
        this.mobile = in.readString();
        this.fenzu = in.readString();
        this.image = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getBig() {
        String big = getName().toString();
        String firstLetter = big.substring(0, 1).toUpperCase();
        if (firstLetter.charAt(0) >= 'A' && firstLetter.charAt(0) <= 'Z') {
            return big.substring(0, 1).toUpperCase();
        }
        else
            return "#";
    }
}
