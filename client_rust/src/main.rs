use druid::widget::Label;
use druid::{AppLauncher, Widget, WidgetExt, WindowDesc, PlatformError, WindowState};

fn main() -> Result<(), PlatformError> {
    let main_window = WindowDesc::new(ui_builder())
        .title("Fenêtre Plein Écran")
        .window_size((800.0, 600.0))
        .set_window_state(WindowState::Maximized);

    AppLauncher::with_window(main_window)
        .launch(())
}

fn ui_builder() -> impl Widget<()> {
    Label::new("Bienvenue dans la fenêtre en plein écran")
        .center()
        .padding(10.0)
}
