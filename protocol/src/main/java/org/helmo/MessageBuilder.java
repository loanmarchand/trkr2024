package org.helmo;

import java.util.List;

public class MessageBuilder {


    public static String buildNewmon(Aurl aurl){
        return Protocole.getNewmonBuild().replace("<aurl>",RegexBuilder.buildAurl(aurl));
    }
    public static String buildNewmonResp(String okOrErr){
        return Protocole.getNewmonrespBuild().replace("<+OK|-ERR>", okOrErr).replace(" <message?>","");
    }
    public static String buildNewmonResp(String okOrErr ,String message){
        return Protocole.getNewmonrespBuild().replace("<+OK|-ERR>", okOrErr).replace("<message?>",message);
    }
    public static String buildListmon(){
        return Protocole.getListmonBuild();
    }
    public static String buildMon(List<String> idList){
        StringBuilder ids = new StringBuilder();
        for (String id: idList) {
            ids.append(" ").append(id);
        }
        return Protocole.getMonBuild().replace(" <ids>", ids.toString());
    }
    public static String buildRequest(String id){
        return Protocole.getRequestBuild().replace("<id>",id);
    }
    public static String buildRespond(String id, Url url, String state){
        return Protocole.getRespondBuild().replace("<id>",id).replace("<url>",RegexBuilder.buildUrl(url)).replace("<state>",state);
    }


    public static String buildSetup(String frequency,List<Aurl> aurlList){
        String message = Protocole.getSetupBuild();
        String aurlsLine = "";
        return null;
    }



    public static String buildProbe(String protocol, int port) {
        return Protocole.getProbeBuilder().replace("<protocol>", protocol).replace("<port>", String.valueOf(port));
    }
    public static String buildData(String protocol, int port) {
        return Protocole.getDataBuilder().replace("<protocol>", protocol).replace("<port>", String.valueOf(port));
    }
}
