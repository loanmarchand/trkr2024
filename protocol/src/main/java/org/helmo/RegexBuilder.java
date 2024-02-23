package org.helmo;

public class RegexBuilder {

    public static String buildAurl(Aurl aurl){
        return Protocole.getAurlBuild().replace("<id>",aurl.type()).replace("<url>",buildUrl(aurl.url())).replace("<min>",String.valueOf(aurl.min())).replace("<max>",String.valueOf(aurl.max()));
    }

    public static String buildUrl(Url url){
        if (url.user() == null){
            if (url.port() == -1){
                //Pas de pass pas de port
                return Protocole.getUrlBuild().replace("<protocole>",url.protocol()).replace("<host>",url.host()).replace("<path>", url.path());
            }else{
                //Pas de pass mais port
                return Protocole.getUrlpBuild().replace("<protocole>",url.protocol()).replace("<host>",url.host()).replace("<path>", url.path()).replace("<port>",String.valueOf(url.port()));
            }
        }else{
            if (url.port() == -1){
                //Pass mais pas port
                return Protocole.getUrlwupBuild().replace("<protocole>",url.protocol()).replace("<host>",url.host()).replace("<path>", url.path()).replace("<username>",url.user()).replace("<password>",url.password());
            }else{
                //Pass et port
                return Protocole.getUrlwuppBuild().replace("<protocole>",url.protocol()).replace("<host>",url.host()).replace("<path>", url.path()).replace("<port>",String.valueOf(url.port())).replace("<username>",url.user()).replace("<password>",url.password());
            }
        }
    }
}
