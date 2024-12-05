package se.hh.filesynchronisation.service;

import static se.hh.filesynchronisation.enums.RequestType.BACKUP_REQUEST;
import static se.hh.filesynchronisation.enums.RequestType.SYNC_REQUEST;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import se.hh.filesynchronisation.data.FileBackupRequest;
import se.hh.filesynchronisation.data.FileDto;
import se.hh.filesynchronisation.data.FileSyncRequest;
import se.hh.filesynchronisation.data.FileSyncResult;

public class Client {

  private final String clientIdentifier;
  private final Socket clientSocket;
  private final ObjectOutputStream out;
  private final ObjectInputStream in;
  private final Set<File> files;
  private final GsonBuilder builder;
  private final Gson gson;

  public Client(String host, int port, String clientIdentifier) {
    try {
      this.clientIdentifier = clientIdentifier;
      this.clientSocket = new Socket(host, port);
      this.out = new ObjectOutputStream(clientSocket.getOutputStream());
      this.in = new ObjectInputStream(clientSocket.getInputStream());
      this.files = new HashSet<>();
      this.builder = new GsonBuilder();
      this.gson = builder.create();
    } catch (Exception e) {
      throw new RuntimeException("Something went wrong while creating the client", e);
    }
  }

  /**
   * Syncs the file metadata of the files in the given directory. <a
   * href="https://stackoverflow.com/questions/1844688/how-can-i-read-all-files-in-a-folder-from-java">Used
   * reference</a>
   *
   * @param directory the directory to sync the file metadata from
   * @throws RuntimeException if something went wrong while syncing the file metadata
   */
  public void syncFileMetadata(String directory) {
    try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
      Stream<Path> pathStream = paths.filter(Files::isRegularFile);
      pathStream.forEach(path -> files.add(path.toFile()));
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong while syncing file metadata", e);
    }

    Set<FileDto> fileDtos = files.stream().map(this::toFileDto).collect(Collectors.toSet());
    FileSyncRequest syncRequest =
        new FileSyncRequest(clientIdentifier, fileDtos, SYNC_REQUEST);

    try {
      String syncRequestJson = gson.toJson(syncRequest);
      out.writeObject(syncRequestJson);
      out.flush();

      String responseJson = (String) in.readObject();
      if (isValidJson(responseJson, FileSyncResult.class)) {
        FileSyncResult response = gson.fromJson(responseJson, FileSyncResult.class);
        System.out.println("-----------------------------------------------------");
        System.out.println("Received file metadata from server which has to be saved.");

        if (!response.fileMetadata().isEmpty()) {
          response
              .fileMetadata()
              .forEach(
                  file ->
                      System.out.printf(
                          "   * %s (Size: %d bytes, Path: %s)%n",
                          file.name(), file.size(), file.path()));

          // Create and send the FileBackupRequest as a JSON string
          FileBackupRequest backupRequest =
              new FileBackupRequest(
                  clientIdentifier, response.fileMetadata(), BACKUP_REQUEST);
          String backupRequestJson = gson.toJson(backupRequest);
          out.writeObject(backupRequestJson);
          out.flush();
        }

        close();
        System.out.println("-----------------------------------------------------");
      } else {
        System.err.println("[WARNING]: Invalid response received from server!");
      }
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("Something went wrong while sending file metadata", e);
    }
  }

  private FileDto toFileDto(File file) {
    String contentType = "unknown";
    try {
      contentType = Files.probeContentType(file.toPath());
    } catch (IOException e) {
      System.err.println("Failed to determine content type for file: " + file.getName());
    }
    return new FileDto(file.getName(), file.length(), contentType, file.getAbsolutePath());
  }

  private boolean isValidJson(String json, Class<?> clazz) {
    try {
      gson.fromJson(json, clazz);
      return true;
    } catch (com.google.gson.JsonSyntaxException e) {
      return false;
    }
  }

  private void close() {
    try {
      if (in != null) in.close();
      if (out != null) out.close();
      if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
    } catch (Exception e) {
      throw new RuntimeException("Something went wrong while closing the client", e);
    }
  }
}
