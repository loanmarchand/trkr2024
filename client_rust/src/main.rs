use druid::widget::{Flex, Label, SizedBox};
use druid::{AppLauncher, Widget, WidgetExt, WindowDesc, PlatformError, WindowState, Color, Key};

// Constantes
const BORDER_COLOR: Key<Color> =  druid::theme::BORDER_LIGHT;

fn main() -> Result<(), PlatformError> {
    let main_window = WindowDesc::new(ui_builder())
        .title("Trkr Client")
        .set_window_state(WindowState::Maximized);

    AppLauncher::with_window(main_window)
        .launch(())
}

fn ui_builder() -> impl Widget<()> {
    // Créer le titre centré dans un Flex
    let header = Flex::row()
        .with_flex_spacer(1.0)
        .with_child(Label::new("Trkr Client").padding(10.0))
        .with_flex_spacer(1.0)
        .border(BORDER_COLOR, 1.0)
        .expand_width();

    // Créer la section de gauche
    let left_sidebar = SizedBox::empty()
        .border(BORDER_COLOR, 1.0)
        .expand_width()
        .expand_height();

    // Créer la section de droite
    let right_sidebar = SizedBox::empty()
        .border(BORDER_COLOR, 1.0)
        .expand_width()
        .expand_height();

    // Créer le footer
    let footer = Label::new("Footer")
        .padding(10.0)
        .border(BORDER_COLOR, 1.0)
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

