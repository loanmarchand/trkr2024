
# Trkr 2024

## Prérequis
* Java SDK 20
* Installer rustup 
```Sous unix : curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh```

```Sous Windows : https://static.rust-lang.org/rustup/dist/i686-pc-windows-gnu/rustup-init.exe```

## Installation
1. ```git clone https://git.helmo.be/students/info/e200249/trkr2024```
2. ```cd trkr2024```
3. Modifier les fichier config dans json/src/main/resources avec votre interface :
Il faut modifier cette ligne :   "multicastInterface" : "enxf8e43bbeaf49" par votre interface.
4. Modifier votre fichier host pour ajouter l'address ip du monitorDaemon : trkr.labo24.swilabus.com 'votreIp'

## Client
1. cd client_rust
2. cargo build
3. cargo run
4. ou version .exe : client_rust/target/release/client_rust.exe

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

## Exemple de configuration monitorDaemon
```json
{
  "multicastAddress" : "224.1.1.255",
  "multicastPort" : 60203,
  "multicastInterface" : "enxf8e43bbeaf49",
  "clientPort" : 60223,
  "tls" : true,
  "aesKey" : "853QlfQasa2OJQlokSYPUZwzhH25sWmcvBkV1yD0Q1yDbf4uB/SHVVdGphA7V2nA4iE78EUikhj95iltTO98gKj3ueX/KxXRkkXtUL+Vk8Ep+xghJk4Ydtm+VDmkQfwVY/3OPLa4HjkGZOZ8Bge+Bg16tdpiUxTwYD+g62NgLpSODP1m/zcOVA8WJ9eUxL+gSrmCtnlpeEc6OQoI8rDOBHgBlfFSk0E+dE0iX2m/HDauUdG7Q4KAD16pH5Wfrgte4ZF449twZMZzm2Hg9JmVPuR0cuEw0qkeeclwN+ZDPN6aVy0s2uwG0RgpNn2rjCKNrNzMcALWckzpAp9plU8TcQ==",
  "protocolsDelay" : {
    "snmp" : 120,
    "http" : 120,
    "imap" : 120
  },
  "probes" : {
    "http2" : "http2!https://www.swilabus.be/!0!2000",
    "http3" : "http3!https://www.swilabus.com/trkr1!0!1700",
    "http4" : "http4!https://www.swilabus.com/trkr2!0!1800",
    "snmp1" : "snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
    "snmp2" : "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999",
    "imap1" : "imap1!imap://trkr@credentials.json:0/!0!10",
    "imap2" : "imap2!imap://trkr2@credentials.json:0/!0!10",
    "http1" : "http1!https://www.swilabus.com/!0!1500"
  }
}
```

## Exemple de configuration sonde
```json
{
  "protocol": "http",
  "multicastAddress": "224.1.1.255",
  "multicastPort": 60203,
  "multicastInterface": "enxf8e43bbeaf49",
  "multicastDelay": 90,
  "unicastPort": 60213,
  "aesKey": "853QlfQasa2OJQlokSYPUZwzhH25sWmcvBkV1yD0Q1yDbf4uB/SHVVdGphA7V2nA4iE78EUikhj95iltTO98gKj3ueX/KxXRkkXtUL+Vk8Ep+xghJk4Ydtm+VDmkQfwVY/3OPLa4HjkGZOZ8Bge+Bg16tdpiUxTwYD+g62NgLpSODP1m/zcOVA8WJ9eUxL+gSrmCtnlpeEc6OQoI8rDOBHgBlfFSk0E+dE0iX2m/HDauUdG7Q4KAD16pH5Wfrgte4ZF449twZMZzm2Hg9JmVPuR0cuEw0qkeeclwN+ZDPN6aVy0s2uwG0RgpNn2rjCKNrNzMcALWckzpAp9plU8TcQ=="
}
```
  

## Auteurs
* [COIBION ROMAIN](https://git.helmo.be/students/info/q210027)
* [MARCHAND LOAN](https://git.helmo.be/students/info/e200249)
* [REZETTE JILLIAN](https://git.helmo.be/students/info/q210099)
* [RODERKERKEN THIBAUT](https://git.helmo.be/students/info/q210069)



