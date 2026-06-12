package com.traintension.core.model.equipment;

import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import com.traintension.core.common.annotations.uniqueIdsValidation.HasId;
import com.traintension.core.common.annotations.uniqueIdsValidation.UniqueIds;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Ekipman DTO'ları")
public class EquipmentDTO {

    //------------------CREATE LIST---------------------------//
    @Schema(description = "Toplu global ekipman oluşturma isteği")
    public record CreateGlobalsRequest(
            @Size(min = 1, max = 500)
            @Valid
            @NotNull
            @Schema(description = "Oluşturulacak ekipman listesi (1-500 arası)")
            List<CreateGlobalRequest> items
    ) {
    }

    @Schema(description = "Toplu kişisel ekipman oluşturma isteği")
    public record CreatePersonalsRequest(
            @Size(min = 1, max = 100)
            @Valid
            @NotNull
            @Schema(description = "Oluşturulacak ekipman listesi (1-100 arası)")
            List<CreatePersonalRequest> items
    ) {

    }


    //-------------------------UPDATE LIST----------------------------//
    @Schema(description = "Toplu global ekipman güncelleme isteği")
    public record UpdateGlobalsRequest(
            @UniqueIds
            @Size(min = 1, max = 500)
            @Valid
            @Schema(description = "Güncellenecek ekipman listesi (ID'ler benzersiz olmalı)")
            List<UpdateGlobalRequest> items
    ) {
    }

    @Schema(description = "Toplu kişisel ekipman güncelleme isteği")
    public record UpdatePersonalsRequest(
            @Size(min = 1, max = 100)
            @UniqueIds
            @Valid
            @NotNull
            @Schema(description = "Güncellenecek ekipman listesi (ID'ler benzersiz olmalı)")
            List<UpdatePersonalRequest> items
    ) {
    }

    //----------------------CREATE SINGLE------------------------//
    @Schema(description = "Tek kişisel ekipman oluşturma isteği")
    public record CreatePersonalRequest(
            @Size(max = 100)
            @NotBlank
            @Schema(description = "Ekipman adı", example = "Barbell")
            String name,
            @Size(max = 1000)
            @Schema(description = "Ekipman açıklaması (opsiyonel)", example = "Olimpik halter çubuğu")
            String description,
            @Size(max = 2048)
            @Schema(description = "Ekipman medya URL'i (opsiyonel)", example = "https://cdn.example.com/barbell.jpg")
            String mediaUrl
    ) {

    }

    @Schema(description = "Tek global ekipman oluşturma isteği")
    public record CreateGlobalRequest(
            @NotBlank
            @Size(max = 100)
            @Schema(description = "Ekipman adı", example = "Dumbbell")
            String name,
            @Size(max = 1000)
            @Schema(description = "Ekipman açıklaması (opsiyonel)")
            String description,
            @Size(max = 2048)
            @Schema(description = "Ekipman medya URL'i (opsiyonel)")
            String mediaUrl
    ) {

    }


    //------------------------UPDATE SINGLE---------------------------//
    @Schema(description = "Tek kişisel ekipman güncelleme isteği. Sadece gönderilen alanlar güncellenir.")
    public record UpdatePersonalRequest(
            @ValidUUIDv7
            @NotNull
            @Schema(description = "Güncellenecek ekipmanın UUID'si")
            UUID id,
            @Size(max = 100)
            @Schema(description = "Yeni ekipman adı (opsiyonel)")
            String name,
            @Size(max = 1000)
            @Schema(description = "Yeni açıklama (opsiyonel)")
            String description,
            @Size(max = 2048)
            @Schema(description = "Yeni medya URL'i (opsiyonel)")
            String mediaUrl
    ) implements HasId {
    }

    @Schema(description = "Tek global ekipman güncelleme isteği. Sadece gönderilen alanlar güncellenir.")
    public record UpdateGlobalRequest(
            @ValidUUIDv7
            @NotNull
            @Schema(description = "Güncellenecek ekipmanın UUID'si")
            UUID id,
            @Size(max = 100)
            @Schema(description = "Yeni ekipman adı (opsiyonel)")
            String name,
            @Size(max = 1000)
            @Schema(description = "Yeni açıklama (opsiyonel)")
            String description,
            @Size(max = 2048)
            @Schema(description = "Yeni medya URL'i (opsiyonel)")
            String mediaUrl
    ) implements HasId {
    }

    @Builder
    @Schema(description = "Ekipman yanıt modeli")
    public record Response(
            @Schema(description = "Ekipman UUID'si")
            UUID id,
            @Schema(description = "Ekipman adı")
            String name,
            @Schema(description = "Ekipman açıklaması")
            String description,
            @Schema(description = "Medya URL'i")
            String mediaUrl,
            @Schema(description = "Oluşturulma tarihi")
            OffsetDateTime createdAt,
            @Schema(description = "Optimistic locking version numarası")
            Long version
    ) {
    }
}
