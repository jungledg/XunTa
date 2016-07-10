package com.larryhowell.xunta.common;

import com.larryhowell.xunta.bean.Person;
import com.larryhowell.xunta.bean.Plan;

import java.util.ArrayList;

public class Config {
    public static String nickname = null;
    public static String telephone = null;
    public static String portrait = null;

    public static Plan plan;

    public static boolean isConnected = false;

    public static ArrayList<Person> bindList = new ArrayList<>();

    public static String currentCity = "南京";
    public static String currentCityDetail = "航空";
}
