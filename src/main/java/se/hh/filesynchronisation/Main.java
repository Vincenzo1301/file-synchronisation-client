package se.hh.filesynchronisation;

import se.hh.filesynchronisation.service.Client;

public class Main {

  private static final String HOST = "localhost";
  private static final int PORT = 8080;

  public static void main(String[] args) {
    String directory = args[0];
    String clientIdentifier = args[1];

    Client client = new Client(HOST, PORT, clientIdentifier);
    client.syncFileMetadata(directory);
  }
}
