use tokio::io::{self, AsyncWriteExt};
use tokio::net::TcpStream;
use tokio_native_tls::TlsConnector;
use native_tls::Certificate;
use std::fs::File;
use std::io::Read;

#[tokio::main]
async fn main() -> io::Result<()> {
    // Chemins vers les fichiers de certificat
    let ca_file_paths = [
        "src/ressource/SwilabusIntermediateG21.crt",
        "src/ressource/SwilabusMainCertificateG1.crt",
    ];

    // Construire le connecteur TLS
    let mut builder = native_tls::TlsConnector::builder();

    // Charger chaque certificat et l'ajouter au constructeur
    for ca_file_path in ca_file_paths.iter() {
        let mut buf = Vec::new();
        let mut file = File::open(ca_file_path).expect("cannot open file");
        file.read_to_end(&mut buf).expect("cannot read file");
        let cert = Certificate::from_pem(&buf).expect("cannot create cert");
        builder.add_root_certificate(cert);
    }

    let connector = TlsConnector::from(builder.build().expect("cannot build TLS connector"));

    // Connecter au serveur
    let stream = TcpStream::connect("star.labo24.swilabus.com:7878").await?;
    let domain = "star.labo24.swilabus.com";
    let stream = connector.connect(domain, stream).await.expect("TLS connection failed");

    // Créer un client pour envoyer un message
    let (_, mut writer) = io::split(stream);

    // Exemple de message à envoyer
    let msg = "Hello, server 3!\n";
    writer.write_all(msg.as_bytes()).await.expect("failed to write data");

    Ok(())
}
