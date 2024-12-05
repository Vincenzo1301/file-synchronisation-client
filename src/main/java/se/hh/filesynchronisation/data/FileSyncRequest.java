package se.hh.filesynchronisation.data;

import java.util.Set;
import se.hh.filesynchronisation.enums.RequestType;

public record FileSyncRequest(
    String clientIdentifier, Set<FileDto> fileMetadata, RequestType type) {}
