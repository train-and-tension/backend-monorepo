package com.traintension.core.model.setResult;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.setResult.SetResultDTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(ApiConstants.SET_RESULT)
@RequiredRole("USER")
@RequiredArgsConstructor
@Tag(name = "Set Result", description = "Kullaniciya ait tamamlanmis set sonucu kayitlari. Tum islemler aktif kullanicinin kayitlariyla sinirlidir.")
public class SetResultController {
    private final SetResultService setResultService;

    @PostMapping
    @Operation(
            summary = "Set sonuclari ekle",
            description = "Belirtilen workout session'a 1-200 arasinda set sonucu ekler. Workout session aktif kullaniciya ait olmali; egzersiz global veya aktif kullaniciya ait olabilir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Set sonuclari olusturuldu"),
            @ApiResponse(responseCode = "400", description = "Gecersiz istek - validation hatasi"),
            @ApiResponse(responseCode = "404", description = "Workout session veya egzersiz bulunamadi")
    })
    public ResponseEntity<List<Response>> addResults(@Valid @RequestBody AddResultsRequest request) {
        return ResponseEntity.ok(setResultService.addResults(request, UserContext.getId()));
    }

    @PatchMapping
    @Operation(
            summary = "Set sonuclarini guncelle",
            description = "Aktif kullaniciya ait set sonuclarini toplu ve kismi olarak gunceller. Sadece gonderilen alanlar degistirilir; ID'ler benzersiz olmalidir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Set sonuclari guncellendi"),
            @ApiResponse(responseCode = "400", description = "Gecersiz istek - validation hatasi"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla set sonucu ya da egzersiz bulunamadi")
    })
    public ResponseEntity<List<Response>> updateResults(@Valid @RequestBody UpdateResultsRequest request) {
        return ResponseEntity.ok(setResultService.updateResults(request, UserContext.getId()));
    }

    @DeleteMapping
    @Operation(
            summary = "Set sonuclarini sil",
            description = "Aktif kullaniciya ait set sonuclarini UUID listesiyle siler. Bos veya null liste gonderilirse bos liste dondurulur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Silinen set sonucu UUID'leri donduruldu"),
            @ApiResponse(responseCode = "404", description = "Bir veya daha fazla set sonucu bulunamadi")
    })
    public ResponseEntity<List<UUID>> deleteResults(@RequestBody Set<UUID> ids) {
        return ResponseEntity.ok(setResultService.deleteResults(ids, UserContext.getId()));
    }
}
