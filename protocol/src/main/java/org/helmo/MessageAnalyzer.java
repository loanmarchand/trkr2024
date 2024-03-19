package org.helmo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageAnalyzer {
    public static Command analyzeMessage(String messageLine) {
        if (messageLine==null){
            return new Command("STOP");
        }

// CLIENT <-> MONITOR DEAMON

        // Message Newmon
        if (Pattern.matches(Protocole.getNewmon(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getNewmon());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("NEWMON", matcher.group(1));
            } else {
                return null;
            }
        }
        // Message Newmon_resp
        if (Pattern.matches(Protocole.getNewmon_resp(), messageLine)) {

            Pattern pattern = Pattern.compile(Protocole.getNewmon_resp());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("NEWMON_RESP", matcher.group(1), matcher.group(2));
            } else {
                return null;
            }
        }
        // Message Listmon
        if (Pattern.matches(Protocole.getListmon(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getListmon());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("LISTMON");
            } else {
                return null;
            }
        }
        // Message Mon
        if (Pattern.matches(Protocole.getMon(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getMon());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                messageLine = messageLine.replaceAll("\\x0D\\x0A", "");
                return new Command("MON", messageLine.split(" "));
            } else {
                return null;
            }
        }
        // Message Request
        if (Pattern.matches(Protocole.getRequest(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getRequest());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("REQUEST", matcher.group(1));
            } else {
                return null;
            }
        }
        // Message Respond
        if (Pattern.matches(Protocole.getRespond(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getRespond());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("RESPOND", matcher.group(1), matcher.group(3), matcher.group(20));
            } else {
                return null;
            }
        }


// PROBE <-> MONITOR DEAMON

        // Message Setup
        if (Pattern.matches(Protocole.getSetup(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getSetup());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                messageLine = messageLine.replaceAll("\\x0D\\x0A", "");
                return new Command("SETUP", messageLine.split(" "));
            } else {
                return null;
            }
        }
        // Message Statusof
        if (Pattern.matches(Protocole.getStatusof(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getStatusof());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("STATUSOF", matcher.group(1));
            } else {
                return null;
            }
        }
        // Message Status
        if (Pattern.matches(Protocole.getStatus(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getStatus());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("STATUS", matcher.group(1), matcher.group(3));
            } else {
                return null;
            }
        }


// MULTICAST

        // Message Probe
        if (Pattern.matches(Protocole.getProbe(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getProbe());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("PROBE", matcher.group(1), matcher.group(3));
            } else {
                return null;
            }
        }
        // Message Data
        if (Pattern.matches(Protocole.getData(), messageLine)) {
            Pattern pattern = Pattern.compile(Protocole.getData());
            Matcher matcher = pattern.matcher(messageLine);
            if (matcher.find()) {
                return new Command("DATA", matcher.group(1), matcher.group(3));
            } else {
                return null;
            }
        }
        return new Command("NONE");
    }
}
