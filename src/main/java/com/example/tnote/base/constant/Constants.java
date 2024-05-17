package com.example.tnote.base.constant;

public class Constants {
    //public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 24 * 60 * 60 * 1000L; //  1day -> 30 minutes -> 30 * 60 * 1000L
    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 3 * 60 * 60 * 1000L; // 3시간 -test용도
    //public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 2 * 60 * 1000L; //  2분 -> test 용도


    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days
}