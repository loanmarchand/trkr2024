package org.helmo.protocole;

import java.util.regex.Pattern;

public class Protocole {
    // Expressions régulières réutilisées
    private static final String letter = "[A-Za-z]";
    private static final String digit = "[0-9]";
    private static final String letter_digit = "(" + letter + "|" + digit + ")";
    private static final String crlf = "\\x0D\\x0A";
    private static final String port = digit + "{1,5}";
    private static final String character = "[\\x20-\\xFF]";
    private static final String character_spec = "[-_\\.=+\\*\\$°\\(\\)\\[\\]\\{\\}\\^]";
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
    private static final String url = "(" + protocol + "://(?:" + username + "(?::" + password_auth + ")?@" + host + "(?::" + port + ")?(?:" + path + ")?))";
    private static final String min = digit + "{1,8}";
    private static final String max = digit + "{1,8}";
    private static final String frequency = digit + "{1,8}";
    private static final String augmented_url = id + "!" + url + "!" + min + "!" + max;
    private static final String state = "(?:OK|ALARM|DOWN|UNKNOWN)";
    private static final String message = character + "{1,200}";

    public static void main(String[] args) {
        // Exemple d'utilisation de certaines expressions régulières
        String sampleText = "http://example.com/user:password!12345!67890";
        System.out.println(url);

        if (Pattern.matches(augmented_url, sampleText)) {
            System.out.println("La chaîne correspond au pattern.");
        } else {
            System.out.println("La chaîne ne correspond pas au pattern.");
        }

        // Vous pouvez utiliser d'autres expressions régulières de manière similaire.
    }

}


