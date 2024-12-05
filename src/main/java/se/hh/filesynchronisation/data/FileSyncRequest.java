package se.hh.filesynchronisation.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import se.hh.filesynchronisation.enums.RequestType;

public record FileSyncRequest(String clientIdentifier, Set<FileDto> fileMetadata, RequestType type)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
