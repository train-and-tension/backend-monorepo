# Core Controller API Dokumantasyonu

Bu dokuman, `src/main/java/com/traintension/core/model` altindaki controller sozlesmelerini AI ajanlarinin hizli yorumlayabilmesi icin yazilmistir. Kaynak kod her zaman ana dogruluk kaynagidir; bu dosya controller davranisini, rol sinirlarini ve veri sahipligi kurallarini ozetler.

## Genel Kurallar

- Servis Spring Boot ve MVC controller yapisini kullanir.
- Public model endpointleri `/api/core/**` altindadir.
- Admin endpointleri `/api/core/admin/**` altindadir ve `@RequiredRole("ADMIN")` ister.
- User endpointleri `@RequiredRole("USER")` ister ve aktif kullanici UUID'sini `UserContext.getId()` ile alir.
- Inline endpointler public API degildir. `UserProfileInlineController` `@Hidden` ile Swagger disinda tutulur.
- Request body validasyonu Jakarta Validation annotation'lari ile DTO seviyesinde yapilir.
- UUID alanlarinda genellikle UUIDv7 validasyonu DTO uzerindedir.
- Batch create/update/delete endpointlerinde eksik ownership veya eksik kayit `NotFoundException` davranisina gider.
- Personal kayitlarda servis katmani `USER_PROFILE_ID.eq(userId)` filtresini korumalidir.
- Global/system kayitlarda `USER_PROFILE_ID.isNull()` filtresi beklenir.

## Hata Sozlesmesi

- `400`: Validation veya domain kurali ihlali.
- `404`: Kayit bulunamadi, ownership uyusmazligi, parent-child uyusmazligi veya batch count mismatch.
- `409`: Duplicate key gibi cakisma durumlari.
- `503`: Retry edilebilir DB serialization/deadlock durumlari.
- Detayli SQL state cevirileri `DatabaseExceptionHandler` tarafindan yapilir.

## Controller Indeksi

### Body Information

- Base path: `/api/core/body-information`
- Role: `USER`
- Controller: `BodyInformationController`
- Endpoints:
  - `POST /api/core/body-information`: Kullanicinin body information kaydini olusturur. Body: `CreatePersonalRequest`. Response: `BodyInformationDTO.Response`.
  - `PUT /api/core/body-information/measurements`: Olcum alanlarini gunceller. Body: `UpdateMeasurementsRequest`. Response: `BodyInformationDTO.Response`.
  - `PATCH /api/core/body-information/profile`: Profil odakli alanlari kismi gunceller. Body: `UpdateProfileRequest`. Response: `BodyInformationDTO.Response`.

### Equipment

- User base path: `/api/core/equipments`
- Admin base path: `/api/core/admin/equipments`
- User controller: `UserEquipmentController`
- Admin controller: `AdminEquipmentController`
- User endpoints:
  - `POST /api/core/equipments`: Personal ekipmanlari toplu olusturur. Body: `CreatePersonalsRequest`. Response: `List<EquipmentDTO.Response>`.
  - `PATCH /api/core/equipments`: Personal ekipmanlari toplu ve kismi gunceller. Body: `UpdatePersonalsRequest`. Response: `List<EquipmentDTO.Response>`.
  - `DELETE /api/core/equipments`: Personal ekipmanlari UUID setiyle siler. Body: `Set<UUID>`. Response: silinen UUID listesi.
- Admin endpoints:
  - `POST /api/core/admin/equipments`: Global ekipmanlari toplu olusturur.
  - `GET /api/core/admin/equipments/{id}`: Global ekipmani ID ile getirir.
  - `GET /api/core/admin/equipments`: Tum global ekipmanlari listeler.
  - `PATCH /api/core/admin/equipments`: Global ekipmanlari toplu ve kismi gunceller.
  - `DELETE /api/core/admin/equipments`: Global ekipmanlari UUID setiyle siler.

### Exercise

- User base path: `/api/core/exercises`
- Admin base path: `/api/core/admin/exercises`
- User controller: `UserExerciseController`
- Admin controller: `AdminExerciseController`
- User endpoints:
  - `POST /api/core/exercises`: Personal egzersizleri toplu olusturur.
  - `PATCH /api/core/exercises`: Personal egzersizleri toplu ve kismi gunceller.
  - `DELETE /api/core/exercises`: Personal egzersizleri UUID setiyle siler.
  - `POST /api/core/exercises/{id}/favorite`: Global veya personal egzersizin favori durumunu gunceller. Body: `FavoriteRequest`.
- Admin endpoints:
  - `POST /api/core/admin/exercises`: Global egzersizleri toplu olusturur.
  - `PATCH /api/core/admin/exercises`: Global egzersizleri toplu ve kismi gunceller.
  - `GET /api/core/admin/exercises/{id}`: Global egzersizi ID ile getirir.
  - `GET /api/core/admin/exercises`: Tum global egzersizleri listeler.
  - `DELETE /api/core/admin/exercises`: Global egzersizleri UUID setiyle siler.

### Exercise Muscle

- User base path: `/api/core/exercise-muscle`
- Admin base path: `/api/core/admin/exercise-muscle`
- User controller: `UserExerciseMuscleController`
- Admin controller: `AdminExerciseMuscleController`
- User endpoints:
  - `POST /api/core/exercise-muscle`: Personal egzersiz-kas iliskilerini toplu olusturur.
  - `DELETE /api/core/exercise-muscle`: Personal iliskileri UUID setiyle siler.
- Admin endpoints:
  - `POST /api/core/admin/exercise-muscle`: Global egzersiz-kas iliskilerini toplu olusturur.
  - `DELETE /api/core/admin/exercise-muscle`: Global iliskileri UUID setiyle siler.
  - `GET /api/core/admin/exercise-muscle`: Tum global iliskileri listeler.
  - `GET /api/core/admin/exercise-muscle/{id}`: Iliskiyi ID ile getirir.
  - `GET /api/core/admin/exercise-muscle/find-muscles-by-exercise/{exerciseId}`: Egzersize bagli kaslari getirir.
  - `GET /api/core/admin/exercise-muscle/find-exercises-by-muscle/{muscleId}`: Kasa bagli egzersizleri getirir.

### Muscle

- Admin base path: `/api/core/admin/muscles`
- Role: `ADMIN`
- Controller: `AdminMuscleController`
- Endpoints:
  - `POST /api/core/admin/muscles`: Global kaslari toplu olusturur.
  - `PATCH /api/core/admin/muscles`: Global kaslari toplu ve kismi gunceller.
  - `DELETE /api/core/admin/muscles`: Global kaslari UUID setiyle siler.
  - `GET /api/core/admin/muscles/{id}`: Global kasi ID ile getirir.
  - `GET /api/core/admin/muscles`: Tum global kaslari listeler.

### Saved Exercise

- Base path: `/api/core/saved-exercise`
- Role: `USER`
- Controller: `UserSavedExerciseController`
- Endpoints:
  - `POST /api/core/saved-exercise`: Kullanicinin kayitli egzersizlerini toplu olusturur.
  - `DELETE /api/core/saved-exercise`: Kayitli egzersizleri egzersiz UUID setiyle siler.

### Set Result

- Base path: `/api/core/set-results`
- Role: `USER`
- Controller: `SetResultController`
- Endpoints:
  - `POST /api/core/set-results`: Bir workout session altina set sonuclari ekler. Body: `AddResultsRequest`. Response: `List<SetResultDTO.Response>`.
  - `PATCH /api/core/set-results`: Set sonuclarini toplu ve kismi gunceller. Body: `UpdateResultsRequest`. Response: `List<SetResultDTO.Response>`.
  - `DELETE /api/core/set-results`: Set sonuclarini UUID setiyle siler. Body: `Set<UUID>`. Response: silinen UUID listesi.
- Ownership: Workout session ve set result aktif kullaniciya ait olmalidir. Egzersiz global veya aktif kullaniciya ait olabilir.

### Target Set

- User base path: `/api/core/target-sets`
- Admin base path: `/api/core/admin/target-sets`
- User controller: `UserTargetSetController`
- Admin controller: `AdminTargetSetController`
- User endpoints:
  - `POST /api/core/target-sets`: Personal target setleri workout day altinda toplu olusturur.
  - `PATCH /api/core/target-sets`: Personal target setleri toplu ve kismi gunceller.
  - `PUT /api/core/target-sets/reorder`: Personal target set sirasini gunceller.
  - `DELETE /api/core/target-sets`: Personal target setleri siler.
  - `GET /api/core/target-sets`: Workout day icin personal target setleri getirir.
  - `GET /api/core/target-sets/by-workout-program`: Workout program icin personal target setleri gruplu getirir.
  - `GET /api/core/target-sets/by-ids`: ID listesiyle personal target setleri getirir.
- Admin endpoints ayni davranisin global/system versiyonudur ve `/api/core/admin/target-sets` altindadir.

### User Profile

- User base path: `/api/core/user-profiles`
- Admin base path: `/api/core/admin/user-profiles`
- User controller: `UserProfileController`
- Admin controller: `AdminUserProfileController`
- User endpoints:
  - `GET /api/core/user-profiles`: Aktif kullanicinin profilini getirir.
  - `PATCH /api/core/user-profiles`: Aktif kullanicinin profilini kismi gunceller.
  - `PUT /api/core/user-profiles/timezone`: Aktif kullanicinin IANA timezone bilgisini gunceller.
  - `PUT /api/core/user-profiles/username`: Aktif kullanicinin username bilgisini gunceller.
  - `GET /api/core/user-profiles/username/available?username=...`: Username musaitligini kontrol eder.
- Admin endpoints:
  - `GET /api/core/admin/user-profiles/{id}`: Kullanici profilini ID ile getirir.
- Internal endpoints:
  - Base path: `/inline/api/core/user-profile`
  - Swagger: hidden.
  - `GET /{id}/exists`, `POST /`, `DELETE /{id}` auth/user servisleri arasi internal akislara hizmet eder.

### Workout Day

- User base path: `/api/core/workout-days`
- Admin base path: `/api/core/admin/workout-days`
- User controller: `UserWorkoutDayController`
- Admin controller: `AdminWorkoutDayController`
- User endpoints:
  - `POST /api/core/workout-days/{workoutProgramId}`: Personal workout dayleri program altinda toplu olusturur.
  - `DELETE /api/core/workout-days/{workoutProgramId}`: Program altindaki personal workout dayleri UUID setiyle siler.
  - `PUT /api/core/workout-days/{workoutProgramId}/reorder`: Program altindaki personal workout day siralarini gunceller.
  - `PATCH /api/core/workout-days/{workoutProgramId}`: Program altindaki personal workout dayleri toplu ve kismi gunceller.
- Admin endpoints:
  - `POST /api/core/admin/workout-days/{workoutProgramId}`: Global workout dayleri program altinda toplu olusturur.
  - `DELETE /api/core/admin/workout-days/{workoutProgramId}`: Program altindaki global workout dayleri siler.
  - `PUT /api/core/admin/workout-days/{workoutProgramId}/reorder`: Global workout day siralarini gunceller.
  - `GET /api/core/admin/workout-days`: UUID setiyle global workout day listesi getirir.
  - `PATCH /api/core/admin/workout-days/{workoutProgramId}`: Global workout dayleri toplu ve kismi gunceller.
  - `GET /api/core/admin/workout-days/{workoutProgramId}`: Program altindaki global workout dayleri getirir.

### Workout Program

- User base path: `/api/core/workout-programs`
- Admin base path: `/api/core/admin/workout-programs`
- User controller: `UserWorkoutProgramController`
- Admin controller: `AdminWorkoutProgramController`
- User endpoints:
  - `POST /api/core/workout-programs`: Personal workout program olusturur.
  - `PATCH /api/core/workout-programs`: Personal workout program gunceller.
  - `DELETE /api/core/workout-programs`: Personal workout programlari UUID setiyle siler.
  - `POST /api/core/workout-programs/{id}/duplicate`: Global veya personal programi kullaniciya kopyalar.
  - `POST /api/core/workout-programs/{id}/deactivate`: Aktif personal programi pasif hale getirir.
  - `POST /api/core/workout-programs/{id}/activate`: Personal programi aktif hale getirir.
- Admin endpoints:
  - `POST /api/core/admin/workout-programs`: Global workout programlari toplu olusturur.
  - `PATCH /api/core/admin/workout-programs`: Global workout programlari toplu ve kismi gunceller.
  - `GET /api/core/admin/workout-programs/{id}`: Global workout programi ID ile getirir.
  - `GET /api/core/admin/workout-programs`: Tum global workout programlari listeler.
  - `DELETE /api/core/admin/workout-programs`: Global workout programlari UUID setiyle siler.

### Workout Session

- Base path: `/api/core/workout-sessions`
- Role: `USER`
- Controller: `WorkoutSessionController`
- Endpoints:
  - `POST /api/core/workout-sessions/quick-workout`: Aktif workout period icinde FINISHED durumunda QUICK_WORKOUT session olusturur ve set sonuclarini kaydeder. Body: `SaveQuickExerciseRequest`. Response: `SaveQuickExerciseResponse`.
- Preconditions: Kullanici icin aktif workout period bulunmalidir. Her set sonucu egzersizi global veya kullaniciya ait olmalidir.

## Swagger Durumu

- Public controller endpointlerinde `@Tag`, `@Operation` ve temel `@ApiResponse` dokumantasyonu bulunmalidir.
- `SetResultController` ve `WorkoutSessionController` public Swagger dokumantasyonu tamamlanmistir.
- `UserProfileInlineController` bilerek `@Hidden` oldugu icin Swagger dokumani beklenmez.

## AI Icin Degisiklik Rehberi

- Yeni public endpoint eklendiginde once `ApiConstants` icine path sabiti ekle veya mevcut sabiti kullan.
- Controller'a `@Tag`, her mapping methoduna `@Operation` ve beklenen `@ApiResponse` degerlerini ekle.
- Request/response semantigini DTO record isimleriyle eslestir.
- User endpointlerinde `UserContext.getId()` disinda kullanici ID'si alma.
- Admin endpointlerde global veri bekleniyorsa `USER_PROFILE_ID.isNull()` semantigini bozma.
- Parent-child iliskilerinde sadece child ID ile islem yapma; parent ID ve gerekiyorsa user ID birlikte dogrulanmalidir.
- Schema degisikligi yoksa generated jOOQ dosyalarina dokunma.
