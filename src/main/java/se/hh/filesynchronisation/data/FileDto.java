package se.hh.filesynchronisation.data;

import java.io.Serial;
import java.io.Serializable;

public record FileDto(String name, long size, String contentType, String path)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
