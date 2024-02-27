package org.helmo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexAnalyzer {
    public static Aurl analyzeAurl(){


        return null;
    }

    public static Url analyzeUrl(String url){
        if(Pattern.matches(Protocole.getUrl(),url)){
            Pattern pattern = Pattern.compile(Protocole.getUrl());
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                if(matcher.group("username") != null){
                    if(matcher.group("port") != null){
                        //UserPassPort
                        return new Url(matcher.group("protocole"), matcher.group("username"), matcher.group("password"), matcher.group("host"), Integer.parseInt(matcher.group("port")), matcher.group("path"));
                    }else{
                        //UserPass
                        return new Url(matcher.group("protocole"), matcher.group("username"), matcher.group("password"), matcher.group("host"), -1, matcher.group("path"));
                    }
                } else{
                    if(matcher.group("port") != null){
                        //Port
                        return new Url(matcher.group("protocole"), null, null, matcher.group("host"), Integer.parseInt(matcher.group("port")), matcher.group("path"));
                    }else{
                        //NUPP
                        return new Url(matcher.group("protocole"), null, null, matcher.group("host"), -1, matcher.group("path"));
                    }
                }
            }else{
                return null;
            }
        }
        return null;
    }
}
