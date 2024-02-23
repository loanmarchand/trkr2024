use druid::{Widget, WidgetExt, FontDescriptor, FontWeight};
use druid::widget::Label;
use crate::AppState;

pub fn title_component(text: &str) -> impl Widget<(AppState)> {
    Label::new(text)
        .with_font(FontDescriptor::new(druid::FontFamily::SYSTEM_UI)
            .with_weight(FontWeight::BOLD)
            .with_size(24.0))
        .padding(10.0)
}
