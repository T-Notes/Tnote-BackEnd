package com.example.tnote.base.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
    private final FindCityUtils findCityUtils;
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

    @Value("${api.career-key-openAPI}")
    private String KEYOpenAPI;

    @Value("${api.call-back-url-openAPI}")
    private String callBackUrlOpenAPI;

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

    public String buildApiUrl(String region, String schoolType, String schoolName) throws UnsupportedEncodingException {
        return callBackUrlOpenAPI + "apiKey=" + KEYOpenAPI +
                "&svcType=api&svcCode=SCHOOL&contentType=json" +
                "&gubun=" + findCityUtils.changeGubun(schoolType) +
                "&region=" + findCityUtils.findCityCode(region) +
                "&searchSchulNm=" + URLEncoder.encode(schoolName, "UTF-8");
    }

    public String fetchApiData(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        return findCityUtils.readStreamToString(findCityUtils.getNetworkConnection(urlConnection));
    }

    public String extractSchoolCode(String schoolInfo) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(schoolInfo);
        JsonNode rowNode = rootNode.path("schoolInfo").get(1).path("row");

        if (rowNode.isArray() && rowNode.size() > 0) {
            return rowNode.get(0).path("SD_SCHUL_CODE").asText();
        }

        return null;
    }
}