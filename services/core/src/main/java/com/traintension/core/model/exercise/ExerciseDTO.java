package com.traintension.core.model.exercise;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traintension.core.common.annotations.UUIDv7Validation.ValidUUIDv7;
import com.traintension.core.common.annotations.enumValidation.ValidEnum;
import com.traintension.core.common.annotations.uniqueIdsValidation.HasId;
import com.traintension.core.common.annotations.uniqueIdsValidation.UniqueIds;
import com.traintension.core.generated.enums.ActivationLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Egzersiz DTO'ları")
public class ExerciseDTO {

    //-------------------------UPDATE LIST-------------------------------//
    @Schema(description = "Toplu kişisel egzersiz güncelleme isteği")
    public record UpdatePersonalsRequest(
            @NotNull
            @Size(min = 1, max = 100)
            @UniqueIds
            @Valid
            @Schema(description = "Güncellenecek egzersiz listesi (ID'ler benzersiz olmalı)")
            List<UpdatePersonalRequest> items
    ) {

    }

    @Schema(description = "Toplu global egzersiz güncelleme isteği")
    public record UpdateGlobalsRequest(
            @NotNull
            @Size(min = 1, max = 1000)
            @UniqueIds
            @Valid
            @Schema(description = "Güncellenecek egzersiz listesi (ID'ler benzersiz olmalı)")
            List<UpdateGlobalRequest> items
    ) {
    }


    //-------------------------CREATE LIST-----------------------------//
    @Schema(description = "Toplu kişisel egzersiz oluşturma isteği")
    public record CreatePersonalsRequest(
            @NotNull
            @Size(min = 1, max = 200)
            @Valid
            @Schema(description = "Oluşturulacak egzersiz listesi (1-200 arası)")
            List<CreatePersonalRequest> items
    ) {
    }

    @Schema(description = "Toplu global egzersiz oluşturma isteği")
    public record CreateGlobalsRequest(
            @NotNull
            @Size(min = 1, max = 200)
            @UniqueIds
            @Valid
            @Schema(description = "Oluşturulacak egzersiz listesi (1-200 arası)")
            List<CreateGlobalRequest> items
    ) {
    }


    //--------------------------CREATE------------------------------//
    @Schema(description = "Tek kişisel egzersiz oluşturma isteği")
    public record CreatePersonalRequest(
            @Size(max = 200)
            @NotBlank
            @Schema(description = "Egzersiz adı", example = "Bench Press")
            String name,

            @NotBlank
            @Schema(description = "Egzersiz açıklaması", example = "Düz bench press hareketi")
            String description,

            @Schema(description = "İlişkili ekipman UUID'si (opsiyonel)")
            UUID equipmentId
    ) {
    }

    @Schema(description = "Tek global egzersiz oluşturma isteği")
    public record CreateGlobalRequest(
            @NotBlank
            @Schema(description = "Egzersiz adı", example = "Squat")
            String name,

            @NotBlank
            @Schema(description = "Egzersiz açıklaması", example = "Back squat hareketi")
            String description,

            @Schema(description = "Egzersiz medya URL'i (opsiyonel)")
            String mediaUrl,

            @Schema(description = "İlişkili ekipman UUID'si (opsiyonel)")
            UUID equipmentId
    ) {
    }

    //---------------------------UPDATE-------------------------------//
    @Schema(description = "Tek kişisel egzersiz güncelleme isteği. Sadece gönderilen alanlar güncellenir.")
    public record UpdatePersonalRequest(
            @NotNull
            @ValidUUIDv7
            @Schema(description = "Güncellenecek egzersizin UUID'si")
            UUID id,
            @Size(max = 200)
            @Schema(description = "Yeni egzersiz adı (opsiyonel)")
            String name,

            @Size(max = 1000)
            @Schema(description = "Yeni açıklama (opsiyonel)")
            String description,

            @Schema(description = "Yeni ekipman UUID'si (opsiyonel)")
            UUID equipmentId
    ) implements HasId {
    }

    @Schema(description = "Tek global egzersiz güncelleme isteği. Sadece gönderilen alanlar güncellenir.")
    public record UpdateGlobalRequest(
            @ValidUUIDv7
            @NotNull
            @Schema(description = "Güncellenecek egzersizin UUID'si")
            UUID id,

            @Schema(description = "Yeni egzersiz adı (opsiyonel)")
            String name,

            @Size(max = 1000)
            @Schema(description = "Yeni açıklama (opsiyonel)")
            String description,

            @Size(max = 2048)
            @Schema(description = "Yeni medya URL'i (opsiyonel)")
            String mediaUrl,

            @Schema(description = "Yeni ekipman UUID'si (opsiyonel)")
            UUID equipmentId,

            @Schema(description = "Birincil kas UUID'si (opsiyonel)")
            UUID primaryMuscleId,

            @Schema(description = "İkincil kas UUID'si (opsiyonel)")
            UUID secondaryMuscleId
    ) implements HasId {
    }

    //--------------------------ACTIONS-------------------------------//
    @Schema(description = "Egzersiz favori durumu guncelleme istegi")
    public record SetFavoriteRequest(
            @JsonProperty("favorite")
            @JsonAlias({"isFavorite", "is_favorite"})
            @NotNull
            @Schema(description = "true ise favoriye ekler, false ise favoriden kaldirir")
            Boolean favorite
    ) {}

    //--------------------------RESPONSE-------------------------------//
    @Builder
    @Schema(description = "Egzersiz yanıt modeli")
    public record Response(
            @Schema(description = "Egzersiz UUID'si")
            UUID id,

            @Schema(description = "Egzersiz adı")
            String name,

            @Schema(description = "Sahip kullanıcı profili UUID'si (kişisel egzersizlerde dolu, global'de null)")
            UUID userProfileId,

            @Schema(description = "Egzersiz açıklaması")
            String description,

            @Schema(description = "Medya URL'i")
            String mediaUrl,

            @Schema(description = "İlişkili ekipman UUID'si")
            UUID equipmentId,

            @Schema(description = "Oluşturulma tarihi")
            OffsetDateTime createdAt,

            @Schema(description = "Optimistic locking version numarası")
            Long version
    ) {
    }


}
