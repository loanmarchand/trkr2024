use tokio::net::TcpListener;
use tokio::io::{AsyncReadExt, AsyncWriteExt};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    // Bind le serveur à l'adresse IP et au port spécifié
    let listener = TcpListener::bind("192.168.1.26:7878").await?;

    println!("Serveur en écoute sur 192.168.1.26:7878");

    loop {
        // Accepter une connexion
        let (mut socket, addr) = listener.accept().await?;

        println!("Connexion établie avec {}", addr);

        // Utilisation d'une tâche asynchrone pour gérer la connexion
        tokio::spawn(async move {
            let mut buf = [0; 1024];

            // Boucle pour lire les données envoyées par le client
            loop {
                match socket.read(&mut buf).await {
                    Ok(0) => {
                        println!("Connexion terminée avec {}", addr);
                        return;
                    }
                    Ok(n) => {
                        // Afficher le message reçu dans la console du serveur
                        if let Ok(msg) = std::str::from_utf8(&buf[..n]) {
                            println!("Message de {}: {}", addr, msg);
                        }
                    }
                    Err(e) => {
                        println!("Erreur lors de la lecture depuis la connexion : {}", e);
                        return;
                    }
                }
            }
        });
    }
}
