package com.traintension.core.model.sync;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Sync DTO'lari")
public class SyncDTO {
    public enum Operation {
        UPSERT,
        DELETE
    }

    @Schema(description = "Cursor tabanli sync yaniti")
    public record Response(
            @Schema(description = "Version'a gore sirali sync event listesi")
            List<Event> items,
            @Schema(description = "Sistem/global kayit sync kapsayicisi")
            Scope system,
            @Schema(description = "Kullaniciya ait personal kayit sync kapsayicisi")
            Scope personal,
            @Schema(description = "Sonraki sayfa var mi?")
            boolean hasNext,
            @Schema(description = "Sonraki istekte afterVersion olarak gonderilecek deger")
            Long nextAfterVersion,
            @Schema(description = "Sonraki sayfanin ilk kaydinin version degeri")
            Long nextPageFirstVersion
    ) {}

    @Schema(description = "Sync event kapsam kapsayicisi")
    public record Scope(
            @Schema(description = "Bu kapsama ait sync event listesi")
            List<Event> items
    ) {}

    @Schema(description = "Tek sync event modeli")
    public record Event(
            @Schema(description = "Global sync version degeri")
            Long version,
            @Schema(description = "Kaydin ait oldugu tablo")
            String tableName,
            @Schema(description = "Event tipi")
            Operation operation,
            @Schema(description = "Etkilenen kaydin UUID'si")
            UUID recordId,
            @Schema(description = "UPSERT eventlerinde kaydin tablo alanlari")
            Map<String, Object> data,
            @Schema(description = "DELETE eventlerinde silinme bilgisi")
            Deleted deleted
    ) {}

    @Schema(description = "Silinmis kayit sync modeli")
    public record Deleted(
            @Schema(description = "Silinen kaydin ait oldugu tablo")
            String tableName,
            @Schema(description = "Silinen kaydin UUID'si")
            UUID recordId,
            @Schema(description = "Kaydin sahibi kullanici UUID'si. Sistem kayitlarinda null olabilir.")
            UUID userProfileId,
            @Schema(description = "Silme eventinin global version degeri")
            Long version,
            @Schema(description = "Silme eventinin olusma zamani")
            OffsetDateTime createdAt
    ) {}
}
