pub(crate) struct Protocol;

impl Protocol {
    const NEWMON_BUILD: &'static str = "NEWMON <aurl>\r\n";

    pub fn build_newmon(aurl: &str) -> String {
        Self::NEWMON_BUILD.replace("<aurl>", aurl)
    }
}