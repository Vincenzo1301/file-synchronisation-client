package se.hh.filesynchronisation.data;

import java.util.Set;

public record FileSyncResult(String clientIdentifier, Set<FileDto> fileMetadata) {}
