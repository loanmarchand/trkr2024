package org.helmo;

public class Command {
    private String CommandType = null;
    private String Aurl = null;
    private String StatusNewmonresp = null;
    private String NewmonrespMessage = null;





    Command(String commandType,String... data){
        switch (commandType) {
            case "NEWMON" -> {
                this.CommandType = "NEWMON";
                this.Aurl = data[0];
            }
            case "NEWMON_RESP" -> {
                this.CommandType = "NEWMON_RESP";
                this.StatusNewmonresp = data[0];
                this.NewmonrespMessage = data[1];
            }
        }
    }






    public String getCommandType() {
        return CommandType;
    }
    public String getAurl(){
        return Aurl;
    }
    public String getStatusNewmonresp(){
        return StatusNewmonresp;
    }
    public String getNewmonrespMessage(){
        return NewmonrespMessage;
    }
}
