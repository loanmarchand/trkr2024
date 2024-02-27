package org.helmo;

import java.util.regex.Pattern;

public class Protocole {

// REGEX GENERALES
    private static final String letter = "[A-Za-z]";
    private static final String digit = "[0-9]";
    private static final String letter_digit = "(" + letter + "|" + digit + ")";
    private static final String crlf = "(\\x0D\\x0A)?";
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
    private static final String url = "(?<protocole>" + protocol + ")://(?:(?<username>(" + username + "))(?::(?<password>" + password_auth + "))?@)?(?<host>" + host + ")(?::(?<port>" + port + "))?(?<path>" + path + ")";
    private static final String min = digit + "{1,8}";
    private static final String max = digit + "{1,8}";
    private static final String frequency = digit + "{1,8}";
    private static final String augmented_url ="(" + id + ")!(" + url + ")!(" + min + ")!(" + max + ")";
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
    private static final String setup = "SETUP" + sp +"(" + frequency + ")" +"(" + sp + augmented_url+ "){0,100}" + crlf;
    private static final String statusof = "STATUSOF" + sp +"(" + id + ")" + crlf;
    private static final String status = "STATUS" + sp + "(" + id + ")" + sp + "(" + state + ")" + crlf;


//MULTICAST
    private static final String probe = "PROBE" + sp + "(" + protocol + ")" + sp +"(" + port + ")" + crlf;
    private static final String data = "DATA" + sp + "(" + protocol + ")" + sp + "(" + port +")" + crlf;




    private static final String AURL_BUILD = "<id>!<url>!<min>!<max>";
    private static final String URLWUP_BUILD = "<protocole>://<username>:<password>@<host><path>";
    private static final String URLWUPP_BUILD = "<protocole>://<username>:<password>@<host>:<port><path>";
    private static final String URL_BUILD = "<protocole>://<host><path>";
    private static final String URLP_BUILD = "<protocole>://<host>:<port><path>";


//CLIENT <--> MONITOR DEAMON builders
    private static final String NEWMON_BUILD = "NEWMON <aurl>\r\n";
    private static final String NEWMONRESP_BUILD = "<+OK|-ERR> <message?>\r\n";
    private static final String LISTMON_BUILD = "LISTMON\r\n";
    private static final String MON_BUILD = "MON <ids>\r\n";
    private static final String REQUEST_BUILD = "REQUEST <id>\r\n";
    private static final String RESPOND_BUILD = "RESPOND <id> <url> <state>\r\n";


//PROBE <--> MONITOR DEAMON builders
    private static final String SETUP_BUILD = "SETUP <frequency> <aurls>\r\n";
    private static final String STATUSOF_BUILD = "STATUSOF <id>\r\n";
    private static final String STATUS_BUILD = "STATUS <id> <state>\r\n";


//MULTICAST builders
    private static final String PROBE_BUILD = "PROBE <protocol> <port>\r\n";
    private static final String DATA_BUILD = "DATA <protocol> <port>\r\n";


    public static String getAurl(){
        return augmented_url;
    }
    public static String getUrl(){
        return url;
    }



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
    public static String getData(){
        return data;
    }




    public static String getAurlBuild(){
        return AURL_BUILD;
    }
    public static String getUrlwupBuild(){
        return URLWUP_BUILD;
    }
    public static String getUrlwuppBuild(){
        return URLWUPP_BUILD;
    }
    public static String getUrlBuild(){
        return URL_BUILD;
    }
    public static String getUrlpBuild(){
        return URLP_BUILD;
    }



    public static String getNewmonBuild(){
        return NEWMON_BUILD;
    }
    public static String getNewmonrespBuild(){
        return NEWMONRESP_BUILD;
    }
    public static String getListmonBuild(){
        return LISTMON_BUILD;
    }
    public static String getMonBuild(){
        return MON_BUILD;
    }
    public static String getRequestBuild(){
        return REQUEST_BUILD;
    }
    public static String getRespondBuild(){
        return RESPOND_BUILD;
    }


    public static String getSetupBuild(){
        return SETUP_BUILD;
    }
    public static String getStatusofBuild(){
        return STATUSOF_BUILD;
    }
    public static String getStatusBuild(){
        return STATUS_BUILD;
    }


    public static String getProbeBuilder(){
        return PROBE_BUILD;
    }
    public static String getDataBuilder(){
        return DATA_BUILD;
    }



    public static void main(String[] args) {
        // Exemple d'utilisation de certaines expressions régulières
        String sampleText = "SETUP 10 http1!https://www.swilabus.com/!0!1500 http2!https://www.swilabus.be/!0!2000 http3!https://www.swilabus.com/trkr1!0!1700 http4!https://www.swilabus.com/trkr2!0!1800\r\n";
        System.out.println(augmented_url);
        if (Pattern.matches(setup, sampleText)) {
            System.out.println("La chaîne correspond au pattern.");
        } else {
            System.out.println("La chaîne ne correspond pas au pattern.");
        }
        if (Pattern.matches(augmented_url, sampleText)) {
            System.out.println("La chaîne correspond au pattern.");
        } else {
            System.out.println("La chaîne ne correspond pas au pattern.");
        }

        // Vous pouvez utiliser d'autres expressions régulières de manière similaire.
    }


}


