package se.hh.filesynchronisation;

import se.hh.filesynchronisation.service.Client;

public class Main {
  public static void main(String[] args) {
    String directory = args[0];
    String clientIdentifier = args[1];

    Client client = new Client("localhost", 8080, clientIdentifier);
    client.syncFileMetadata(directory);
  }
}
