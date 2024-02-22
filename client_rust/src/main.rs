use druid::widget::{Flex, Label, SizedBox};
use druid::{AppLauncher, Color, Widget, WidgetExt, WindowDesc, PlatformError, WindowState};

fn main() -> Result<(), PlatformError> {
    let main_window = WindowDesc::new(ui_builder())
        .title("Trkr Client")
        .window_size((800.0, 600.0))
        .set_window_state(WindowState::Maximized);

    AppLauncher::with_window(main_window)
        .launch(())
}

fn ui_builder() -> impl Widget<()> {
    // Créer le titre
    let header = Label::new("Trkr Client")
        .padding(10.0)
        .expand_width();

    // Créer la section de gauche
    let left_sidebar = SizedBox::empty()
        .expand_height();

    // Créer la section de droite
    let right_sidebar = SizedBox::empty()
        .background(Color::grey(0.7))
        .expand_height();

    // Créer le footer
    let footer = Label::new("Footer")
        .padding(10.0)
        .expand_width();

    // Assembler la mise en page principale
    let main_layout = Flex::column()
        .with_child(header)
        .with_flex_child(
            Flex::row()
                .with_flex_child(left_sidebar, 1.0)
                .with_flex_child(right_sidebar, 3.0),
            1.0,
        )
        .with_child(footer);

    main_layout
}
