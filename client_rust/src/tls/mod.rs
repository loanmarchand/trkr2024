use tokio::io::{self, AsyncReadExt, AsyncWriteExt};
use tokio::net::TcpStream;
use tokio_native_tls::TlsConnector;
use native_tls::Certificate;
use std::fs::File;
use std::io::Read;

pub async fn connect_tls_and_receive(message: &str) -> Option<String> {
    let ca_file_paths = [
        "src/ressource/SwilabusIntermediateG21.crt",
        "src/ressource/SwilabusMainCertificateG1.crt",
    ];

    let mut builder = native_tls::TlsConnector::builder();
    for ca_file_path in ca_file_paths.iter() {
        let mut buf = Vec::new();
        let mut file = match File::open(ca_file_path) {
            Ok(file) => file,
            Err(_) => return None, // Gestion simplifiée de l'erreur
        };
        if file.read_to_end(&mut buf).is_err() {
            return None; // Gestion simplifiée de l'erreur
        }
        let cert = match Certificate::from_pem(&buf) {
            Ok(cert) => cert,
            Err(_) => return None, // Gestion simplifiée de l'erreur
        };
        builder.add_root_certificate(cert);
    }

    let connector = match builder.build() {
        Ok(connector) => TlsConnector::from(connector),
        Err(_) => return None, // Gestion simplifiée de l'erreur
    };

    let stream = match TcpStream::connect("trkr.labo24.swilabus.com:60223").await {
        Ok(stream) => stream,
        Err(_) => return None, // Gestion simplifiée de l'erreur
    };
    let domain = "trkr.labo24.swilabus.com";
    let stream = match connector.connect(domain, stream).await {
        Ok(stream) => stream,
        Err(_) => return None, // Gestion simplifiée de l'erreur
    };

    let (mut reader, mut writer) = io::split(stream);
    if writer.write_all(message.as_bytes()).await.is_err() {
        return None; // Gestion simplifiée de l'erreur
    }

    // Lire la réponse
    let mut buffer = Vec::new();
    if reader.read_to_end(&mut buffer).await.is_err() {
        return None; // Gestion simplifiée de l'erreur
    }
    match String::from_utf8(buffer) {
        Ok(response) => Some(response),
        Err(_) => None, // Gestion simplifiée de l'erreur
    }
}
