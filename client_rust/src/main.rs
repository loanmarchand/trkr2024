use std::sync::Arc;

use druid::{AppDelegate, AppLauncher, Color, Command, DelegateCtx, Env, EventCtx, Handled, Key, Selector, Target, Widget, WidgetExt, WindowDesc, WindowState};
use druid::widget::{Button, Flex, Label, List, TextBox};
use druid_derive::{Data, Lens};
use tokio::spawn;
use tokio::sync::Mutex;

use components::text::text_component;
use components::title::title_component;
use components::title_lvl_1::title_lvl_1_component;
use protocol::protocol::Protocol;

use crate::protocol::protocol::get_aurl_regex;
use crate::tls::TlsClient;

mod components;
mod protocol;
mod tls;

// dossier::fichier::struct
pub const UPDATE_SERVICE_RESPONSE: Selector<String> = Selector::new("update-service-response");
pub const UPDATE_LIST_SERVICE_RESPONSE: Selector<String> = Selector::new("update-list-service-response");
pub const WATCH_SERVICE: Selector<String> = Selector::new("watch-service");

// Constantes
const BORDER_COLOR: Key<Color> = druid::theme::BORDER_LIGHT;

#[tokio::main]
async fn main() {
    // Initialisation de la connexion TLS
    let tls_client = match TlsClient::connect().await {
        Some(client) => client,
        None => {
            eprintln!("Failed to create TLS connection");
            return;
        },
    };
    // Convertir la connexion TLS en une Arc<Mutex<TlsClient>> afin de la stocker dans AppState
    let tls_client = Arc::new(Mutex::new(tls_client));

    // Création d'un Vec temporaire avec vos chaînes de caractères
    let temp_services = vec![
    ];

    // Conversion vers Arc<Vec<String>>,
    let monitored_services = Arc::new(temp_services);

    // Utilisation de ce im::Vector pour initialiser AppState
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
        monitored_services,
        tls_client,
    };

    let main_window = WindowDesc::new(ui_builder())
        .title("Trkr Client")
        .set_window_state(WindowState::Maximized);

    AppLauncher::with_window(main_window)
        .delegate(YourDelegate {})
        .launch(app_state).expect("Moniteur non joignable");
}

fn ui_builder() -> impl Widget<AppState> {
    // Créer le titre centré dans un Flex
    let header = Flex::<AppState>::row()
        .with_flex_spacer(1.0)
        .with_child(title_component("Trkr Client"))
        .with_flex_spacer(1.0)
        .border(BORDER_COLOR, 1.0)
        .expand_width();


    // Créer la section de gauche avec la liste des services monitorés
    let left_sidebar = Flex::<AppState>::column()
        .with_child(title_lvl_1_component("Services monitorés").center())
        .with_spacer(8.0)
        .with_child(Button::new("Actualiser").on_click(update_service_list))
        .with_spacer(8.0)
        .with_child(set_list_view());


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

fn set_list_view() -> impl Widget<AppState> {
    List::new(|| {
        Flex::row()
            .with_child(Label::new(|item: &String, _env: &Env| item.clone()))
            .with_spacer(8.0)
            .with_child(Button::new("Voir").on_click(|ctx, data: &mut String, _env| {
                ctx.submit_command(Command::new(WATCH_SERVICE, data.clone(), Target::Auto));
            }))
    }).lens(AppState::monitored_services)
}

// Méthode qui sera applée quand on veut ajouter un nouveau service
fn add_new_service(_ctx: &mut EventCtx, data: &mut AppState, _env: &Env) {
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
            let tls_client_arc = data.tls_client.clone();
            let newmon_request = Protocol::build_newmon(&data.input_new_url);

            println!("Requête à envoyer au moniteur: {}", newmon_request);

            let newmon_request_clone = newmon_request.clone();
            // Crée une nouvelle tâche asynchrone
            spawn(async move {
                // Obtenir un verrou sur le Mutex pour accéder à `TlsClient`
                let tls_client = tls_client_arc.lock().await;

                match tls_client.send_and_receive(&newmon_request_clone).await {
                    Some(response) => {
                        // Affiche la réponse du serveur
                        println!("Réponse du serveur: {}", response);
                    },
                    None => {
                        // Gère l'absence de réponse
                        println!("Erreur de connexion TLS ou aucune réponse reçue");
                    }
                }
            });

        }
        None => {
            println!("L'URL n'est pas valide");
            data.service_validation_message = String::from("L'URL n'est pas validé par la regex");
        }
    }
}

fn watch_service(ctx: &mut DelegateCtx, data: &mut AppState, _env: &Env) {
    let request_request = Protocol::build_request(&data.service_name_state);
    let event_sink = ctx.get_external_handle();

    println!("Requête à envoyer au moniteur: {}", request_request);

    // Cloner l'Arc pour pouvoir l'utiliser dans la tâche asynchrone sans déplacer `data`
    let tls_client_arc = data.tls_client.clone();

    spawn(async move {
        // Obtenir un verrou sur le Mutex pour accéder à `TlsClient`
        let tls_client = tls_client_arc.lock().await;

        match tls_client.send_and_receive(&request_request).await {
            Some(response) => {
                println!("Réponse du serveur: {}", response);

                // Utilisation de event_sink pour soumettre la réponse reçue à l'interface utilisateur
                event_sink.submit_command(UPDATE_SERVICE_RESPONSE, response, Target::Auto)
                    .expect("Failed to submit command");
            },
            None => {
                println!("Erreur de connexion TLS ou aucune réponse reçue");
            }
        }
    });
}


fn update_service_list(ctx: &mut EventCtx, data: &mut AppState, _env: &Env) {
    let listmon_request = Protocol::build_listmon();
    let event_sink = ctx.get_external_handle();

    println!("Requête à envoyer au moniteur: {}", listmon_request);

    // Cloner l'Arc pour pouvoir l'utiliser dans la tâche asynchrone sans déplacer `data`
    let tls_client_arc = data.tls_client.clone();

    spawn(async move {
        // Obtenir un verrou sur le Mutex pour accéder à `TlsClient`
        let tls_client = tls_client_arc.lock().await;

        match tls_client.send_and_receive(&listmon_request).await {
            Some(response) => {
                println!("Réponse du serveur: {}", response);

                // Utilisation de event_sink pour soumettre la réponse reçue à l'interface utilisateur
                event_sink.submit_command(UPDATE_LIST_SERVICE_RESPONSE, response, Target::Auto)
                    .expect("Failed to submit command");
            },
            None => {
                println!("Erreur de connexion TLS ou aucune réponse reçue");
            }
        }
    });
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
    monitored_services: Arc<Vec<String>>,
    tls_client: Arc<Mutex<TlsClient>>
}

struct YourDelegate;

impl AppDelegate<AppState> for YourDelegate {
    fn command(
        &mut self,
        ctx: &mut DelegateCtx,
        _target: Target,
        cmd: &Command,
        data: &mut AppState,
        _env: &Env,
    ) -> Handled {
        if let Some(response) = cmd.get(UPDATE_SERVICE_RESPONSE) {
            // Afficher un message dans la console pour voir si la commande a été reçue
            println!("Commande reçue 1: {}", response);
            data.service_state = response.clone();
            Handled::Yes
        } else if let Some(response) = cmd.get(UPDATE_LIST_SERVICE_RESPONSE) {
            // Afficher un message dans la console pour voir si la commande a été reçue
            println!("Commande reçue 2: {}", response);
            data.monitored_services = Arc::new(response.split_whitespace().skip(1).map(|s| s.to_string()).collect());
            Handled::Yes
        } else if let Some(service_name) = cmd.get(WATCH_SERVICE) {
            // Afficher un message dans la console pour voir si la commande a été reçue
            println!("Commande reçue 3: {}", service_name);
            data.service_name_state = service_name.clone();
            watch_service(ctx, data, _env);
            Handled::Yes
        }

        else {
            Handled::No
        }
    }
}