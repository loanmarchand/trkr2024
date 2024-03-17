use std::io::Read;

use tokio::io::{self, AsyncWriteExt};

mod tls;

#[tokio::main]
async fn main() -> io::Result<()> {
    tls::connect_tls().await
}