pub(crate) struct Protocol;

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