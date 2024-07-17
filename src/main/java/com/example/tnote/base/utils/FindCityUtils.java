package com.example.tnote.base.utils;

import static com.example.tnote.base.exception.ErrorCode.NO_PERMISSION;

import com.example.tnote.base.exception.CustomException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FindCityUtils {
    public int findCityCode(String cityName) {
        Map<String, Integer> cityCodes = new HashMap<>();

        cityCodes.put("서울특별시", 100260);
        cityCodes.put("부산광역시", 100267);
        cityCodes.put("인천광역시", 100269);
        cityCodes.put("대전광역시", 100271);
        cityCodes.put("대구광역시", 100272);
        cityCodes.put("울산광역시", 100273);
        cityCodes.put("광주광역시", 100275);
        cityCodes.put("경기도", 100276);
        cityCodes.put("강원도", 100278);
        cityCodes.put("충청북도", 100280);
        cityCodes.put("충청남도", 100281);
        cityCodes.put("전라북도", 100282);
        cityCodes.put("전라남도", 100283);
        cityCodes.put("경상북도", 100285);
        cityCodes.put("경상남도", 100291);
        cityCodes.put("제주도", 100292);

        Integer cityCode = cityCodes.get(cityName);
        return cityCode;
    }

    public String changeGubun(String gubun) {
        String result = null;

        if (gubun.equals("고등학교")) {
            result = "high_list";
        } else if (gubun.equals("중학교")) {
            result = "midd_list";
        } else {
            result = "elem_list";
        }

        return result;
    }

    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
    public InputStream getNetworkConnection(HttpURLConnection urlConnection) throws IOException {
        urlConnection.setConnectTimeout(3000);
        urlConnection.setReadTimeout(3000);
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);

        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new CustomException(NO_PERMISSION, "http connection 에러");
        }
        return urlConnection.getInputStream();
    }

    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    public String readStreamToString(InputStream stream) throws IOException {
        StringBuilder result = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        String readLine;
        while ((readLine = br.readLine()) != null) {
            result.append(readLine + "\n\r");
        }

        br.close();

        return result.toString();
    }
}
