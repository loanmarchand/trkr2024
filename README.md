
# Trkr 2024

## Prérequis
* Java SDK 20
* Installer rustup 
```Sous unix : curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh```

```Sous Windows : https://static.rust-lang.org/rustup/dist/i686-pc-windows-gnu/rustup-init.exe```

## Installation
1. git clone https://git.helmo.be/students/info/e200249/trkr2024
2. cd trkr2024
3. Modifier les fichier config dans json/src/main/resources avec votre interface :
Il faut modifier cette ligne : multicastInterface : ""

## Client
1. cd client_rust
2. cargo build
3. cargo run

## Sonde 
1. Lancer le projet sous intelIj
2. Reload All gralde projects
3. Lancer ProbeRunner avec un argument : http ou snmp ou imap

## MonitorDaemon
1. Lancer le projet sous intelIj
2. Reload All gralde projects
3. Lancer MonitorDaemon

## Ordre de lancement
Il faut lancer le MonitorDaemon avant le client.



