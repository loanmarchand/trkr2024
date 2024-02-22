use druid::{FontDescriptor, Widget, WidgetExt};
use druid::widget::Label;

pub fn text_component(text: &str) -> impl Widget<()> {
    Label::new(text)
        .with_font(FontDescriptor::new(druid::FontFamily::SYSTEM_UI)
            .with_size(16.0))
        .padding(10.0)
}