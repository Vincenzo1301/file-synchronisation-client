package se.hh.filesynchronisation.service;

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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import se.hh.filesynchronisation.data.FileBackupRequest;
import se.hh.filesynchronisation.data.FileDto;
import se.hh.filesynchronisation.data.FileSyncRequest;
import se.hh.filesynchronisation.data.FileSyncResult;

public class Client {

  private final UUID clientIdentifier;
  private final Socket clientSocket;
  private final ObjectOutputStream out;
  private final ObjectInputStream in;
  private final Set<File> files;

  public Client(String host, int port) {
    try {
      clientIdentifier = UUID.randomUUID();
      clientSocket = new Socket(host, port);
      out = new ObjectOutputStream(clientSocket.getOutputStream());
      in = new ObjectInputStream(clientSocket.getInputStream());
      files = new HashSet();
    } catch (Exception e) {
      throw new RuntimeException("Something went wrong while creating the client", e);
    }
  }

  /**
   * Closes the client including the input and output streams and the client socket.
   *
   * @throws RuntimeException if something went wrong while closing the client
   */
  public void close() {
    try {
      if (in != null) in.close();
      if (out != null) out.close();
      if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
    } catch (Exception e) {
      throw new RuntimeException("Something went wrong while closing the client", e);
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
    FileSyncRequest syncRequest = new FileSyncRequest(clientIdentifier.toString(), fileDtos);

    try {
      out.writeObject(syncRequest);
      out.flush();
      Object o = in.readObject();
      if (o instanceof FileSyncResult response) {
        System.out.println("Received response from server: " + response);
        FileBackupRequest backupRequest = new FileBackupRequest(clientIdentifier.toString(), response.fileMetadata());
        out.writeObject(backupRequest);
        out.flush();
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
}
