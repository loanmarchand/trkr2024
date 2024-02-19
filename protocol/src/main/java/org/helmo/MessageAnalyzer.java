package org.helmo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MessageAnalyzer {
    public static Command analyzeMessage(String messageLine){
        // Message Newmon
        if(Pattern.matches(Protocole.getNewmon(),messageLine)){
            Pattern pattern = Pattern.compile(Protocole.getNewmon());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("NEWMON", matcher.group(1));
            }else{
                return null;
            }
        }
        // Message Newmon_resp
        if(Pattern.matches(Protocole.getNewmon_resp(),messageLine)){

            Pattern pattern = Pattern.compile(Protocole.getNewmon_resp());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("NEWMON_RESP", matcher.group(1), matcher.group(2));
            }else{
                return null;
            }
        }
        // Message Listmon
        if(Pattern.matches(Protocole.getListmon(),messageLine)){
            Pattern pattern = Pattern.compile(Protocole.getListmon());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("LISTMON");
            }else{
                return null;
            }
        }
        // Message Mon
        if(Pattern.matches(Protocole.getMon(),messageLine)){
            Pattern pattern = Pattern.compile(Protocole.getMon());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                messageLine = messageLine.replaceAll("\\x0D\\x0A", "");
                return new Command("MON",messageLine.split(" "));
            }else{
                return null;
            }
        }
        // Message Request
        if(Pattern.matches(Protocole.getRequest(),messageLine)){
            Pattern pattern = Pattern.compile(Protocole.getRequest());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("REQUEST", matcher.group(1));
            }else{
                return null;
            }
        }
        // Message Request
        if(Pattern.matches(Protocole.getRespond(),messageLine)){
            Pattern pattern = Pattern.compile(Protocole.getRespond());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("RESPOND", matcher.group(1), matcher.group(3), matcher.group(20));
            }else{
                return null;
            }
        }


        return null;
    }



    public static void main(String[] args) {

        Command testCommand = analyzeMessage("RESPOND myid15 https://youtube.com/sa OK\r\n");
        if(testCommand != null){
            System.out.println(testCommand.getCommandType());
            System.out.println(testCommand.getId()+"---");
            System.out.println(testCommand.getUrlEtPath()+"---");
            System.out.println(testCommand.getState()+"---");
/*            for (String element : testCommand.getIdList()) {
                System.out.println(element);
            }*/
        }else{
            System.out.println("Le renvoi de la méthode analyze est null");
        }


    }
}
