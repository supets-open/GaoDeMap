package com.supets.gaodeng.map;

import com.amap.api.services.core.LatLonPoint;

/**
 * gaodeng
 *
 * @user lihongjiang
 * @description
 * @date 2017/9/18
 * @updatetime 2017/9/18
 */

public class AddressInfo {

    public LatLonPoint  point;

    public String address;

    public String name="";

    public AddressInfo(LatLonPoint jindu,  String address) {
        this.point = jindu;
        this.address = address;
    }
}
