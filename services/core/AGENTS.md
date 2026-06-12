# AGENTS.md

Bu dosya, core servisinde calisan ajanlar ve gelistiriciler icin hizli ama karar-verdirici calisma rehberidir.

## Servis Ozeti

- Bu repo `com.traintension.core` paketinde bir Spring Boot `4.0.5` / Java `25` servisidir.
- Build araci Maven wrapper'dir: Windows icin `.\mvnw.cmd`, Unix icin `./mvnw`.
- Veri erisimi jOOQ `DSLContext` ile yapilir. Generated jOOQ siniflari `src/main/java/com/traintension/core/generated` altindadir.
- Veritabani migrasyonlari Flyway ile `src/main/resources/db/migration` altindadir.
- Runtime bagimliliklari PostgreSQL, Redis, Caffeine cache ve private `com.traintension:common` paketidir.
- Uygulama `@EnableCaching` ve `@EnableScheduling` ile cache ve zamanlama islerini aktif eder.
- API endpoint sabitleri `src/main/java/com/traintension/core/common/config/ApiConstants.java` icinde tutulur.
- Ortak auth/role/user context davranislari private `common` paketinden gelir: `@RequiredRole`, `UserContext`, ortak exception tipleri.

## Komutlar

```powershell
# Tum testler
.\mvnw.cmd test

# Paketleme
.\mvnw.cmd clean package

# CI ile benzer paketleme
.\mvnw.cmd clean package -DskipTests

# Uygulamayi yerelde calistirma
.\mvnw.cmd spring-boot:run

# Flyway migrasyonlarini calistirma
.\mvnw.cmd flyway:migrate

# jOOQ kodlarini yeniden uretme
.\mvnw.cmd jooq-codegen:generate
```

Not: `com.traintension:common` GitHub Packages uzerinden gelir. Maven dependency resolve icin lokal `~/.m2/settings.xml` icinde `github` server credential'i gerekebilir.

## Gerekli Yerel Ayarlar

`application.yaml` su environment variable'lari bekler:

- `CORE_PORT`
- `CORE_DB_HOST` varsayilan `127.0.0.1`
- `CORE_DB_CONTAINER_PORT` varsayilan `5432`
- `CORE_DB_NAME` varsayilan `traintension`
- `CORE_DB_USERNAME`
- `CORE_DB_PASSWORD`
- `REDIS_HOST` varsayilan `127.0.0.1`
- `REDIS_CONTAINER_PORT` varsayilan `6379`
- `REDIS_PASSWORD`
- `SWAGGER_ENABLED` varsayilan `false`
- `GATEWAY_PORT` yalnizca Swagger/OpenAPI config aktifken gerekir
- `LOG_LEVEL_SECURITY`, `LOG_LEVEL_WEB`, `LOG_LEVEL_APP` opsiyonel log seviyesi override'laridir

Testler `@SpringBootTest` ile context actigi icin veritabani/Redis/env eksikse fail edebilir. Test calistirmadan once gerekli env degerlerini ayarla veya ilgili test profili ekle.

## CI ve Docker

- GitHub Actions sadece `test` ve `production` branch push'larinda calisir.
- CI build komutu `mvn -T 1C clean package -DskipTests` seklindedir.
- CI, branch'e gore DockerHub'a `core-test-latest` veya `core-latest` tag'i basar.
- `Dockerfile`, `target/*.jar` dosyasini `app.jar` olarak kopyalar; Docker build oncesi package alinmis olmalidir.
- Docker context icin `.dockerignore` Maven wrapper, testler, markdown ve ara build ciktisini disarida birakir.

## Kod Organizasyonu

- Domain kodlari `src/main/java/com/traintension/core/model/<domain>` altinda gruplanir.
- Mevcut pattern: `Admin*Controller`, `User*Controller`, `*Service`, `*Mapper`, `*DTO`.
- Controller'lar HTTP, role ve validation siniridir; business/veri erisimi service katmaninda kalmalidir.
- User endpointlerinde aktif kullanici `UserContext.getId()` ile alinmali ve service'e `userId` olarak gecilmelidir.
- Mapper siniflari jOOQ `Record` ile DTO arasindaki donusumu yapar; controller veya service icinde uzun DTO mapping yazma.
- DTO'larda Java `record`, Jakarta Validation annotation'lari, Swagger `@Schema` ve gerekirse Lombok `@Builder` kullanimi mevcut stille uyumlu tutulur.
- Inline/internal endpoint desenleri varsa mevcut controller path'lerini izle; public API endpoint'i eklerken once `ApiConstants` icine sabit ekle.

## Auth ve API Kurallari

- Admin endpoint'lerinde mevcut pattern'e uygun olarak `@RequiredRole("ADMIN")` kullanilir.
- Kullaniciya ait endpoint'lerde `@RequiredRole("USER")` kullanilir.
- Admin/global path'ler `ApiConstants.ADMIN_*`, user/personal path'ler `ApiConstants.*` sabitlerini kullanmalidir.
- Swagger annotation'lari mevcut controller/DTO stiliyle paralel tutulmalidir.
- OpenAPI config Swagger aktifken gateway uzerinden erisim varsayar; security scheme bearer JWT olarak tanimlidir.
- CORS config tum path'lerde yaygindir; yeni endpoint icin ek CORS config yazma.

## Veri Sahipligi ve jOOQ

- User-specific kayitlarda `USER_PROFILE_ID.eq(userId)` filtresini asla atlama.
- Global/system kayitlarda `USER_PROFILE_ID.isNull()` filtresini kullan.
- Parent-child islemlerde yalnizca child ID kontrolu yetmez; `workoutProgramId`, `workoutDayId` ve gerekiyorsa `userId` birlikte dogrulanmalidir.
- Toplu update/delete islemlerinde eksik kayit varsa mevcut servislerdeki gibi `NotFoundException` davranisini koru.
- jOOQ update/insert islemlerinde mevcut `returning()` veya batch pattern'ini takip et; davranis farki yaratmadan mekanik refactor yapma.
- DB duplicate veya foreign key hatalarini gereksiz try/catch ile sarmalama; `DatabaseExceptionHandler` SQL state davranisini kullanir.

## DTO ve Validation Kurallari

- Request validation icin Jakarta Validation kullan; custom validation gerekiyorsa `common/annotations` veya domain altindaki mevcut annotation pattern'ini takip et.
- UUID alanlarinda mevcut kullanimla uyumlu olarak `@ValidUUIDv7` kullan.
- Enum alanlarinda `@ValidEnum(enumClass = ...)` pattern'ini koru.
- Toplu requestlerde `items` listesi pattern'ini, `@Valid`, `@NotNull`, `@Size` ve gerekiyorsa `@UniqueIds` ile surdur.
- `@UniqueIds` kullanilacak DTO item'lari `HasId` implement etmelidir.
- Reorder requestlerde `@UniqueOrderNumbers` ve order alanlarinda mevcut min/max sinirlarini koru.
- `targetSet` icin tekrar/sure ayrimini `@ValidRepOrDuration` ve DB check constraint ile uyumlu tut.
- `bodyInformation` icin `@ValidBodyMeasurements`, `@AgeRange` ve domain annotation patternlerini izle.
- Partial update DTO'larinda sadece dolu alanlari `Record` uzerine set eden mapper stilini koru.

## jOOQ ve Veritabani Kurallari

- `src/main/java/com/traintension/core/generated` altindaki jOOQ ciktilarini elle duzenleme.
- Schema degisikligi gerekiyorsa yeni Flyway dosyasi ekle: `V<siradaki_numara>__kisa_aciklama.sql`.
- Migration sonrasi jOOQ siniflarini `.\mvnw.cmd jooq-codegen:generate` ile yeniden uret.
- SQL tarafinda mevcut UUIDv7, `global_version_seq`, optimistic locking `version`, timestamp ve foreign key davranislarini koru.
- Insert/update version atamasi trigger'larla yapilir; elle `version` set etmeye calisma.
- Delete islemleri `deleted_history` trigger'lari ile sync history uretir; delete davranisini degistirirken bu etkiyi hesaba kat.
- Sync indexleri genelde `(user_profile_id, version ASC)` uzerindedir; sync edilecek yeni personal tablolar bu yaklasimla uyumlu olmali.
- `updated_at` olan tablolarda timestamp trigger'lari vardir; uygulama kodunda gereksiz elle timestamp set etme.
- Generated jOOQ ile migration uyumsuzlugu compile hatasi yaratabilir; schema degisikliklerinden sonra generate + compile/test beklenir.

## Cache ve Transaction

- Read methodlarda gerekiyorsa `@Transactional(readOnly = true)` kullan.
- Write methodlarda `@Transactional` kullan.
- Cache'lenen domainlerde global/admin write islemlerinde `@CacheEvict(allEntries = true)` pattern'ini koru.
- Cache key formatini mevcut servislerdeki sekilde acik ve stabil tut: ornekler `id_`, `all`, `global:<id>`.
- Mevcut cache isimlerini koru: `equipments-cache`, `exercise-cache`, `exercise-muscle-cache`, `muscles-cache`, `target-sets-cache`, `workout-days-cache`, `workout-program-cache`.
- Personal write/read akislari cache'lenmediyse yeni cache eklemeden once user-specific key ve invalidation etkisini dusun.

## Zamanlama Isleri

- Scheduler kodlari `model/workoutSession` altindadir.
- `WorkoutSessionScheduler` cron tetigini ve kullanici bazli taramayi yonetir; transactional isler `WorkoutSessionCronHelper` tarafinda yapilir.
- Kullanici tarih/saat hesaplarinda kullanicinin timezone bilgisini dikkate al; gecersiz timezone icin mevcut UTC fallback/log pattern'ini koru.
- Scheduler icinde tek bir kullanicidaki hata diger kullanicilarin islenmesini durdurmamali; mevcut try/catch ve log pattern'ini koru.
- Session status gecislerinde mevcut `PLANNED`, `FINISHED`, `MISSED` ve `WORKOUT`, `OFF`, `QUICK_WORKOUT` enum davranislarini bozma.

## Hata Yonetimi

- `NotFoundException` kaynak bulunamadi, sahiplik uyusmazligi veya batch count mismatch icin mevcut pattern'dir.
- `BadRequestException` domain kurali ihlallerinde kullanilir; ornek olarak gecmis tarih veya aktiflestirme on kosullari.
- `DatabaseExceptionHandler`, jOOQ `DataAccessException` icin SQL state map eder:
  - `23505` duplicate icin `409 CONFLICT`
  - `23503` foreign key icin `404 NOT_FOUND`
  - `23502` validation katmani kacagi olarak `500`
  - `40001` ve `40P01` retry edilebilir durum olarak `503`
- Loglarda request/user context yazilabilir ama sifre, token, auth header veya hassas veri yazma.

## Test ve Dogrulama Beklentisi

- Dar kapsamli degisikliklerde en azindan ilgili unit/integration testlerini veya `.\mvnw.cmd test` komutunu calistir.
- DB schema degisikliginde migration + jOOQ generate sonrasi compile/test calistir.
- Sadece dokuman degisikliginde Maven test zorunlu degildir; final notta test calistirilip calistirilmadigini belirt.
- Testler dis bagimlilik/env bekleyebildigi icin fail olursa eksik env/servis bilgisini final notta acikca belirt.

## Guvenlik ve Secret Hijyeni

- Yeni secret, token veya gercek credential commit etme.
- Yeni konfig degerleri icin env variable kullan ve makul default gerekiyorsa `application.yaml` pattern'ine uygun ekle.
- Lokal `flyway-config.properties` ve `jooq-codegen.xml` gibi dosyalarda credential bulunabilir; bu degerleri AGENTS.md'ye, loglara veya yeni dokumana kopyalama.
- Maven GitHub Packages credential'larini `settings.xml` veya CI secret olarak tut; repoya token yazma.
- Loglarda sifre, token, auth header veya kullaniciya ait hassas veri yazma.

## Git ve Dosya Hijyeni

- Kullaniciya ait mevcut degisiklikleri geri alma.
- `target/`, IDE dosyalari, build artifact'leri ve gereksiz generated ciktisini commit etme.
- jOOQ generated dosyalari yalnizca migration/generate gerektiren schema degisikligi varsa degismelidir.
- Mekanik olmayan kod degisikliklerinde mevcut stil ve paket yapisini koru; ilgisiz refactor yapma.
- Yeni dosyalari mumkunse ASCII ile yaz; mevcut dosyadaki karakter seti ve dil stili neyse ona uy.
