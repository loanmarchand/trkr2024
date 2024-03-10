mod components;
mod protocol;
mod tls;

use druid::widget::{Button, Flex, Label, SizedBox, TextBox};
use druid::{AppLauncher, Widget, WidgetExt, WindowDesc, PlatformError, WindowState, Color, Key};
use druid_derive::{Data, Lens};
use components::title::title_component;
use components::title_lvl_1::title_lvl_1_component;
use components::text::text_component;
use protocol::protocol::Protocol;
use crate::protocol::protocol::get_aurl_regex; // dossier::fichier::struct

// Constantes
const BORDER_COLOR: Key<Color> = druid::theme::BORDER_LIGHT;

fn main() -> Result<(), PlatformError> {
    let app_state = AppState {
        input_new_url: String::new(),
        service_name: String::from("no_data"),
        service_state: String::from("no_data"),
        service_id: String::from("no_data"),
        service_protocol: String::from("no_data"),
        service_username: String::from("no_data"),
        service_password: String::from("no_data"),
        service_authentication: String::from("no_data"),
        service_host: String::from("no_data"),
        service_port: String::from("no_data"),
        service_path: String::from("no_data"),
        service_min: String::from("no_data"),
        service_max: String::from("no_data"),
        service_validation_message: String::from(""),
        service_name_state: String::from("no_data"),
    };

    let main_window = WindowDesc::new(ui_builder())
        .title("Trkr Client")
        .set_window_state(WindowState::Maximized);

    AppLauncher::with_window(main_window)
        .launch(app_state)
}

fn ui_builder() -> impl Widget<(AppState)> {
    // Créer le titre centré dans un Flex
    let header = Flex::<AppState>::row()
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
        "https://www.swilabus.com/trkr2",
    ];

    // Créer la section de gauche avec la liste des services monitorés
    let left_sidebar = Flex::<AppState>::column()
        .with_child(title_lvl_1_component("Services monitorés").center())
        .with_spacer(8.0)
        .with_child(Button::new("Actualiser").on_click(update_service_list))
        .with_spacer(8.0);


    let left_sidebar = set_list_view(monitored_services, left_sidebar);

    // left_service_table
    let left_service_table = Flex::column()
        .with_child(Label::new(|data: &AppState, _env: &_| "ID: ".to_string() + &data.service_id))
        .with_spacer(8.0)
        .with_child(Label::new(|data: &AppState, _env: &_| "Protocol: ".to_string() + &data.service_protocol))
        .with_spacer(8.0)
        .with_child(Label::new(|data: &AppState, _env: &_| "Username: ".to_string() + &data.service_username))
        .with_spacer(8.0)
        .with_child(Label::new(|data: &AppState, _env: &_| "Password: ".to_string() + &data.service_password))
        .with_spacer(8.0)
        .with_child(Label::new(|data: &AppState, _env: &_| "Authentication: ".to_string() + &data.service_authentication))
        .with_spacer(8.0);


    let right_service_table = Flex::column()
        .with_child(Label::new(|data: &AppState, _env: &_| "Host: ".to_string() + &data.service_host))
        .with_spacer(8.0)
        .with_child(Label::new(|data: &AppState, _env: &_| "Port: ".to_string() + &data.service_port))
        .with_spacer(8.0)
        .with_child(Label::new(|data: &AppState, _env: &_| "Path: ".to_string() + &data.service_path))
        .with_spacer(8.0)
        .with_child(Label::new(|data: &AppState, _env: &_| "Min: ".to_string() + &data.service_min))
        .with_spacer(8.0)
        .with_child(Label::new(|data: &AppState, _env: &_| "Max: ".to_string() + &data.service_max))
        .with_spacer(8.0);


    // Créer une section qui va afficher les différents éléments d'un service
    let service_table = Flex::row()
        .with_flex_spacer(1.0)
        .with_flex_child(left_service_table, 1.0)
        .with_flex_spacer(1.0)
        .with_flex_child(right_service_table, 1.0)
        .center();

    // Créer la section en haut à droite
    let right_top_sidebar = Flex::<AppState>::column()
        .with_child(title_lvl_1_component("Monitorer un nouveau service").center())
        .with_child(text_component("Ajouter un nouveau service").center())
        .with_child(TextBox::new().lens(AppState::input_new_url))
        .with_spacer(8.0)
        .with_child(Button::new("Ajouter").on_click(add_new_service))
        .with_flex_spacer(1.0)
        .with_child(service_table.center())
        .with_flex_spacer(1.0)
        .with_child(Label::new(|data: &AppState, _env: &_| data.service_validation_message.clone()))
        .with_flex_spacer(1.0)
        .border(BORDER_COLOR, 1.0)
        .expand_width();

    // Créer la section en bas à droite
    let right_bottom_sidebar = Flex::column()
        .with_child(title_lvl_1_component("Etat du service").center())
        .with_flex_spacer(1.0)
        .with_child(Label::new(|data: &AppState, _env: &_| data.service_name_state.clone() + " : " + &data.service_state))
        .with_flex_spacer(1.0)
        .border(BORDER_COLOR, 1.0)
        .expand_width();

    // Combinez les sections supérieure et inférieure dans right_sidebar
    let right_sidebar = Flex::<AppState>::column()
        .with_flex_child(right_top_sidebar, 1.0)
        .with_flex_child(right_bottom_sidebar, 1.0)
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
            Flex::<AppState>::row()
                .with_flex_child(left_sidebar, 1.0)
                .with_flex_child(right_sidebar, 3.0),
            1.0,
        )
        .with_child(footer);

    main_layout
}

fn set_list_view(monitored_services: Vec<&str>, left_sidebar: Flex<(AppState)>) -> SizedBox<(AppState)> {
    let left_sidebar = monitored_services.iter().fold(left_sidebar, |column, service| {
        let service_owned = service.to_string();
        let processed_service = insert_line_breaks(&service_owned, 30);

        // Crée une nouvelle ligne pour chaque service
        let service_row = Flex::<AppState>::row()
            .with_child(Label::new(processed_service).center())
            .with_spacer(8.0)
            .with_child(Button::new("Voir").on_click(watch_service))
            .align_left()
            .on_click(move |_ctx, data: &mut AppState, _env| {
                data.service_name_state = service_owned.clone();
            });

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
                acc.push('\n');
            }
            acc.push(char);
            acc
        })
}

// Méthode qui sera applée quand on veut ajouter un nouveau service
fn add_new_service(_ctx: &mut druid::EventCtx, data: &mut AppState, _env: &druid::Env) {
    // println!("Bouton 'Ajouter' cliqué avec l'URL: {}", data.input_new_url);

    // On commence par remettre toutes les valeurs à "no_data"
    data.service_name = String::from("no_data");
    data.service_state = String::from("no_data");
    data.service_id = String::from("no_data");
    data.service_protocol = String::from("no_data");
    data.service_username = String::from("no_data");
    data.service_password = String::from("no_data");
    data.service_authentication = String::from("no_data");
    data.service_host = String::from("no_data");
    data.service_port = String::from("no_data");
    data.service_path = String::from("no_data");
    data.service_min = String::from("no_data");
    data.service_max = String::from("no_data");
    data.service_validation_message = String::from("");

    let aurl_regex = get_aurl_regex();

    match aurl_regex.captures(&data.input_new_url) {
        Some(caps) => {
            // println!("ID: {}", caps.name("id").unwrap().as_str());
            data.service_id = caps.name("id").unwrap().as_str().to_string();

            // println!("Protocol: {}", caps.name("protocol").unwrap().as_str());
            data.service_protocol = caps.name("protocol").unwrap().as_str().to_string();

            if let Some(username) = caps.name("username") {
                // println!("Username: {}", username.as_str());
                data.service_username = username.as_str().to_string();
            } else {
                // println!("Username: no_data");
                data.service_username = String::from("no_data");
            }

            if let Some(password) = caps.name("password") {
                // println!("Password: {}", password.as_str());
                data.service_password = password.as_str().to_string();
            } else {
                // println!("Password: no_data");
                data.service_password = String::from("no_data");
            }

            if let Some(authentication) = caps.name("authentication") {
                // println!("Authentication: {}", authentication.as_str());
                data.service_authentication = authentication.as_str().to_string();
            } else {
                // println!("Authentication: no_data");
                data.service_authentication = String::from("no_data");
            }

            // println!("Host: {}", caps.name("host").unwrap().as_str());
            data.service_host = caps.name("host").unwrap().as_str().to_string();

            if let Some(port) = caps.name("port") {
                // println!("Port: {}", port.as_str());
                data.service_port = port.as_str().to_string();
            } else {
                // println!("Port: no_data");
                data.service_port = String::from("no_data");
            }

            if let Some(path) = caps.name("path") {
                // println!("Path: {}", path.as_str());
                data.service_path = path.as_str().to_string();
            } else {
                // println!("Path: no_data");
                data.service_path = String::from("no_data");
            }

            // println!("Min: {}", caps.name("min").unwrap().as_str());
            data.service_min = caps.name("min").unwrap().as_str().to_string();

            // println!("Max: {}", caps.name("max").unwrap().as_str());
            data.service_max = caps.name("max").unwrap().as_str().to_string();

            data.service_name = data.input_new_url.clone();
            data.service_validation_message = String::from(data.service_name.clone() + " est validé par la regex");

            let newmon_request = Protocol::build_newmon(&data.input_new_url);

            println!("Requete à envoyer au moniteur: {}", newmon_request);
        }
        None => {
            println!("L'URL n'est pas valide");
            data.service_validation_message = String::from("L'URL n'est pas validé par la regex");
        }
    }
}

fn watch_service(_ctx: &mut druid::EventCtx, data: &mut AppState, _env: &druid::Env) {
    let request_request = Protocol::build_request(&data.service_name_state);

    println!("Requete à envoyer au moniteur: {}", request_request);
}

fn update_service_list(_ctx: &mut druid::EventCtx, data: &mut AppState, _env: &druid::Env) {
    // On construit la requête
    let listmon_request = Protocol::build_listmon();

    println!("Requete à envoyer au moniteur: {}", listmon_request);
}

#[derive(Clone, Data, Lens)]
struct AppState {
    input_new_url: String,
    service_name: String,
    service_name_state: String,
    service_state: String,
    service_id: String,
    service_protocol: String,
    service_username: String,
    service_password: String,
    service_authentication: String,
    service_host: String,
    service_port: String,
    service_path: String,
    service_min: String,
    service_max: String,
    service_validation_message: String,
}