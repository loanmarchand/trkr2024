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
                return new Command("NEWMON",matcher.group(1));
            }else{
                return null;
            }
        }
        // Message Newmon_resp
        if(Pattern.matches(Protocole.getNewmon_resp(),messageLine)){

            Pattern pattern = Pattern.compile(Protocole.getNewmon_resp());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("NEWMON_RESP",matcher.group(1),matcher.group(2));
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



        return null;
    }



    public static void main(String[] args) {

        Command testCommand = analyzeMessage("LISTMON");
        if(testCommand != null){
            System.out.println(testCommand.getCommandType());
        }else{
            System.out.println("Le renvoi de la méthode analyze est null");
        }


    }
}
