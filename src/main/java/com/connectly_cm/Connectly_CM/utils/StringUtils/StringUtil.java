package com.connectly_cm.Connectly_CM.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringUtil{
    public static final String COMMA_SEPARATOR_REGEX = "[,]";
    public static boolean isEmpty(String str) {
        return str.isEmpty() || str==null;
    }
    public static List<String> listSeparatedByComma(String s){
        return new ArrayList<>(List.of(s.split(COMMA_SEPARATOR_REGEX)));
    }
}
