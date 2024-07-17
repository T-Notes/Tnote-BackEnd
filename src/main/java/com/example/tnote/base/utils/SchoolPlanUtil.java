package com.example.tnote.base.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class SchoolPlanUtil {

    protected URL url;

    private String Type;
    private String path;
    private String pIndex;
    private String pSize;

    private String ATPT_OFCDC_SC_CODE;
    private String SD_SCHUL_CODE;

    @Value("${api.career-key}")
    private String KEY;

    @Value("${api.call-back-url}")
    private String callBackUrl;

    /**
     * make URL with variable in class
     */
    private URL makeURL() throws IOException {


        /*URL*/
        callBackUrl += "/" + path + "?KEY=" + KEY;
        callBackUrl += "&Type=" + this.Type;
        callBackUrl += "&pIndex=" + this.pIndex;
        callBackUrl += "&pSize=" + this.pSize;
        callBackUrl += "&" + URLEncoder.encode("ATPT_OFCDC_SC_CODE", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(
                this.ATPT_OFCDC_SC_CODE, StandardCharsets.UTF_8);
        callBackUrl += "&" + URLEncoder.encode("SD_SCHUL_CODE", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(
                this.SD_SCHUL_CODE, StandardCharsets.UTF_8);

        return new URL(callBackUrl);
    }

    public URL makeURL(String path, String KEY, String Type, String pIndex, String pSize, String ATPT_OFCDC_SC_CODE,
                       String SD_SCHUL_CODE) throws IOException {
        this.path = path;
        this.KEY = KEY;
        this.Type = Type;
        this.pIndex = pIndex;
        this.pSize = pSize;

        this.ATPT_OFCDC_SC_CODE = ATPT_OFCDC_SC_CODE;
        this.SD_SCHUL_CODE = SD_SCHUL_CODE;

        this.url = makeURL();

        return this.url;
    }


    /**
     * call Neis API
     */

    public String schoolPlan(String path, String ATPT_OFCDC_SC_CODE, String SD_SCHUL_CODE)
            throws IOException {

        URL url = makeURL(path, KEY, "JSON", "1", "500", ATPT_OFCDC_SC_CODE, SD_SCHUL_CODE);
        HttpURLConnection conn;
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json; charset=utf-8");

        BufferedReader rd;

        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();

        conn.disconnect();

        return sb.toString();
    }

}

