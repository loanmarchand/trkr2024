use std::io::prelude::*;
use std::net::TcpStream;
use std::error::Error;

fn main() -> Result<(), Box<dyn Error>> {
    // Adresse du serveur
    let mut stream = TcpStream::connect("192.168.1.26:7878")?;

    // Message à envoyer
    let msg = "Bonjour du client !";

    // Envoi du message
    stream.write_all(msg.as_bytes())?;

    println!("Message envoyé au serveur : {}", msg);
    Ok(())
}
