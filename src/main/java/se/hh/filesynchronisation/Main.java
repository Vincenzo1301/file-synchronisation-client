package se.hh.filesynchronisation;

import se.hh.filesynchronisation.service.Client;

public class Main {
  public static void main(String[] args) {
    String directory = "src/main/resources";

    Client client = new Client("localhost", 8080, "Roberto");
    client.syncFileMetadata(directory);
  }
}
