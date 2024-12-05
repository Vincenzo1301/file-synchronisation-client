package se.hh.filesynchronisation.data;

import java.util.Set;
import se.hh.filesynchronisation.enums.RequestType;

public record FileBackupRequest(
    String clientIdentifier, Set<FileDto> filesToBackup, RequestType type) {}
