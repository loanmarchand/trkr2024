package org.helmo;

import java.util.List;

public class MessageBuilder {


    public static String buildNewmon(String aurl){
        return Protocole.getNewmonBuild().replace("<aurl>",aurl);
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
        String ids = "";
        for (String id: idList) {
            ids += " "+id;
        }
        return Protocole.getMonBuild().replace(" <ids>",ids);
    }



    public static String buildProbe(String protocol, int port) {
        return Protocole.getProbeBuilder().replace("<protocol>", protocol).replace("<port>", String.valueOf(port));
    }
    public static String buildData(String protocol, int port) {
        return Protocole.getDataBuilder().replace("<protocol>", protocol).replace("<port>", String.valueOf(port));
    }
}
