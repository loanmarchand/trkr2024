package org.helmo;

import java.util.regex.Pattern;

public class Protocole {

// REGEX GENERALES
    private static final String letter = "[A-Za-z]";
    private static final String digit = "[0-9]";
    private static final String letter_digit = "(" + letter + "|" + digit + ")";
    private static final String crlf = "\\x0D\\x0A";
    private static final String port = digit + "{1,5}";
    private static final String character = "[\\x20-\\xFF]";
    private static final String character_spec = "[-_.=+*$°()\\[\\]{}^]";
    private static final String character_pass = "(" + letter_digit + "|" + character_spec + ")";
    private static final String sp = "\\x20";
    private static final String id = letter_digit + "{5,10}";
    private static final String protocol = letter_digit + "{3,15}";
    private static final String username = letter_digit + "{3,50}";
    private static final String password = character_pass + "{3,50}";
    private static final String authentication = character_pass + "{3,50}";
    private static final String password_auth = password + "(?:#" + authentication + ")?";
    private static final String host = "(" + letter_digit + "|\\.|_|-)" + "{3,50}";
    private static final String path = "/(?:" + letter_digit + "|\\.|_|-|/){0,100}";
    private static final String url = "((" + protocol + ")://(?:(" + username + ")(?::(" + password_auth + "))?@)?(" + host + ")(?::(" + port + "))?(" + path + "))";
    private static final String min = digit + "{1,8}";
    private static final String max = digit + "{1,8}";
    private static final String frequency = digit + "{1,8}";
    private static final String augmented_url ="(" + id + ")!" + url + "!(" + min + ")!(" + max + ")";
    private static final String state = "(?:OK|ALARM|DOWN|UNKNOWN)";
    private static final String message = character + "{1,200}";


//CLIENT <--> MONITOR DEAMON
    private static final String newmon = "NEWMON" + sp +"("+ augmented_url +")"+ crlf;
    private static final String newmon_resp = "(\\+OK|-ERR)"+sp+"("+message+")?" + crlf;
    private static final String listmon = "LISTMON" + crlf;
    private static final String mon = "MON" + "(" + sp +"("+ id + ")){0,100}" + crlf;
    private static final String request = "REQUEST" + sp +"("+ id +")"+ crlf;
    private static final String respond = "RESPOND" + sp +"(" + id +")" + sp +"(" + url +")" + sp + "(" + state +")" + crlf;


//PROBE <--> MONITOR DEAMON
    private static final String setup = "SETUP" + sp +"(" + frequency + ")" +"((" + sp + augmented_url+ ")){0,100}" + crlf;
    private static final String statusof = "STATUSOF" + sp +"(" + id + ")" + crlf;
    private static final String status = "STATUS" + sp + "(" + id + ")" + sp + "(" + state + ")" + crlf;


//MULTICAST
    private static final String probe = "PROBE" + sp + "(" + protocol + ")" + sp +"(" + port + ")" + crlf;
    private static final String PROBE_MSG = "PROBE <protocol> <port>\r\n";
    private static final String data = "DATA" + sp + "(" + protocol + ")" + sp + "(" + port +")" + crlf;



    public static String getNewmon(){
        return newmon;
    }
    public static String getNewmon_resp(){
        return newmon_resp;
    }
    public static String getListmon(){
        return listmon;
    }
    public static String getMon(){
        return mon;
    }
    public static String getRequest(){
        return request;
    }
    public static String getRespond(){
        return respond;
    }


    public static String getSetup(){
        return setup;
    }
    public static String getStatusof(){
        return statusof;
    }
    public static String getStatus(){
        return status;
    }


    public static String getProbe(){
        return probe;
    }

    public static String getPROBE_MSG(){
        return PROBE_MSG;
    }
    public static String getData(){
        return data;
    }



    public static void main(String[] args) {
        // Exemple d'utilisation de certaines expressions régulières
        String sampleText = "monid!https://salute.sal/ezajo!57575!54645654";
        System.out.println(data);

        if (Pattern.matches(augmented_url, sampleText)) {
            System.out.println("La chaîne correspond au pattern.");
        } else {
            System.out.println("La chaîne ne correspond pas au pattern.");
        }

        // Vous pouvez utiliser d'autres expressions régulières de manière similaire.

    }

}


