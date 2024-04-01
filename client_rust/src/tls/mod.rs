use std::fs::File;
use std::io::Read;
use std::sync::Arc;

use native_tls::Certificate;
use tokio::io::{AsyncBufReadExt, AsyncReadExt, AsyncWriteExt};
use tokio::net::TcpStream;
use tokio::sync::Mutex;
use tokio_native_tls::{TlsConnector, TlsStream};

// Structure pour maintenir la connexion TLS
pub struct TlsClient {
    stream: Arc<Mutex<TlsStream<TcpStream>>>,
}

impl TlsClient {
    // Établir une connexion TLS et retourner une instance de TlsClient
    pub async fn connect() -> Option<Self> {
        let ca_file_paths = [
            "src/ressource/SwilabusIntermediateG21.crt",
            "src/ressource/SwilabusMainCertificateG1.crt",
        ];

        let mut builder = native_tls::TlsConnector::builder();
        for ca_file_path in ca_file_paths.iter() {
            let mut buf = Vec::new();
            let mut file = match File::open(ca_file_path) {
                Ok(file) => file,
                Err(_) => return None,
            };
            if file.read_to_end(&mut buf).is_err() {
                return None;
            }
            let cert = match Certificate::from_pem(&buf) {
                Ok(cert) => cert,
                Err(_) => return None,
            };
            builder.add_root_certificate(cert);
        }

        let connector = match builder.build() {
            Ok(connector) => TlsConnector::from(connector),
            Err(_) => return None,
        };

        let stream = match TcpStream::connect("trkr.labo24.swilabus.com:60223").await {
            Ok(stream) => stream,
            Err(_) => return None,
        };
        let domain = "trkr.labo24.swilabus.com";
        let stream = match connector.connect(domain, stream).await {
            Ok(stream) => stream,
            Err(_) => return None,
        };

        let stream = Arc::new(Mutex::new(stream));
        Some(TlsClient { stream })
    }

    // Envoyer un message et recevoir la réponse
    pub async fn send_and_receive(&self, message: &str) -> Option<String> {
        let mut stream = self.stream.lock().await;

        if stream.write_all(message.as_bytes()).await.is_err() {
            return None;
        }

        let mut buffer = Vec::new();
        if stream.read_buf(&mut buffer).await.is_err() {
            return None;
        }

        match String::from_utf8(buffer) {
            Ok(response) => Some(response),
            Err(_) => None,
        }
    }
}