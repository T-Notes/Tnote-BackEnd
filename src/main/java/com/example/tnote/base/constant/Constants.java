package com.example.tnote.base.constant;

public class Constants {
    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 24 * 60 * 60 * 1000L; //  1day -> 30 minutes -> 30 * 60 * 1000L
//    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 60 * 1000L; //  1분 -> test 용도

    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days
}
