package org.helmo;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command {
    private final String CommandType;
    private Aurl aurl = null;
    private String StatusNewmonresp = null;
    private String NewmonrespMessage = null;
    private List<String> idList = null;
    private String id = null;
    private String urlEtPath = null;
    private String state = null;
    private String frequency = null;
    private List<Aurl> aurlList = new ArrayList<>();
    private String protocole = null;
    private String port = null;





    public Command(String commandType,String... data){
        this.CommandType = commandType;
        switch (commandType) {
            case "NEWMON" -> this.aurl = RegexAnalyzer.analyzeAurl(data[0]);
            case "NEWMON_RESP" -> {
                this.StatusNewmonresp = data[0];
                this.NewmonrespMessage = data[1];
            }
            case "MON" -> {
                this.idList = new ArrayList<>(Arrays.asList(data));
                this.idList.remove(0);
            }
            case "REQUEST", "STATUSOF" -> this.id = data[0];
            case "RESPOND" -> {
                this.id = data[0];
                this.urlEtPath = data[1];
                this.state = data[2];
            }
            case "SETUP" -> {
                List<String> list = new ArrayList<>(Arrays.asList(data));
                list.remove(0);
                this.frequency = list.get(0);
                list.remove(0);
                for (String aurl:list) {
                    this.aurlList.add(RegexAnalyzer.analyzeAurl(aurl));
                }
                //this.aurlList = new ArrayList<>(Arrays.asList(data));
                //this.aurlList.remove(0);
                //this.frequency =  this.aurlList.get(0);
                //this.aurlList.remove(0);
            }
            case "STATUS" -> {
                this.id = data[0];
                this.state = data[1];
            }
            case "PROBE", "DATA" -> {
                this.protocole = data[0];
                this.port = data[1];
            }
        }
    }



    public String getCommandType() {
        return CommandType;
    }
    public Aurl getAurl(){
        return aurl;
    }
    public String getStatusNewmonresp(){
        return StatusNewmonresp;
    }
    public String getNewmonrespMessage(){
        return NewmonrespMessage;
    }
    public List<String> getIdList(){
        return new ArrayList<>(idList);
    }
    public String getId(){
        return id;
    }
    public String getUrlEtPath(){
        return urlEtPath;
    }
    public String getState(){
        return state;
    }
    public List<Aurl> getAurlList(){
        return new ArrayList<>(aurlList);
    }
    public String getFrequency(){
        return frequency;
    }
    public String getProtocole(){
        return protocole;
    }
    public String getPort(){
        return port;
    }

    @Override
    public String toString() {
        return "Command{" +
                "CommandType='" + CommandType + '\'' +
                ", aurl='" + aurl + '\'' +
                ", StatusNewmonresp='" + StatusNewmonresp + '\'' +
                ", NewmonrespMessage='" + NewmonrespMessage + '\'' +
                ", idList=" + idList +
                ", id='" + id + '\'' +
                ", urlEtPath='" + urlEtPath + '\'' +
                ", state='" + state + '\'' +
                ", frequency='" + frequency + '\'' +
                ", aurlList=" + aurlList +
                ", protocole='" + protocole + '\'' +
                ", port='" + port + '\'' +
                '}';
    }


}
