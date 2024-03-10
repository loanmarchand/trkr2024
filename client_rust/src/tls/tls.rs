use rustls::{ClientConfig};
use rustls_pemfile::{certs, pkcs8_private_keys};
use std::fs::File;
use std::io::BufReader;
use std::sync::Arc;
use tokio::io;

pub struct TlsClient {
    config: Arc<ClientConfig>,
}

impl TlsClient {
    pub fn new(cert_path: &str, key_path: &str) -> io::Result<Self> {
        let mut config = ClientConfig::new();

        // Charger le certificat et la clé privée
        let cert_file = &mut BufReader::new(File::open(cert_path)?);
        let key_file = &mut BufReader::new(File::open(key_path)?);

        // Lire le certificat et la clé
        let certs = certs(cert_file).map_err(|_| io::Error::new(io::ErrorKind::InvalidInput, "Invalid certificate"))?;
        let mut keys = pkcs8_private_keys(key_file).map_err(|_| io::Error::new(io::ErrorKind::InvalidInput, "Invalid key"))?;

        // Assurez-vous que nous avons au moins une clé privée
        if keys.is_empty() {
            return Err(io::Error::new(io::ErrorKind::InvalidInput, "No private key found"));
        }

        // Ajouter le certificat et la clé au config
        config.set_single_client_cert(certs, keys.remove(0)).map_err(|_| io::Error::new(io::ErrorKind::InvalidInput, "Could not set the client certificate"))?;

        // Utiliser les certificats racine par défaut
        config.root_store.add_server_trust_anchors(&webpki_roots::TLS_SERVER_ROOTS);

        // Retourner une instance de TlsClient
        Ok(TlsClient { config: Arc::new(config) })
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_tls_client_certificate_loading() {
        // Chemin vers les fichiers de certificat et de clé pour le test
        let cert_path = "../ressource/star.labo24.swilabus.com.p12";
        let key_path = "../ressource/star.labo24.swilabus.com.p12";

        // Tentative de création d'un client TLS avec le certificat et la clé
        let client_result = TlsClient::new(cert_path, key_path);

        assert!(client_result.is_ok(), "Failed to create TLS client with provided certificate and key");
    }
}

