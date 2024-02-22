mod components;

use druid::widget::{Button, Flex, Label, SizedBox};
use druid::{AppLauncher, Widget, WidgetExt, WindowDesc, PlatformError, WindowState, Color, Key};
use components::title::title_component;
use components::title_lvl_1::title_lvl_1_component;
use components::text::text_component;

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
        .with_child(title_component("Trkr Client"))
        .with_flex_spacer(1.0)
        .border(BORDER_COLOR, 1.0)
        .expand_width();

    let monitored_services = vec![
        "snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0",
        "snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0",
        "https://www.swilabus.com/",
        "https://www.swilabus.be/",
        "https://www.swilabus.com/trkr1",
        "https://www.swilabus.com/trkr2"
    ];

    // Créer la section de gauche avec la liste des services monitorés
    let left_sidebar = Flex::column()
        .with_child(title_lvl_1_component("Services monitorés").center())
        .with_spacer(8.0);

    let left_sidebar = set_list_view(monitored_services, left_sidebar);

    // Créer la section de droite
    let right_sidebar = Flex::column()
        .with_child(title_lvl_1_component("Monitorer un nouveau service").center())
        .with_flex_spacer(1.0)
        .border(BORDER_COLOR, 1.0)
        .expand_width()
        .expand_height();

    // Créer le footer
    let footer = Flex::row()
        .with_flex_spacer(1.0)
        .with_child(text_component("Jillian Rezette - Loan Marchand - Romain Coibion - Roderkerken Bogaert Thibaut"))
        .with_flex_spacer(1.0)
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

fn set_list_view(monitored_services: Vec<&str>, left_sidebar: Flex<()>) -> SizedBox<()> {
    let left_sidebar = monitored_services.iter().fold(left_sidebar, |column, service| {
        let service_owned = service.to_string(); // Convertit `&str` en `String`
        let processed_service = insert_line_breaks(&service_owned, 30); // Applique la fonction de prétraitement

        // Crée une nouvelle ligne pour chaque service
        let service_row = Flex::row()
            .with_child(Label::new(processed_service).center()) // Utilise la chaîne traitée
            .with_spacer(8.0)
            .with_child(Button::new("Voir").on_click(move |_ctx, _data, _env| {
                println!("Bouton 'Voir' cliqué pour le service: {}", service_owned);
            }))
            .align_left();

        column.with_child(service_row).with_spacer(2.0)
    });

    let left_sidebar = left_sidebar.with_flex_spacer(1.0);

    let left_sidebar = left_sidebar
        .border(BORDER_COLOR, 1.0)
        .expand_width()
        .expand_height();
    left_sidebar
}


fn insert_line_breaks(original: &str, max_length: usize) -> String {
    original.chars()
        .enumerate()
        .fold(String::new(), |mut acc, (index, char)| {
            if index % max_length == 0 && index != 0 {
                acc.push('\n'); // Insère un saut de ligne
            }
            acc.push(char);
            acc
        })
}
