package se.hh.filesynchronisation.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public record FileBackupRequest(String clientIdentifier, Set<FileDto> filesToBackup)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
