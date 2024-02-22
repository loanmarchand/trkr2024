package org.helmo;

import org.helmo.Aurl;
import org.helmo.Url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLParser {

    public static Url parseUrl(String urlString) {
        // Expression régulière pour analyser l'URL de base
        String urlPattern = "(?<protocol>[A-Za-z]+)://(?:(?<username>[^:@]+)(?::(?<password>[^:@]*))?@)?(?<host>[^:/]+)(?::(?<port>\\d+))?(/(?<path>[^/].*[^/])?)?";

        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(urlString);

        if (matcher.find()) {
            String protocol = matcher.group("protocol");
            String username = matcher.group("username");
            String password = matcher.group("password");
            String host = matcher.group("host");
            int port = matcher.group("port") != null ? Integer.parseInt(matcher.group("port")) : -1;
            String path = matcher.group("path");

            // Ajoute "/" si le chemin n'est pas vide et ne commence pas déjà par "/"
            if (path != null && !path.isEmpty() && !path.startsWith("/")) {
                path = "/" + path;
            }

            return new Url(protocol, username, password, host, port, path);
        }

        return null;
    }

    public static Aurl parseAugmentedUrl(String augmentedUrlString) {
        // Expression régulière pour analyser l'URL augmentée
        String augmentedUrlPattern = "(?<id>[A-Za-z0-9]{5,10})!(?<url>.+)!(?<min>[0-9]{1,8})!(?<max>[0-9]{1,8})";

        Pattern pattern = Pattern.compile(augmentedUrlPattern);
        Matcher matcher = pattern.matcher(augmentedUrlString);

        if (matcher.find()) {
            String id = matcher.group("id");
            String urlString = matcher.group("url");
            int min = Integer.parseInt(matcher.group("min"));
            int max = Integer.parseInt(matcher.group("max"));

            // Analyser l'URL de base
            Url url = parseUrl(urlString);
            if (url != null) {
                return new Aurl(id, url, min, max);
            }
        }

        return null;
    }
}
