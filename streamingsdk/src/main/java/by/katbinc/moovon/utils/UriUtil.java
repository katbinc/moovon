package by.katbinc.moovon.utils;

import android.net.Uri;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UriUtil {
    public static String getBase(String url) {
        String[] parts = url.split("\\?", 2);
        if (parts[0].endsWith("/")) {
            return parts[0].substring(0, parts[0].length() - 1);
        }
        return parts[0];
    }

    public static Map<String, String> getParameters(String url) {
        Uri uri = Uri.parse(url);
        if (uri.isOpaque()) {
            return Collections.emptyMap();
        }

        Map<String, String> map = new HashMap<>();
        for (String key : uri.getQueryParameterNames()) {
            map.put(key, uri.getQueryParameter(key));
        }
        return map;
    }
}
