use lazy_static::lazy_static;
use regex::Regex;

pub(crate) struct Protocol;
pub(crate) struct ResponseParser;

impl Protocol {
    const NEWMON_BUILD: &'static str = "NEWMON <aurl>\r\n";
    const LISTMON_BUILD: &'static str = "LISTMON\r\n";
    const REQUEST_BUILD: &'static str = "REQUEST <id>\r\n";

    pub fn build_newmon(aurl: &str) -> String {
        Self::NEWMON_BUILD.replace("<aurl>", aurl)
    }
    pub fn build_listmon() -> String {
        Self::LISTMON_BUILD.to_string()
    }
    pub fn build_request(id: &str) -> String {
        Self::REQUEST_BUILD.replace("<id>", id)
    }
}

impl ResponseParser {
    pub fn parse_response(response: &str) -> Result<String, &'static str> {
        lazy_static! {
            static ref NEWMON_RESP_REGEX: Regex = Regex::new(r"^\+OK|-ERR(?: (.*))?\r\n$").unwrap();
            static ref MON_REGEX: Regex = Regex::new(r"^MON(?: (.*))?\r\n$").unwrap();
            static ref RESPOND_REGEX: Regex = Regex::new(r"^RESPOND ([A-Za-z0-9]{5,10}) ([A-Za-z0-9]{3,15}://[^\s]+) (OK|ALARM|DOWN|UNKNOWN)\r\n$").unwrap();
        }

        if let Some(caps) = NEWMON_RESP_REGEX.captures(response) {
            return Ok(format!("Newmon Response: {}", caps.get(1).map_or("", |m| m.as_str())));
        } else if let Some(caps) = MON_REGEX.captures(response) {
            return Ok(format!("List of Monitors: {}", caps.get(1).map_or("", |m| m.as_str())));
        } else if let Some(caps) = RESPOND_REGEX.captures(response) {
            return Ok(format!(
                "Respond: ID={} URL={} State={}",
                caps.get(1).unwrap().as_str(),
                caps.get(2).unwrap().as_str(),
                caps.get(3).unwrap().as_str()
            ));
        }

        Err("Unknown response type")
    }
}

lazy_static! {
    static ref AURL_REGEX: Regex = Regex::new(
        r"^(?P<id>[A-Za-z0-9]{5,10})!(?P<protocol>[A-Za-z0-9]{3,15})://(?:(?P<username>[A-Za-z0-9]{3,50})(?::(?P<password>[A-Za-z0-9\-_.=+*$°()\[\]{}^]{3,50})(?:#(?P<authentication>[A-Za-z0-9\-_.=+*$°()\[\]{}^]{3,50}))?)?@)?(?P<host>[A-Za-z0-9.\-_]{3,50})(?::(?P<port>[0-9]{1,5}))?(?P<path>/[A-Za-z0-9.\-_/]{0,100})!(?P<min>[0-9]{1,8})!(?P<max>[0-9]{1,8})$"
    ).unwrap();
}

pub fn get_aurl_regex() -> &'static Regex {
    &AURL_REGEX
}