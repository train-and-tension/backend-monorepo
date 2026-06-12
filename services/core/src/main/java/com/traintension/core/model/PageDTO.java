package com.traintension.core.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Sayfalama (pagination) yanit modeli")
public record PageDTO<T>(
        @Schema(description = "Sayfa icerigi") List<T> content,
        @Schema(description = "Sonraki sayfa var mi?") boolean hasNext,
        @Schema(description = "Mevcut sayfa numarasi (0-indexed)") int page,
        @Schema(description = "Sayfa basina kayit sayisi") int size,
        @Schema(description = "Offset degeri (page * size)") int offset
) {
    public static <T> PageDTO<T> of(List<T> content, boolean hasNext, int page, int size) {
        return new PageDTO<>(content, hasNext, page, size, page * size);
    }
}