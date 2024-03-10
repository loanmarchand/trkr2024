use tokio::net::{TcpListener, TcpStream};
use tokio::io::{self, AsyncReadExt, AsyncWriteExt};
use tokio_rustls::{TlsAcceptor, TlsConnector};
use rustls::{ServerConfig, ClientConfig, Certificate, PrivateKey, ServerName};
use std::sync::Arc;
use std::fs::File;
use std::io::BufReader;
use rustls_pemfile::certs;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    // Mettre en commentaire en fonction de ce que l'on veut tester
    server().await?;
    // client().await?;

    Ok(())
}

async fn server() -> Result<(), Box<dyn std::error::Error>> {
    let cert_file = File::open("src/ressource/star.labo24.swilabus.com.crt")?;
    let key_file = File::open("src/ressource/star.labo24.swilabus.com.key")?;
    let mut cert_reader = BufReader::new(cert_file);
    let mut key_reader = BufReader::new(key_file);

    let certs = certs(&mut cert_reader)
        .expect("Failed to read certificate chain")
        .iter()
        .map(|cert| Certificate(cert.clone()))
        .collect::<Vec<Certificate>>();

    // Charger la clé privée au format PKCS#8
    let keys = rustls_pemfile::pkcs8_private_keys(&mut key_reader)
        .expect("Failed to read private keys")
        .iter()
        .map(|key| PrivateKey(key.clone()))
        .collect::<Vec<PrivateKey>>();

    if keys.is_empty() {
        return Err("No private keys found".into());
    }

    let config = ServerConfig::builder()
        .with_safe_defaults()
        .with_no_client_auth()
        .with_single_cert(certs, keys[0].clone())?;

    let acceptor = TlsAcceptor::from(Arc::new(config));

    let listener = TcpListener::bind("192.168.1.26:7878").await?;
    println!("Server listening on 192.168.1.26:7878");

    loop {
        let (stream, _addr) = listener.accept().await?;
        let acceptor = acceptor.clone();

        tokio::spawn(async move {
            let stream = acceptor.accept(stream).await.expect("Failed to accept TLS connection");
            let (mut reader, _) = tokio::io::split(stream);
            let mut buf = [0; 1024];

            match reader.read(&mut buf).await {
                Ok(0) => {}, // Connection was closed
                Ok(_) => {
                    println!("Received: {}", String::from_utf8_lossy(&buf));
                },
                Err(e) => println!("Error reading from connection: {}", e),
            }
        });
    }
}

async fn client() -> io::Result<()> {
    let config = ClientConfig::builder()
        .with_safe_defaults()
        .with_root_certificates(rustls::RootCertStore::empty())
        .with_no_client_auth();
    let connector = TlsConnector::from(Arc::new(config));
    let domain = "star.labo24.swilabus.com";

    // Convertir le domaine en ServerName
    let domain = ServerName::try_from(domain).expect("Invalid DNS name");

    let stream = TcpStream::connect("192.168.1.26:7878").await?;
    let mut stream = connector.connect(domain, stream).await.expect("Failed to connect");

    stream.write_all(b"Bonjour du client avec TLS!").await?;
    println!("Message envoyé au serveur");

    Ok(())
}