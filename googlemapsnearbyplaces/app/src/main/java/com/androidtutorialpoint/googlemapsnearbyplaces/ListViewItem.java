package com.androidtutorialpoint.googlemapsnearbyplaces;

/**
 * Created by user on 2016-10-30.
 */

// ListView 아이템 데이터 클래스 정의
public class ListViewItem {
    private int image;
    private String name;
    private String tel;
    private String email;

    public ListViewItem(int image, String name, String tel, String email){
        this.image = image;
        this.name = name;
        this.tel = tel;
        this.email = email;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public String getEmail() {
        return email;
    }

}