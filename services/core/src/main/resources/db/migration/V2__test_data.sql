-- ============================================================
-- V2__seed_data.sql  –  Traintension Test / Seed Verisi
-- ============================================================
-- İçerik:
--   5 kullanıcı profili + vücut bilgisi + ölçüm geçmişi
--  20 sistem kası (muscle)
--  12 sistem ekipmanı + 10 kullanıcı ekipmanı
--  25 sistem egzersizi + 5 kullanıcı egzersizi
--  ~60 exercise_muscle eşleşmesi
--  ~20 saved_exercise
--  10 workout_program (kullanıcı başı 2, biri aktif)
--  ~50 workout_day
-- ~220 target_set
--  10 workout_period (kullanıcı başı 2, biri aktif)
--  ~50 workout_session (FINISHED / PLANNED / MISSED karışımı)
--
-- NOT: set_result tablosundaki `rep_count` ve `duration` sütunları
--      aynı anda NOT NULL tanımlanmış, ancak chk_rep_or_duration CHECK'i
--      bu iki sütundan tam olarak birinin NULL olmasını bekliyor.
--      Bu çelişki nedeniyle set_result insertleri ayrı bir migration'a
--      (V3) bırakılmıştır; schema düzeltildikten sonra eklenmelidir.
-- ============================================================

-- ============================================================
-- 1. KULLANICI PROFİLLERİ
-- ============================================================
INSERT INTO user_profile (id, first_name, last_name, username, profile_pic_url, timezone)
VALUES ('018ea000-0001-7000-8000-000000000001', 'Ahmet', 'Yılmaz', 'ahmet.yilmaz',
        'https://cdn.traintension.com/avatars/1.jpg', 'Europe/Istanbul'),
       ('018ea000-0002-7000-8000-000000000002', 'Zeynep', 'Kaya', 'zeynep.kaya',
        'https://cdn.traintension.com/avatars/2.jpg', 'Europe/Istanbul'),
       ('018ea000-0003-7000-8000-000000000003', 'Mehmet', 'Demir', 'mehmet.demir', NULL, 'America/New_York'),
       ('018ea000-0004-7000-8000-000000000004', 'Elif', 'Şahin', 'elif.sahin', NULL, 'Europe/Berlin'),
       ('018ea000-0005-7000-8000-000000000005', 'Can', 'Öztürk', 'can.ozturk',
        'https://cdn.traintension.com/avatars/5.jpg', 'Europe/Istanbul');

-- ============================================================
-- 2. VÜCUT BİLGİSİ
-- ============================================================
INSERT INTO body_information (id, user_profile_id, training_goal, weight_goal, weight_kg, height_cm, birth_date, gender,
                              activity_level, unit)
VALUES ('018ea000-0001-7000-8000-000000000101', '018ea000-0001-7000-8000-000000000001', 'HYPERTROPHY', 'GAIN_WEIGHT',
        82.50, 181.00, '1999-05-15', 'MALE', 'ACTIVE', 'METRIC'),
       ('018ea000-0002-7000-8000-000000000102', '018ea000-0002-7000-8000-000000000002', 'HYPERTROPHY',
        'MAINTAIN_WEIGHT', 62.00, 165.00, '2001-03-22', 'FEMALE', 'MODERATE', 'METRIC'),
       ('018ea000-0003-7000-8000-000000000103', '018ea000-0003-7000-8000-000000000003', 'STRENGTH', 'GAIN_WEIGHT',
        95.00, 185.00, '1995-11-08', 'MALE', 'VERY_ACTIVE', 'IMPERIAL'),
       ('018ea000-0004-7000-8000-000000000104', '018ea000-0004-7000-8000-000000000004', 'ENDURANCE', 'LOSE_WEIGHT',
        70.00, 170.00, '1997-07-30', 'FEMALE', 'LIGHT', 'METRIC'),
       ('018ea000-0005-7000-8000-000000000105', '018ea000-0005-7000-8000-000000000005', 'GENERAL_FITNESS',
        'MAINTAIN_WEIGHT', 78.00, 178.00, '2000-01-10', 'MALE', 'MODERATE', 'METRIC');

-- ============================================================
-- 3. ÖLÇÜM GEÇMİŞİ (kullanıcı başı 3 ölçüm)
-- ============================================================
INSERT INTO measurement_history (user_profile_id, weight_kg, height_cm, unit, created_at)
VALUES
    -- Ahmet – kilo alıyor
    ('018ea000-0001-7000-8000-000000000001', 78.00, 181.00, 'METRIC', now() - INTERVAL '90 days'),
    ('018ea000-0001-7000-8000-000000000001', 80.20, 181.00, 'METRIC', now() - INTERVAL '45 days'),
    ('018ea000-0001-7000-8000-000000000001', 82.50, 181.00, 'METRIC', now()),
    -- Zeynep – sabit tutuyor
    ('018ea000-0002-7000-8000-000000000002', 62.50, 165.00, 'METRIC', now() - INTERVAL '90 days'),
    ('018ea000-0002-7000-8000-000000000002', 62.10, 165.00, 'METRIC', now() - INTERVAL '45 days'),
    ('018ea000-0002-7000-8000-000000000002', 62.00, 165.00, 'METRIC', now()),
    -- Mehmet – kilo alıyor (Imperial)
    ('018ea000-0003-7000-8000-000000000003', 88.00, 185.00, 'IMPERIAL', now() - INTERVAL '120 days'),
    ('018ea000-0003-7000-8000-000000000003', 91.50, 185.00, 'IMPERIAL', now() - INTERVAL '60 days'),
    ('018ea000-0003-7000-8000-000000000003', 95.00, 185.00, 'IMPERIAL', now()),
    -- Elif – kilo veriyor
    ('018ea000-0004-7000-8000-000000000004', 76.00, 170.00, 'METRIC', now() - INTERVAL '90 days'),
    ('018ea000-0004-7000-8000-000000000004', 73.00, 170.00, 'METRIC', now() - INTERVAL '45 days'),
    ('018ea000-0004-7000-8000-000000000004', 70.00, 170.00, 'METRIC', now()),
    -- Can – sabit tutuyor
    ('018ea000-0005-7000-8000-000000000005', 77.50, 178.00, 'METRIC', now() - INTERVAL '90 days'),
    ('018ea000-0005-7000-8000-000000000005', 78.20, 178.00, 'METRIC', now() - INTERVAL '45 days'),
    ('018ea000-0005-7000-8000-000000000005', 78.00, 178.00, 'METRIC', now());

-- ============================================================
-- 4. SİSTEM KASLARI (20 adet)
-- ============================================================
-- ID şeması: 018ea000-1001..1020-7000-8000-000000000000
INSERT INTO muscle (id, name, description)
VALUES ('018ea000-1001-7000-8000-000000000000', 'Quadriceps',
        'Uyluk ön kasları; diz ekstansiyonu ve kalça fleksiyonunda birincil rol oynar.'),
       ('018ea000-1002-7000-8000-000000000000', 'Hamstrings',
        'Uyluk arka kasları; diz fleksiyonu ve kalça ekstansiyonundan sorumludur.'),
       ('018ea000-1003-7000-8000-000000000000', 'Glutes',
        'Kalça kasları; kalça ekstansiyonu, abdüksiyon ve dış rotasyonda güç sağlar.'),
       ('018ea000-1004-7000-8000-000000000000', 'Calves',
        'Baldır kasları (gastrocnemius + soleus); ayak bileği plantar fleksiyonu.'),
       ('018ea000-1005-7000-8000-000000000000', 'Chest',
        'Pectoralis major; yatay itme ve omuz adduksiyonunda birincil kastır.'),
       ('018ea000-1006-7000-8000-000000000000', 'Upper Back',
        'Trapezius ve rhomboidler; omuz retraksiyonu ve stabilizasyonu.'),
       ('018ea000-1007-7000-8000-000000000000', 'Lats',
        'Latissimus dorsi; omuz ekstansiyonu ve adduksiyonu, çekme hareketleri.'),
       ('018ea000-1008-7000-8000-000000000000', 'Rear Deltoid',
        'Arka deltoid; omuz ekstansiyonu ve yatay abdüksiyonu.'),
       ('018ea000-1009-7000-8000-000000000000', 'Front Deltoid', 'Ön deltoid; omuz fleksiyonu ve yatay addüksiyonu.'),
       ('018ea000-1010-7000-8000-000000000000', 'Side Deltoid', 'Yan deltoid; omuz abdüksiyonu.'),
       ('018ea000-1011-7000-8000-000000000000', 'Biceps', 'Biceps brachii; dirsek fleksiyonu ve ön kol supinasyonu.'),
       ('018ea000-1012-7000-8000-000000000000', 'Triceps', 'Triceps brachii; dirsek ekstansiyonu.'),
       ('018ea000-1013-7000-8000-000000000000', 'Forearms',
        'Ön kol fleksör ve ekstansörleri; bilek ve parmak kontrolü.'),
       ('018ea000-1014-7000-8000-000000000000', 'Core', 'Rectus abdominis; gövde fleksiyonu ve spinal stabilizasyon.'),
       ('018ea000-1015-7000-8000-000000000000', 'Obliques',
        'İç ve dış oblikler; gövde rotasyonu ve lateral fleksiyon.'),
       ('018ea000-1016-7000-8000-000000000000', 'Lower Back', 'Erector spinae; spinal ekstansiyon ve postür koruma.'),
       ('018ea000-1017-7000-8000-000000000000', 'Hip Flexors', 'İliopsoas; kalça fleksiyonu ve spinal stabilizasyon.'),
       ('018ea000-1018-7000-8000-000000000000', 'Adductors', 'İç uyluk kasları; kalça addüksiyonu ve stabilizasyon.'),
       ('018ea000-1019-7000-8000-000000000000', 'Abductors',
        'Dış uyluk kasları (gluteus medius/minimus); kalça abdüksiyonu.'),
       ('018ea000-1020-7000-8000-000000000000', 'Neck',
        'Sternokleidomastoid ve splenius; baş hareketi ve servikal stabilizasyon.');

-- ============================================================
-- 5. SİSTEM EKİPMANLARI (12 adet)
-- ============================================================
-- ID şeması: 018ea000-2001..2012-7000-8000-000000000000
INSERT INTO equipment (id, user_profile_id, name, description)
VALUES ('018ea000-2001-7000-8000-000000000000', NULL, 'Barbell',
        'Olimpik halter; ağır bileşik hareketler için standart 20 kg''lık bar.'),
       ('018ea000-2002-7000-8000-000000000000', NULL, 'Dumbbell',
        'Dambıl; tek elle veya çift elle izole ve bileşik hareketler.'),
       ('018ea000-2003-7000-8000-000000000000', NULL, 'Cable Machine',
        'Kablo makinesi; ayarlanabilir yükseklik ve açıyla çok yönlü hareketler.'),
       ('018ea000-2004-7000-8000-000000000000', NULL, 'Smith Machine',
        'Smith makinesi; sabit yörüngeli güvenli halter hareketi.'),
       ('018ea000-2005-7000-8000-000000000000', NULL, 'Bench', 'Düz / eğik / negatif ayarlanabilir spor sehpası.'),
       ('018ea000-2006-7000-8000-000000000000', NULL, 'Pull-up Bar',
        'Barfiks çubuğu; pull-up ve chin-up varyasyonları için.'),
       ('018ea000-2007-7000-8000-000000000000', NULL, 'Resistance Band',
        'Direnç bandı; ısınma, yardımcı egzersiz ve mobilite çalışmaları.'),
       ('018ea000-2008-7000-8000-000000000000', NULL, 'Kettlebell',
        'Kettle bell; fonksiyonel güç, kondisyon ve denge çalışmaları.'),
       ('018ea000-2009-7000-8000-000000000000', NULL, 'Leg Press Machine',
        'Bacak baskı makinesi; quads ve glutes için güvenli yükleme.'),
       ('018ea000-2010-7000-8000-000000000000', NULL, 'Lat Pulldown',
        'Lat pulldown makinesi; sırt genişliği egzersizleri.'),
       ('018ea000-2011-7000-8000-000000000000', NULL, 'Treadmill',
        'Koşu bandı; kardiyovasküler kondisyon ve aktif ısınma.'),
       ('018ea000-2012-7000-8000-000000000000', NULL, 'Bodyweight', 'Ekipmansız vücut ağırlığı egzersizleri.');

-- Kullanıcı ekipmanları (her kullanıcıya 2 adet)
INSERT INTO equipment (id, user_profile_id, name, description)
VALUES ('018ea000-2101-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001', 'Home Dumbbell Set',
        'Ev için 5-30 kg ayarlanabilir dambıl seti.'),
       ('018ea000-2102-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001', 'Pull-up Doorframe',
        'Kapı çerçevesine takılan barfiks aparatı.'),
       ('018ea000-2103-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002', 'Yoga Mat',
        'Yer egzersizleri ve esneme için yoga matı.'),
       ('018ea000-2104-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002', 'Mini Bands',
        'Aktivasyon egzersizleri için mini direnç bantları.'),
       ('018ea000-2105-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003', 'Power Rack',
        'Ev jimnastiği için profesyonel power rack.'),
       ('018ea000-2106-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003', 'Lifting Belt',
        'Ağır halter hareketlerinde bel desteği kemeri.'),
       ('018ea000-2107-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004', 'Jump Rope',
        'Kondisyon ve ısınma için atlama ipi.'),
       ('018ea000-2108-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004', 'Foam Roller',
        'Kas gevşetme ve miyofasiyal serbest bırakma.'),
       ('018ea000-2109-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005', 'Adjustable Bench',
        'Ev kullanımı ayarlanabilir spor sehpası.'),
       ('018ea000-2110-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005', 'Ab Wheel',
        'Karın kasları için tekerlek aleti.');

-- ============================================================
-- 6. SİSTEM EGZERSİZLERİ (25 adet)
-- ============================================================
-- ID şeması: 018ea000-3001..3025-7000-8000-000000000000
INSERT INTO exercise (id, user_profile_id, equipment_id, name, description)
VALUES ('018ea000-3001-7000-8000-000000000000', NULL, '018ea000-2001-7000-8000-000000000000', 'Barbell Back Squat',
        'Temel alt vücut hareketi; halteri trapezde taşıyarak derin squat.'),
       ('018ea000-3002-7000-8000-000000000000', NULL, '018ea000-2001-7000-8000-000000000000', 'Barbell Bench Press',
        'Yatay itme; düz sehpada geniş tutuşla göğüs baskısı.'),
       ('018ea000-3003-7000-8000-000000000000', NULL, '018ea000-2001-7000-8000-000000000000', 'Conventional Deadlift',
        'Tam vücut hareketi; halteri yerden hiplere kaldırma.'),
       ('018ea000-3004-7000-8000-000000000000', NULL, '018ea000-2006-7000-8000-000000000000', 'Pull-up',
        'Barfiks; pronasyon tutuşla vücudu çubuğa çekme.'),
       ('018ea000-3005-7000-8000-000000000000', NULL, '018ea000-2001-7000-8000-000000000000', 'Overhead Press',
        'Halteri ayakta baş üzeri itme; omuz ve triceps.'),
       ('018ea000-3006-7000-8000-000000000000', NULL, '018ea000-2001-7000-8000-000000000000', 'Barbell Row',
        'Öne eğik halter çekişi; sırt kalınlık hareketi.'),
       ('018ea000-3007-7000-8000-000000000000', NULL, '018ea000-2001-7000-8000-000000000000', 'Romanian Deadlift',
        'Bacak gergin deadlift; hamstring ve glutes odaklı.'),
       ('018ea000-3008-7000-8000-000000000000', NULL, '018ea000-2009-7000-8000-000000000000', 'Leg Press',
        'Bacak baskı makinesi; quads dominant alt vücut.'),
       ('018ea000-3009-7000-8000-000000000000', NULL, '018ea000-2002-7000-8000-000000000000', 'Incline Dumbbell Press',
        'Eğik sehpada dambıl baskısı; üst göğüs vurgusu.'),
       ('018ea000-3010-7000-8000-000000000000', NULL, '018ea000-2010-7000-8000-000000000000', 'Lat Pulldown',
        'Lat pulldown makinesi; sırt genişliği için temel hareket.'),
       ('018ea000-3011-7000-8000-000000000000', NULL, '018ea000-2003-7000-8000-000000000000', 'Seated Cable Row',
        'Oturarak kablo çekişi; sırt kalınlık hareketi.'),
       ('018ea000-3012-7000-8000-000000000000', NULL, '018ea000-2002-7000-8000-000000000000', 'Dumbbell Curl',
        'Dambıl kürek kemiği; biceps izole hareketi.'),
       ('018ea000-3013-7000-8000-000000000000', NULL, '018ea000-2003-7000-8000-000000000000', 'Tricep Pushdown',
        'Kablo makinesiyle triceps baskısı.'),
       ('018ea000-3014-7000-8000-000000000000', NULL, NULL, 'Leg Extension', 'Makine bazlı quadriceps izole hareketi.'),
       ('018ea000-3015-7000-8000-000000000000', NULL, NULL, 'Leg Curl', 'Makine bazlı hamstring izole hareketi.'),
       ('018ea000-3016-7000-8000-000000000000', NULL, NULL, 'Standing Calf Raise',
        'Baldır makinesi veya basamakta baldır kaldırma.'),
       ('018ea000-3017-7000-8000-000000000000', NULL, '018ea000-2012-7000-8000-000000000000', 'Plank',
        'İzometrik core stabilizasyon egzersizi.'),
       ('018ea000-3018-7000-8000-000000000000', NULL, '018ea000-2012-7000-8000-000000000000', 'Ab Crunch',
        'Üst karın kasları odaklı temel mekik.'),
       ('018ea000-3019-7000-8000-000000000000', NULL, '018ea000-2012-7000-8000-000000000000', 'Russian Twist',
        'Oturarak gövde rotasyonu; oblik odaklı.'),
       ('018ea000-3020-7000-8000-000000000000', NULL, '018ea000-2002-7000-8000-000000000000', 'Dumbbell Lateral Raise',
        'Dambıl yan kaldırma; yan deltoid izole.'),
       ('018ea000-3021-7000-8000-000000000000', NULL, '018ea000-2003-7000-8000-000000000000', 'Face Pull',
        'Kablo makinesi yüz çekişi; arka deltoid ve rotator cuff.'),
       ('018ea000-3022-7000-8000-000000000000', NULL, '018ea000-2012-7000-8000-000000000000', 'Hip Thrust',
        'Sırt üstü kalça itiş; glutes dominant hareket.'),
       ('018ea000-3023-7000-8000-000000000000', NULL, '018ea000-2002-7000-8000-000000000000', 'Dumbbell Lunge',
        'İleri veya geri adım lunges; quads ve glutes.'),
       ('018ea000-3024-7000-8000-000000000000', NULL, '018ea000-2001-7000-8000-000000000000', 'Close Grip Bench Press',
        'Dar tutuşlu bench press; triceps dominant.'),
       ('018ea000-3025-7000-8000-000000000000', NULL, '018ea000-2002-7000-8000-000000000000', 'Hammer Curl',
        'Nötr tutuşlu dambıl curl; brachialis ve biceps.');

-- Kullanıcı egzersizleri (her kullanıcıya 1 özel egzersiz)
INSERT INTO exercise (id, user_profile_id, equipment_id, name, description)
VALUES ('018ea000-3101-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-2102-7000-8000-000000000000', 'Doorframe Pull-up Superset',
        'Kapı barfiks ile wide-grip ve chin-up süper seti.'),
       ('018ea000-3102-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-2103-7000-8000-000000000000', 'Mat Pilates Core Circuit',
        'Yoga matında 5 hareketten oluşan pilates devre antrenmanı.'),
       ('018ea000-3103-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-2105-7000-8000-000000000000', 'Rack Pull',
        'Power rack üst açısından kısmi deadlift; üst sırt ve trap.'),
       ('018ea000-3104-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-2107-7000-8000-000000000000', 'Jump Rope Tabata',
        '20 sn çalışma / 10 sn dinlenme x8 tur atlama ipi tabatası.'),
       ('018ea000-3105-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-2110-7000-8000-000000000000', 'Ab Wheel Rollout',
        'Tekerlek ile tam uzanma veya partial rollout; derin core aktivasyonu.');

-- ============================================================
-- 7. EGZERSİZ–KAS EŞLEŞMELERİ
-- ============================================================
-- ID şeması: 018ea000-4001..40xx-7000-8000-000000000000
INSERT INTO exercise_muscle (id, user_profile_id, exercise_id, muscle_id, activation_level)
VALUES
    -- Back Squat
    ('018ea000-4001-7000-8000-000000000000', NULL, '018ea000-3001-7000-8000-000000000000',
     '018ea000-1001-7000-8000-000000000000', 'PRIMARY'),   -- Quads
    ('018ea000-4002-7000-8000-000000000000', NULL, '018ea000-3001-7000-8000-000000000000',
     '018ea000-1003-7000-8000-000000000000', 'SECONDARY'), -- Glutes
    ('018ea000-4003-7000-8000-000000000000', NULL, '018ea000-3001-7000-8000-000000000000',
     '018ea000-1002-7000-8000-000000000000', 'SECONDARY'), -- Hamstrings
    ('018ea000-4004-7000-8000-000000000000', NULL, '018ea000-3001-7000-8000-000000000000',
     '018ea000-1016-7000-8000-000000000000', 'SECONDARY'), -- Lower Back
    -- Bench Press
    ('018ea000-4005-7000-8000-000000000000', NULL, '018ea000-3002-7000-8000-000000000000',
     '018ea000-1005-7000-8000-000000000000', 'PRIMARY'),   -- Chest
    ('018ea000-4006-7000-8000-000000000000', NULL, '018ea000-3002-7000-8000-000000000000',
     '018ea000-1009-7000-8000-000000000000', 'SECONDARY'), -- Front Delt
    ('018ea000-4007-7000-8000-000000000000', NULL, '018ea000-3002-7000-8000-000000000000',
     '018ea000-1012-7000-8000-000000000000', 'SECONDARY'), -- Triceps
    -- Deadlift
    ('018ea000-4008-7000-8000-000000000000', NULL, '018ea000-3003-7000-8000-000000000000',
     '018ea000-1016-7000-8000-000000000000', 'PRIMARY'),   -- Lower Back
    ('018ea000-4009-7000-8000-000000000000', NULL, '018ea000-3003-7000-8000-000000000000',
     '018ea000-1003-7000-8000-000000000000', 'SECONDARY'), -- Glutes
    ('018ea000-4010-7000-8000-000000000000', NULL, '018ea000-3003-7000-8000-000000000000',
     '018ea000-1002-7000-8000-000000000000', 'SECONDARY'), -- Hamstrings
    ('018ea000-4011-7000-8000-000000000000', NULL, '018ea000-3003-7000-8000-000000000000',
     '018ea000-1006-7000-8000-000000000000', 'SECONDARY'), -- Upper Back
    -- Pull-up
    ('018ea000-4012-7000-8000-000000000000', NULL, '018ea000-3004-7000-8000-000000000000',
     '018ea000-1007-7000-8000-000000000000', 'PRIMARY'),   -- Lats
    ('018ea000-4013-7000-8000-000000000000', NULL, '018ea000-3004-7000-8000-000000000000',
     '018ea000-1011-7000-8000-000000000000', 'SECONDARY'), -- Biceps
    ('018ea000-4014-7000-8000-000000000000', NULL, '018ea000-3004-7000-8000-000000000000',
     '018ea000-1006-7000-8000-000000000000', 'SECONDARY'), -- Upper Back
    -- Overhead Press
    ('018ea000-4015-7000-8000-000000000000', NULL, '018ea000-3005-7000-8000-000000000000',
     '018ea000-1009-7000-8000-000000000000', 'PRIMARY'),   -- Front Delt
    ('018ea000-4016-7000-8000-000000000000', NULL, '018ea000-3005-7000-8000-000000000000',
     '018ea000-1010-7000-8000-000000000000', 'SECONDARY'), -- Side Delt
    ('018ea000-4017-7000-8000-000000000000', NULL, '018ea000-3005-7000-8000-000000000000',
     '018ea000-1012-7000-8000-000000000000', 'SECONDARY'), -- Triceps
    -- Barbell Row
    ('018ea000-4018-7000-8000-000000000000', NULL, '018ea000-3006-7000-8000-000000000000',
     '018ea000-1007-7000-8000-000000000000', 'PRIMARY'),   -- Lats
    ('018ea000-4019-7000-8000-000000000000', NULL, '018ea000-3006-7000-8000-000000000000',
     '018ea000-1006-7000-8000-000000000000', 'SECONDARY'), -- Upper Back
    ('018ea000-4020-7000-8000-000000000000', NULL, '018ea000-3006-7000-8000-000000000000',
     '018ea000-1011-7000-8000-000000000000', 'SECONDARY'), -- Biceps
    -- Romanian Deadlift
    ('018ea000-4021-7000-8000-000000000000', NULL, '018ea000-3007-7000-8000-000000000000',
     '018ea000-1002-7000-8000-000000000000', 'PRIMARY'),   -- Hamstrings
    ('018ea000-4022-7000-8000-000000000000', NULL, '018ea000-3007-7000-8000-000000000000',
     '018ea000-1003-7000-8000-000000000000', 'SECONDARY'), -- Glutes
    ('018ea000-4023-7000-8000-000000000000', NULL, '018ea000-3007-7000-8000-000000000000',
     '018ea000-1016-7000-8000-000000000000', 'SECONDARY'), -- Lower Back
    -- Leg Press
    ('018ea000-4024-7000-8000-000000000000', NULL, '018ea000-3008-7000-8000-000000000000',
     '018ea000-1001-7000-8000-000000000000', 'PRIMARY'),   -- Quads
    ('018ea000-4025-7000-8000-000000000000', NULL, '018ea000-3008-7000-8000-000000000000',
     '018ea000-1003-7000-8000-000000000000', 'SECONDARY'), -- Glutes
    -- Incline Dumbbell Press
    ('018ea000-4026-7000-8000-000000000000', NULL, '018ea000-3009-7000-8000-000000000000',
     '018ea000-1005-7000-8000-000000000000', 'PRIMARY'),   -- Chest
    ('018ea000-4027-7000-8000-000000000000', NULL, '018ea000-3009-7000-8000-000000000000',
     '018ea000-1009-7000-8000-000000000000', 'SECONDARY'), -- Front Delt
    ('018ea000-4028-7000-8000-000000000000', NULL, '018ea000-3009-7000-8000-000000000000',
     '018ea000-1012-7000-8000-000000000000', 'SECONDARY'), -- Triceps
    -- Lat Pulldown
    ('018ea000-4029-7000-8000-000000000000', NULL, '018ea000-3010-7000-8000-000000000000',
     '018ea000-1007-7000-8000-000000000000', 'PRIMARY'),   -- Lats
    ('018ea000-4030-7000-8000-000000000000', NULL, '018ea000-3010-7000-8000-000000000000',
     '018ea000-1011-7000-8000-000000000000', 'SECONDARY'), -- Biceps
    -- Seated Cable Row
    ('018ea000-4031-7000-8000-000000000000', NULL, '018ea000-3011-7000-8000-000000000000',
     '018ea000-1007-7000-8000-000000000000', 'PRIMARY'),   -- Lats
    ('018ea000-4032-7000-8000-000000000000', NULL, '018ea000-3011-7000-8000-000000000000',
     '018ea000-1006-7000-8000-000000000000', 'SECONDARY'), -- Upper Back
    ('018ea000-4033-7000-8000-000000000000', NULL, '018ea000-3011-7000-8000-000000000000',
     '018ea000-1011-7000-8000-000000000000', 'SECONDARY'), -- Biceps
    -- Dumbbell Curl
    ('018ea000-4034-7000-8000-000000000000', NULL, '018ea000-3012-7000-8000-000000000000',
     '018ea000-1011-7000-8000-000000000000', 'PRIMARY'),   -- Biceps
    ('018ea000-4035-7000-8000-000000000000', NULL, '018ea000-3012-7000-8000-000000000000',
     '018ea000-1013-7000-8000-000000000000', 'SECONDARY'), -- Forearms
    -- Tricep Pushdown
    ('018ea000-4036-7000-8000-000000000000', NULL, '018ea000-3013-7000-8000-000000000000',
     '018ea000-1012-7000-8000-000000000000', 'PRIMARY'),   -- Triceps
    -- Leg Extension
    ('018ea000-4037-7000-8000-000000000000', NULL, '018ea000-3014-7000-8000-000000000000',
     '018ea000-1001-7000-8000-000000000000', 'PRIMARY'),   -- Quads
    -- Leg Curl
    ('018ea000-4038-7000-8000-000000000000', NULL, '018ea000-3015-7000-8000-000000000000',
     '018ea000-1002-7000-8000-000000000000', 'PRIMARY'),   -- Hamstrings
    -- Calf Raise
    ('018ea000-4039-7000-8000-000000000000', NULL, '018ea000-3016-7000-8000-000000000000',
     '018ea000-1004-7000-8000-000000000000', 'PRIMARY'),   -- Calves
    -- Plank
    ('018ea000-4040-7000-8000-000000000000', NULL, '018ea000-3017-7000-8000-000000000000',
     '018ea000-1014-7000-8000-000000000000', 'PRIMARY'),   -- Core
    ('018ea000-4041-7000-8000-000000000000', NULL, '018ea000-3017-7000-8000-000000000000',
     '018ea000-1016-7000-8000-000000000000', 'SECONDARY'), -- Lower Back
    -- Ab Crunch
    ('018ea000-4042-7000-8000-000000000000', NULL, '018ea000-3018-7000-8000-000000000000',
     '018ea000-1014-7000-8000-000000000000', 'PRIMARY'),   -- Core
    ('018ea000-4043-7000-8000-000000000000', NULL, '018ea000-3018-7000-8000-000000000000',
     '018ea000-1015-7000-8000-000000000000', 'SECONDARY'), -- Obliques
    -- Russian Twist
    ('018ea000-4044-7000-8000-000000000000', NULL, '018ea000-3019-7000-8000-000000000000',
     '018ea000-1015-7000-8000-000000000000', 'PRIMARY'),   -- Obliques
    ('018ea000-4045-7000-8000-000000000000', NULL, '018ea000-3019-7000-8000-000000000000',
     '018ea000-1014-7000-8000-000000000000', 'SECONDARY'), -- Core
    -- Lateral Raise
    ('018ea000-4046-7000-8000-000000000000', NULL, '018ea000-3020-7000-8000-000000000000',
     '018ea000-1010-7000-8000-000000000000', 'PRIMARY'),   -- Side Delt
    ('018ea000-4047-7000-8000-000000000000', NULL, '018ea000-3020-7000-8000-000000000000',
     '018ea000-1009-7000-8000-000000000000', 'SECONDARY'), -- Front Delt
    -- Face Pull
    ('018ea000-4048-7000-8000-000000000000', NULL, '018ea000-3021-7000-8000-000000000000',
     '018ea000-1008-7000-8000-000000000000', 'PRIMARY'),   -- Rear Delt
    ('018ea000-4049-7000-8000-000000000000', NULL, '018ea000-3021-7000-8000-000000000000',
     '018ea000-1006-7000-8000-000000000000', 'SECONDARY'), -- Upper Back
    -- Hip Thrust
    ('018ea000-4050-7000-8000-000000000000', NULL, '018ea000-3022-7000-8000-000000000000',
     '018ea000-1003-7000-8000-000000000000', 'PRIMARY'),   -- Glutes
    ('018ea000-4051-7000-8000-000000000000', NULL, '018ea000-3022-7000-8000-000000000000',
     '018ea000-1002-7000-8000-000000000000', 'SECONDARY'), -- Hamstrings
    -- Dumbbell Lunge
    ('018ea000-4052-7000-8000-000000000000', NULL, '018ea000-3023-7000-8000-000000000000',
     '018ea000-1001-7000-8000-000000000000', 'PRIMARY'),   -- Quads
    ('018ea000-4053-7000-8000-000000000000', NULL, '018ea000-3023-7000-8000-000000000000',
     '018ea000-1003-7000-8000-000000000000', 'SECONDARY'), -- Glutes
    ('018ea000-4054-7000-8000-000000000000', NULL, '018ea000-3023-7000-8000-000000000000',
     '018ea000-1002-7000-8000-000000000000', 'SECONDARY'), -- Hamstrings
    -- Close Grip Bench Press
    ('018ea000-4055-7000-8000-000000000000', NULL, '018ea000-3024-7000-8000-000000000000',
     '018ea000-1012-7000-8000-000000000000', 'PRIMARY'),   -- Triceps
    ('018ea000-4056-7000-8000-000000000000', NULL, '018ea000-3024-7000-8000-000000000000',
     '018ea000-1005-7000-8000-000000000000', 'SECONDARY'), -- Chest
    -- Hammer Curl
    ('018ea000-4057-7000-8000-000000000000', NULL, '018ea000-3025-7000-8000-000000000000',
     '018ea000-1011-7000-8000-000000000000', 'PRIMARY'),   -- Biceps
    ('018ea000-4058-7000-8000-000000000000', NULL, '018ea000-3025-7000-8000-000000000000',
     '018ea000-1013-7000-8000-000000000000', 'SECONDARY');
-- Forearms

-- ============================================================
-- 8. KAYDEDİLMİŞ EGZERSİZLER (kullanıcı başı 4 adet)
-- ============================================================
INSERT INTO saved_exercise (user_profile_id, exercise_id)
VALUES
    -- Ahmet (hypertrophy – pushing ekzersizler)
    ('018ea000-0001-7000-8000-000000000001', '018ea000-3002-7000-8000-000000000000'), -- Bench Press
    ('018ea000-0001-7000-8000-000000000001', '018ea000-3001-7000-8000-000000000000'), -- Squat
    ('018ea000-0001-7000-8000-000000000001', '018ea000-3009-7000-8000-000000000000'), -- Incline DB Press
    ('018ea000-0001-7000-8000-000000000001', '018ea000-3024-7000-8000-000000000000'), -- CGBP
    -- Zeynep (hipertrofi – karışık)
    ('018ea000-0002-7000-8000-000000000002', '018ea000-3022-7000-8000-000000000000'), -- Hip Thrust
    ('018ea000-0002-7000-8000-000000000002', '018ea000-3023-7000-8000-000000000000'), -- DB Lunge
    ('018ea000-0002-7000-8000-000000000002', '018ea000-3017-7000-8000-000000000000'), -- Plank
    ('018ea000-0002-7000-8000-000000000002', '018ea000-3018-7000-8000-000000000000'), -- Ab Crunch
    -- Mehmet (güç)
    ('018ea000-0003-7000-8000-000000000003', '018ea000-3003-7000-8000-000000000000'), -- Deadlift
    ('018ea000-0003-7000-8000-000000000003', '018ea000-3001-7000-8000-000000000000'), -- Squat
    ('018ea000-0003-7000-8000-000000000003', '018ea000-3005-7000-8000-000000000000'), -- OHP
    ('018ea000-0003-7000-8000-000000000003', '018ea000-3006-7000-8000-000000000000'), -- BB Row
    -- Elif (endurance)
    ('018ea000-0004-7000-8000-000000000004', '018ea000-3017-7000-8000-000000000000'), -- Plank
    ('018ea000-0004-7000-8000-000000000004', '018ea000-3019-7000-8000-000000000000'), -- Russian Twist
    ('018ea000-0004-7000-8000-000000000004', '018ea000-3018-7000-8000-000000000000'), -- Ab Crunch
    ('018ea000-0004-7000-8000-000000000004', '018ea000-3016-7000-8000-000000000000'), -- Calf Raise
    -- Can (genel fitness)
    ('018ea000-0005-7000-8000-000000000005', '018ea000-3004-7000-8000-000000000000'), -- Pull-up
    ('018ea000-0005-7000-8000-000000000005', '018ea000-3010-7000-8000-000000000000'), -- Lat Pulldown
    ('018ea000-0005-7000-8000-000000000005', '018ea000-3012-7000-8000-000000000000'), -- DB Curl
    ('018ea000-0005-7000-8000-000000000005', '018ea000-3013-7000-8000-000000000000');
-- Tricep Pushdown

-- ============================================================
-- 9. ANTRENMAN PROGRAMLARI
--    Kullanıcı başı 2 program; sadece biri aktif (partial unique index)
-- ============================================================
-- ID şeması: 018ea000-5001..5010-7000-8000-000000000000
INSERT INTO workout_program (id, user_profile_id, is_active, is_ai_generated, name, description)
VALUES
    -- Ahmet
    ('018ea000-5001-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001', TRUE, FALSE, 'PPL Hypertrophy',
     'Push / Pull / Legs split; haftada 6 gün. Bulking fazı için optimize edildi.'),
    ('018ea000-5002-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001', FALSE, TRUE, 'AI Upper-Lower',
     'Yapay zeka destekli upper-lower 4 günlük split (arşiv).'),
    -- Zeynep
    ('018ea000-5003-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002', TRUE, FALSE, 'Full Body 3x',
     'Haftada 3 gün tam vücut antrenmanı; sürdürülebilir rutin.'),
    ('018ea000-5004-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002', FALSE, FALSE, 'Glute & Core Focus',
     'Kalça ve core odaklı 4 günlük split (arşiv).'),
    -- Mehmet
    ('018ea000-5005-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003', TRUE, FALSE, '5x5 Strength',
     'Klasik 5x5 güç programı; compound hareketler ağırlıklı.'),
    ('018ea000-5006-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003', FALSE, FALSE, 'Powerbuilding',
     'Güç + hacim kombine program (arşiv).'),
    -- Elif
    ('018ea000-5007-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004', TRUE, TRUE, 'AI Endurance Circuit',
     'Yapay zeka devre antrenmanı; kardio + ağırlık kombinasyonu.'),
    ('018ea000-5008-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004', FALSE, FALSE, 'Beginner Fitness',
     'Başlangıç seviye 3 günlük tam vücut (arşiv).'),
    -- Can
    ('018ea000-5009-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005', TRUE, FALSE, 'Bro Split 5 Day',
     'Klasik bro-split; göğüs / sırt / omuz / biceps-triceps / bacak.'),
    ('018ea000-5010-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005', FALSE, FALSE, 'Home Workout',
     'Evde ekipsiz antrenman programı (arşiv).');
-- ============================================================
-- 10. ANTRENMAN GÜNLERİ
-- ============================================================
-- Ahmet – PPL (6 gün)
INSERT INTO workout_day (id, user_profile_id, workout_program_id, is_off, order_number, name)
VALUES ('018ea000-6001-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5001-7000-8000-000000000000', FALSE, 1.00, 'Push A (Göğüs Ağırlıklı)'),
       ('018ea000-6002-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5001-7000-8000-000000000000', FALSE, 2.00, 'Pull A (Sırt Ağırlıklı)'),
       ('018ea000-6003-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5001-7000-8000-000000000000', FALSE, 3.00, 'Legs A (Quad Ağırlıklı)'),
       ('018ea000-6004-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5001-7000-8000-000000000000', FALSE, 4.00, 'Push B (Omuz Ağırlıklı)'),
       ('018ea000-6005-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5001-7000-8000-000000000000', FALSE, 5.00, 'Pull B (Biceps Ağırlıklı)'),
       ('018ea000-6006-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5001-7000-8000-000000000000', FALSE, 6.00, 'Legs B (Hamstring / Glute)');

-- Ahmet – AI Upper-Lower (arşiv, 4 gün)
INSERT INTO workout_day (id, user_profile_id, workout_program_id, is_off, order_number, name)
VALUES ('018ea000-6007-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5002-7000-8000-000000000000', FALSE, 1.00, 'Upper A'),
       ('018ea000-6008-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5002-7000-8000-000000000000', FALSE, 2.00, 'Lower A'),
       ('018ea000-6009-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5002-7000-8000-000000000000', TRUE, 3.00, 'Dinlenme'),
       ('018ea000-6010-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-5002-7000-8000-000000000000', FALSE, 4.00, 'Upper B');

-- Zeynep – Full Body 3x (3 gün + 4. off)
INSERT INTO workout_day (id, user_profile_id, workout_program_id, is_off, order_number, name)
VALUES ('018ea000-6011-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-5003-7000-8000-000000000000', FALSE, 1.00, 'Tam Vücut A'),
       ('018ea000-6012-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-5003-7000-8000-000000000000', TRUE, 2.00, 'Aktif Dinlenme'),
       ('018ea000-6013-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-5003-7000-8000-000000000000', FALSE, 3.00, 'Tam Vücut B'),
       ('018ea000-6014-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-5003-7000-8000-000000000000', TRUE, 4.00, 'Dinlenme'),
       ('018ea000-6015-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-5003-7000-8000-000000000000', FALSE, 5.00, 'Tam Vücut C');

-- Mehmet – 5x5 Strength (3 gün)
INSERT INTO workout_day (id, user_profile_id, workout_program_id, is_off, order_number, name)
VALUES ('018ea000-6016-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-5005-7000-8000-000000000000', FALSE, 1.00, 'Gün A – Squat / BP / Row'),
       ('018ea000-6017-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-5005-7000-8000-000000000000', FALSE, 2.00, 'Gün B – Squat / OHP / DL'),
       ('018ea000-6018-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-5005-7000-8000-000000000000', TRUE, 3.00, 'Dinlenme');

-- Elif – AI Endurance Circuit (4 gün)
INSERT INTO workout_day (id, user_profile_id, workout_program_id, is_off, order_number, name)
VALUES ('018ea000-6019-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-5007-7000-8000-000000000000', FALSE, 1.00, 'Kardio + Core'),
       ('018ea000-6020-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-5007-7000-8000-000000000000', FALSE, 2.00, 'Alt Vücut Devre'),
       ('018ea000-6021-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-5007-7000-8000-000000000000', TRUE, 3.00, 'Dinlenme'),
       ('018ea000-6022-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-5007-7000-8000-000000000000', FALSE, 4.00, 'Üst Vücut Devre');

-- Can – Bro Split 5 Day
INSERT INTO workout_day (id, user_profile_id, workout_program_id, is_off, order_number, name)
VALUES ('018ea000-6023-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-5009-7000-8000-000000000000', FALSE, 1.00, 'Göğüs'),
       ('018ea000-6024-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-5009-7000-8000-000000000000', FALSE, 2.00, 'Sırt'),
       ('018ea000-6025-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-5009-7000-8000-000000000000', FALSE, 3.00, 'Omuz'),
       ('018ea000-6026-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-5009-7000-8000-000000000000', FALSE, 4.00, 'Kol (Biceps + Triceps)'),
       ('018ea000-6027-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-5009-7000-8000-000000000000', FALSE, 5.00, 'Bacak');

-- ============================================================
-- 11. HEDEF SETLER (target_set)
--     Kural: rep_count XOR duration; biri NULL, diğeri dolu.
-- ============================================================

-- ── Ahmet PPL – Push A (Göğüs Ağırlıklı) ──────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Bench Press 4x8
    ('018ea000-7001-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 80.00, 8, NULL, 'METRIC', 120,
     1.00),
    ('018ea000-7002-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 80.00, 8, NULL, 'METRIC', 120,
     1.25),
    ('018ea000-7003-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 80.00, 8, NULL, 'METRIC', 120,
     1.50),
    ('018ea000-7004-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 80.00, 8, NULL, 'METRIC', 120,
     1.75),
    -- Incline DB Press 3x10
    ('018ea000-7005-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3009-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 28.00, 10, NULL, 'METRIC', 90,
     2.00),
    ('018ea000-7006-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3009-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 28.00, 10, NULL, 'METRIC', 90,
     2.25),
    ('018ea000-7007-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3009-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 28.00, 10, NULL, 'METRIC', 90,
     2.50),
    -- OHP 3x8
    ('018ea000-7008-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3005-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 50.00, 8, NULL, 'METRIC', 120,
     3.00),
    ('018ea000-7009-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3005-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 50.00, 8, NULL, 'METRIC', 120,
     3.25),
    ('018ea000-7010-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3005-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 50.00, 8, NULL, 'METRIC', 120,
     3.50),
    -- Tricep Pushdown 3x12
    ('018ea000-7011-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3013-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 30.00, 12, NULL, 'METRIC', 60,
     4.00),
    ('018ea000-7012-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3013-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 30.00, 12, NULL, 'METRIC', 60,
     4.25),
    ('018ea000-7013-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3013-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000', 30.00, 12, NULL, 'METRIC', 60,
     4.50);

-- ── Ahmet PPL – Pull A (Sırt Ağırlıklı) ───────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Barbell Row 4x8
    ('018ea000-7014-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 70.00, 8, NULL, 'METRIC', 120,
     1.00),
    ('018ea000-7015-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 70.00, 8, NULL, 'METRIC', 120,
     1.25),
    ('018ea000-7016-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 70.00, 8, NULL, 'METRIC', 120,
     1.50),
    ('018ea000-7017-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 70.00, 8, NULL, 'METRIC', 120,
     1.75),
    -- Pull-up 3x Max
    ('018ea000-7018-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3004-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 0.00, 10, NULL, 'METRIC', 90,
     2.00),
    ('018ea000-7019-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3004-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 0.00, 10, NULL, 'METRIC', 90,
     2.25),
    ('018ea000-7020-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3004-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 0.00, 10, NULL, 'METRIC', 90,
     2.50),
    -- Seated Cable Row 3x12
    ('018ea000-7021-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3011-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 55.00, 12, NULL, 'METRIC', 75,
     3.00),
    ('018ea000-7022-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3011-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 55.00, 12, NULL, 'METRIC', 75,
     3.25),
    ('018ea000-7023-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3011-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 55.00, 12, NULL, 'METRIC', 75,
     3.50),
    -- DB Curl 3x12
    ('018ea000-7024-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3012-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 16.00, 12, NULL, 'METRIC', 60,
     4.00),
    ('018ea000-7025-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3012-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 16.00, 12, NULL, 'METRIC', 60,
     4.25),
    ('018ea000-7026-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3012-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000', 16.00, 12, NULL, 'METRIC', 60,
     4.50);

-- ── Ahmet PPL – Legs A (Quad Ağırlıklı) ───────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Squat 4x6
    ('018ea000-7027-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 100.00, 6, NULL, 'METRIC', 180,
     1.00),
    ('018ea000-7028-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 100.00, 6, NULL, 'METRIC', 180,
     1.25),
    ('018ea000-7029-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 100.00, 6, NULL, 'METRIC', 180,
     1.50),
    ('018ea000-7030-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 100.00, 6, NULL, 'METRIC', 180,
     1.75),
    -- Leg Press 3x12
    ('018ea000-7031-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3008-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 140.00, 12, NULL, 'METRIC', 90,
     2.00),
    ('018ea000-7032-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3008-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 140.00, 12, NULL, 'METRIC', 90,
     2.25),
    ('018ea000-7033-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3008-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 140.00, 12, NULL, 'METRIC', 90,
     2.50),
    -- Leg Extension 3x15
    ('018ea000-7034-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3014-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 50.00, 15, NULL, 'METRIC', 60,
     3.00),
    ('018ea000-7035-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3014-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 50.00, 15, NULL, 'METRIC', 60,
     3.25),
    ('018ea000-7036-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3014-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 50.00, 15, NULL, 'METRIC', 60,
     3.50),
    -- Plank 3x60sn (duration bazlı)
    ('018ea000-7037-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 0.00, NULL, 60, 'METRIC', 45,
     4.00),
    ('018ea000-7038-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 0.00, NULL, 60, 'METRIC', 45,
     4.25),
    ('018ea000-7039-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000', 0.00, NULL, 60, 'METRIC', 45,
     4.50);

-- ── Zeynep – Tam Vücut A ────────────────────────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Hip Thrust 4x12
    ('018ea000-7040-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3022-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 60.00, 12, NULL, 'METRIC', 90,
     1.00),
    ('018ea000-7041-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3022-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 60.00, 12, NULL, 'METRIC', 90,
     1.25),
    ('018ea000-7042-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3022-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 60.00, 12, NULL, 'METRIC', 90,
     1.50),
    ('018ea000-7043-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3022-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 60.00, 12, NULL, 'METRIC', 90,
     1.75),
    -- Lat Pulldown 3x12
    ('018ea000-7044-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3010-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 42.00, 12, NULL, 'METRIC', 75,
     2.00),
    ('018ea000-7045-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3010-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 42.00, 12, NULL, 'METRIC', 75,
     2.25),
    ('018ea000-7046-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3010-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 42.00, 12, NULL, 'METRIC', 75,
     2.50),
    -- Plank 3x45sn
    ('018ea000-7047-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 0.00, NULL, 45, 'METRIC', 30,
     3.00),
    ('018ea000-7048-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 0.00, NULL, 45, 'METRIC', 30,
     3.25),
    ('018ea000-7049-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000', 0.00, NULL, 45, 'METRIC', 30,
     3.50);

-- ── Mehmet – 5x5 Gün A ──────────────────────────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Squat 5x5
    ('018ea000-7050-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 130.00, 5, NULL, 'IMPERIAL', 240,
     1.00),
    ('018ea000-7051-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 130.00, 5, NULL, 'IMPERIAL', 240,
     1.25),
    ('018ea000-7052-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 130.00, 5, NULL, 'IMPERIAL', 240,
     1.50),
    ('018ea000-7053-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 130.00, 5, NULL, 'IMPERIAL', 240,
     1.75),
    ('018ea000-7054-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 130.00, 5, NULL, 'IMPERIAL', 240,
     2.00),
    -- Bench Press 5x5
    ('018ea000-7055-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 110.00, 5, NULL, 'IMPERIAL', 180,
     3.00),
    ('018ea000-7056-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 110.00, 5, NULL, 'IMPERIAL', 180,
     3.25),
    ('018ea000-7057-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 110.00, 5, NULL, 'IMPERIAL', 180,
     3.50),
    ('018ea000-7058-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 110.00, 5, NULL, 'IMPERIAL', 180,
     3.75),
    ('018ea000-7059-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 110.00, 5, NULL, 'IMPERIAL', 180,
     4.00),
    -- BB Row 5x5
    ('018ea000-7060-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 90.00, 5, NULL, 'IMPERIAL', 180,
     5.00),
    ('018ea000-7061-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 90.00, 5, NULL, 'IMPERIAL', 180,
     5.25),
    ('018ea000-7062-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 90.00, 5, NULL, 'IMPERIAL', 180,
     5.50),
    ('018ea000-7063-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 90.00, 5, NULL, 'IMPERIAL', 180,
     5.75),
    ('018ea000-7064-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-3006-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000', 90.00, 5, NULL, 'IMPERIAL', 180,
     6.00);

-- ── Elif – Kardio + Core ─────────────────────────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Plank 4x45sn
    ('018ea000-7065-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 0.00, NULL, 45, 'METRIC', 30,
     1.00),
    ('018ea000-7066-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 0.00, NULL, 45, 'METRIC', 30,
     1.25),
    ('018ea000-7067-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 0.00, NULL, 45, 'METRIC', 30,
     1.50),
    ('018ea000-7068-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3017-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 0.00, NULL, 45, 'METRIC', 30,
     1.75),
    -- Russian Twist 3x20
    ('018ea000-7069-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3019-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 4.00, 20, NULL, 'METRIC', 30,
     2.00),
    ('018ea000-7070-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3019-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 4.00, 20, NULL, 'METRIC', 30,
     2.25),
    ('018ea000-7071-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3019-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 4.00, 20, NULL, 'METRIC', 30,
     2.50),
    -- Ab Crunch 3x20
    ('018ea000-7072-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3018-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 0.00, 20, NULL, 'METRIC', 30,
     3.00),
    ('018ea000-7073-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3018-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 0.00, 20, NULL, 'METRIC', 30,
     3.25),
    ('018ea000-7074-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-3018-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000', 0.00, 20, NULL, 'METRIC', 30,
     3.50);

-- ── Can – Göğüs ─────────────────────────────────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Bench Press 4x10
    ('018ea000-7075-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 75.00, 10, NULL, 'METRIC', 120,
     1.00),
    ('018ea000-7076-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 75.00, 10, NULL, 'METRIC', 120,
     1.25),
    ('018ea000-7077-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 75.00, 10, NULL, 'METRIC', 120,
     1.50),
    ('018ea000-7078-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3002-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 75.00, 10, NULL, 'METRIC', 120,
     1.75),
    -- Incline DB Press 3x12
    ('018ea000-7079-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3009-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 26.00, 12, NULL, 'METRIC', 90,
     2.00),
    ('018ea000-7080-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3009-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 26.00, 12, NULL, 'METRIC', 90,
     2.25),
    ('018ea000-7081-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3009-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 26.00, 12, NULL, 'METRIC', 90,
     2.50),
    -- CGBP 3x10
    ('018ea000-7082-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3024-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 60.00, 10, NULL, 'METRIC', 90,
     3.00),
    ('018ea000-7083-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3024-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 60.00, 10, NULL, 'METRIC', 90,
     3.25),
    ('018ea000-7084-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3024-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000', 60.00, 10, NULL, 'METRIC', 90,
     3.50);

-- ── Can – Sırt ──────────────────────────────────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Pull-up 4x8
    ('018ea000-7085-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3004-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 0.00, 8, NULL, 'METRIC', 120,
     1.00),
    ('018ea000-7086-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3004-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 0.00, 8, NULL, 'METRIC', 120,
     1.25),
    ('018ea000-7087-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3004-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 0.00, 8, NULL, 'METRIC', 120,
     1.50),
    ('018ea000-7088-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3004-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 0.00, 8, NULL, 'METRIC', 120,
     1.75),
    -- Lat Pulldown 3x12
    ('018ea000-7089-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3010-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 55.00, 12, NULL, 'METRIC', 75,
     2.00),
    ('018ea000-7090-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3010-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 55.00, 12, NULL, 'METRIC', 75,
     2.25),
    ('018ea000-7091-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3010-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 55.00, 12, NULL, 'METRIC', 75,
     2.50),
    -- Face Pull 3x15
    ('018ea000-7092-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3021-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 20.00, 15, NULL, 'METRIC', 45,
     3.00),
    ('018ea000-7093-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3021-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 20.00, 15, NULL, 'METRIC', 45,
     3.25),
    ('018ea000-7094-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3021-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000', 20.00, 15, NULL, 'METRIC', 45,
     3.50);

-- ── Can – Bacak ─────────────────────────────────────────────
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- Squat 4x8
    ('018ea000-7095-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 85.00, 8, NULL, 'METRIC', 150,
     1.00),
    ('018ea000-7096-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 85.00, 8, NULL, 'METRIC', 150,
     1.25),
    ('018ea000-7097-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 85.00, 8, NULL, 'METRIC', 150,
     1.50),
    ('018ea000-7098-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3001-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 85.00, 8, NULL, 'METRIC', 150,
     1.75),
    -- RDL 3x10
    ('018ea000-7099-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3007-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 70.00, 10, NULL, 'METRIC', 120,
     2.00),
    ('018ea000-7100-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3007-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 70.00, 10, NULL, 'METRIC', 120,
     2.25),
    ('018ea000-7101-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3007-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 70.00, 10, NULL, 'METRIC', 120,
     2.50),
    -- Leg Curl 3x12
    ('018ea000-7102-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3015-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 45.00, 12, NULL, 'METRIC', 60,
     3.00),
    ('018ea000-7103-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3015-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 45.00, 12, NULL, 'METRIC', 60,
     3.25),
    ('018ea000-7104-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3015-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 45.00, 12, NULL, 'METRIC', 60,
     3.50),
    -- Calf Raise 4x15
    ('018ea000-7105-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3016-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 60.00, 15, NULL, 'METRIC', 45,
     4.00),
    ('018ea000-7106-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3016-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 60.00, 15, NULL, 'METRIC', 45,
     4.25),
    ('018ea000-7107-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3016-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 60.00, 15, NULL, 'METRIC', 45,
     4.50),
    ('018ea000-7108-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-3016-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000', 60.00, 15, NULL, 'METRIC', 45,
     4.75);

-- ============================================================
-- 12. ANTRENMAN PERİYOTLARI (DÜZELTİLMİŞ)
-- ============================================================
INSERT INTO workout_period (id, user_profile_id, workout_program_id, is_active, start_date, end_date,
                            workout_program_name_snapshot)
VALUES
    -- Ahmet
    ('018ea000-8001-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-5002-7000-8000-000000000000', FALSE, now() - INTERVAL '180 days', now() - INTERVAL '90 days',
     'AI Upper-Lower'),
    ('018ea000-8002-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
     '018ea000-5001-7000-8000-000000000000', TRUE, now() - INTERVAL '89 days', NULL, 'PPL Hypertrophy'),
    -- Zeynep
    ('018ea000-8003-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-5004-7000-8000-000000000000', FALSE, now() - INTERVAL '150 days', now() - INTERVAL '60 days',
     'Glute & Core Focus'),
    ('018ea000-8004-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
     '018ea000-5003-7000-8000-000000000000', TRUE, now() - INTERVAL '59 days', NULL, 'Full Body 3x'),
    -- Mehmet
    ('018ea000-8005-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-5006-7000-8000-000000000000', FALSE, now() - INTERVAL '200 days', now() - INTERVAL '100 days',
     'Powerbuilding'),
    ('018ea000-8006-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
     '018ea000-5005-7000-8000-000000000000', TRUE, now() - INTERVAL '99 days', NULL, '5x5 Strength'),
    -- Elif
    ('018ea000-8007-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-5008-7000-8000-000000000000', FALSE, now() - INTERVAL '120 days', now() - INTERVAL '30 days',
     'Beginner Fitness'),
    ('018ea000-8008-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
     '018ea000-5007-7000-8000-000000000000', TRUE, now() - INTERVAL '29 days', NULL, 'AI Endurance Circuit'),
-- Can (DÜZELTİLDİ: Program ID sonu 09 olmalı)
    ('018ea000-8009-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-5010-7000-8000-000000000000', FALSE, now() - INTERVAL '160 days', now() - INTERVAL '80 days',
     'Home Workout'),
    ('018ea000-800a-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
     '018ea000-5009-7000-8000-000000000000', TRUE, now() - INTERVAL '79 days', NULL, 'Bro Split 5 Day');
-- ============================================================
-- 13. ANTRENMAN SEANSLARI
--     Karışık statüler: FINISHED / MISSED / PLANNED
-- ============================================================
-- Ahmet – 12 seans (FINISHED ağırlıklı + 2 PLANNED)
INSERT INTO workout_session (id, user_profile_id, workout_period_id, workout_day_id, start_date, start_time, end_time,
                             status, type, workout_day_name_snapshot, exercise_count_snapshot)
VALUES ('018ea000-9001-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000',
        CAST(now() - INTERVAL '85 days' AS DATE), now() - INTERVAL '85 days',
        now() - INTERVAL '85 days' + INTERVAL '75 minutes', 'FINISHED', 'WORKOUT', 'Push A (Göğüs Ağırlıklı)', 4),
       ('018ea000-9002-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000',
        CAST(now() - INTERVAL '84 days' AS DATE), now() - INTERVAL '84 days',
        now() - INTERVAL '84 days' + INTERVAL '70 minutes', 'FINISHED', 'WORKOUT', 'Pull A (Sırt Ağırlıklı)', 4),
       ('018ea000-9003-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000',
        CAST(now() - INTERVAL '83 days' AS DATE), now() - INTERVAL '83 days',
        now() - INTERVAL '83 days' + INTERVAL '80 minutes', 'FINISHED', 'WORKOUT', 'Legs A (Quad Ağırlıklı)', 4),
       ('018ea000-9004-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6004-7000-8000-000000000000',
        CAST(now() - INTERVAL '78 days' AS DATE), now() - INTERVAL '78 days',
        now() - INTERVAL '78 days' + INTERVAL '65 minutes', 'FINISHED', 'WORKOUT', 'Push B (Omuz Ağırlıklı)', 4),
       ('018ea000-9005-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6005-7000-8000-000000000000',
        CAST(now() - INTERVAL '77 days' AS DATE), now() - INTERVAL '77 days',
        now() - INTERVAL '77 days' + INTERVAL '68 minutes', 'FINISHED', 'WORKOUT', 'Pull B (Biceps Ağırlıklı)', 4),
       ('018ea000-9006-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6006-7000-8000-000000000000',
        CAST(now() - INTERVAL '76 days' AS DATE), now() - INTERVAL '76 days',
        now() - INTERVAL '76 days' + INTERVAL '78 minutes', 'FINISHED', 'WORKOUT', 'Legs B (Hamstring / Glute)', 4),
       ('018ea000-9007-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6001-7000-8000-000000000000',
        CAST(now() - INTERVAL '35 days' AS DATE), now() - INTERVAL '35 days', NULL, 'MISSED', 'WORKOUT',
        'Push A (Göğüs Ağırlıklı)', 4),
       ('018ea000-9008-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6002-7000-8000-000000000000',
        CAST(now() - INTERVAL '14 days' AS DATE), now() - INTERVAL '14 days',
        now() - INTERVAL '14 days' + INTERVAL '72 minutes', 'FINISHED', 'WORKOUT', 'Pull A (Sırt Ağırlıklı)', 4),
       ('018ea000-9009-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6003-7000-8000-000000000000',
        CAST(now() - INTERVAL '13 days' AS DATE), now() - INTERVAL '13 days',
        now() - INTERVAL '13 days' + INTERVAL '85 minutes', 'FINISHED', 'WORKOUT', 'Legs A (Quad Ağırlıklı)', 4),
       ('018ea000-900a-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', NULL, CAST(now() - INTERVAL '7 days' AS DATE),
        now() - INTERVAL '7 days', now() - INTERVAL '7 days' + INTERVAL '40 minutes', 'FINISHED', 'QUICK_WORKOUT',
        'Quick Push', 3),
       ('018ea000-900b-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6004-7000-8000-000000000000',
        CAST(now() + INTERVAL '1 day' AS DATE), now() + INTERVAL '1 day', NULL, 'PLANNED', 'WORKOUT',
        'Push B (Omuz Ağırlıklı)', 4),
       ('018ea000-900c-7000-8000-000000000000', '018ea000-0001-7000-8000-000000000001',
        '018ea000-8002-7000-8000-000000000000', '018ea000-6005-7000-8000-000000000000',
        CAST(now() + INTERVAL '2 days' AS DATE), now() + INTERVAL '2 days', NULL, 'PLANNED', 'WORKOUT',
        'Pull B (Biceps Ağırlıklı)', 4);

-- Zeynep – 10 seans
INSERT INTO workout_session (id, user_profile_id, workout_period_id, workout_day_id, start_date, start_time, end_time,
                             status, type, workout_day_name_snapshot, exercise_count_snapshot)
VALUES ('018ea000-9101-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-8004-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000',
        CAST(now() - INTERVAL '55 days' AS DATE), now() - INTERVAL '55 days',
        now() - INTERVAL '55 days' + INTERVAL '55 minutes', 'FINISHED', 'WORKOUT', 'Tam Vücut A', 3),
       ('018ea000-9102-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-8004-7000-8000-000000000000', '018ea000-6013-7000-8000-000000000000',
        CAST(now() - INTERVAL '53 days' AS DATE), now() - INTERVAL '53 days',
        now() - INTERVAL '53 days' + INTERVAL '58 minutes', 'FINISHED', 'WORKOUT', 'Tam Vücut B', 3),
       ('018ea000-9103-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-8004-7000-8000-000000000000', '018ea000-6015-7000-8000-000000000000',
        CAST(now() - INTERVAL '51 days' AS DATE), now() - INTERVAL '51 days',
        now() - INTERVAL '51 days' + INTERVAL '52 minutes', 'FINISHED', 'WORKOUT', 'Tam Vücut C', 3),
       ('018ea000-9104-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-8004-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000',
        CAST(now() - INTERVAL '20 days' AS DATE), now() - INTERVAL '20 days',
        now() - INTERVAL '20 days' + INTERVAL '60 minutes', 'FINISHED', 'WORKOUT', 'Tam Vücut A', 3),
       ('018ea000-9105-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-8004-7000-8000-000000000000', '018ea000-6013-7000-8000-000000000000',
        CAST(now() - INTERVAL '18 days' AS DATE), now() - INTERVAL '18 days', NULL, 'MISSED', 'WORKOUT', 'Tam Vücut B',
        3),
       ('018ea000-9106-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-8004-7000-8000-000000000000', '018ea000-6015-7000-8000-000000000000',
        CAST(now() - INTERVAL '5 days' AS DATE), now() - INTERVAL '5 days',
        now() - INTERVAL '5 days' + INTERVAL '56 minutes', 'FINISHED', 'WORKOUT', 'Tam Vücut C', 3),
       ('018ea000-9107-7000-8000-000000000000', '018ea000-0002-7000-8000-000000000002',
        '018ea000-8004-7000-8000-000000000000', '018ea000-6011-7000-8000-000000000000',
        CAST(now() + INTERVAL '2 days' AS DATE), now() + INTERVAL '2 days', NULL, 'PLANNED', 'WORKOUT', 'Tam Vücut A',
        3);

-- Mehmet – 10 seans
INSERT INTO workout_session (id, user_profile_id, workout_period_id, workout_day_id, start_date, start_time, end_time,
                             status, type, workout_day_name_snapshot, exercise_count_snapshot)
VALUES ('018ea000-9201-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-8006-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000',
        CAST(now() - INTERVAL '95 days' AS DATE), now() - INTERVAL '95 days',
        now() - INTERVAL '95 days' + INTERVAL '95 minutes', 'FINISHED', 'WORKOUT', 'Gün A – Squat / BP / Row', 3),
       ('018ea000-9202-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-8006-7000-8000-000000000000', '018ea000-6017-7000-8000-000000000000',
        CAST(now() - INTERVAL '93 days' AS DATE), now() - INTERVAL '93 days',
        now() - INTERVAL '93 days' + INTERVAL '90 minutes', 'FINISHED', 'WORKOUT', 'Gün B – Squat / OHP / DL', 3),
       ('018ea000-9203-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-8006-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000',
        CAST(now() - INTERVAL '40 days' AS DATE), now() - INTERVAL '40 days',
        now() - INTERVAL '40 days' + INTERVAL '100 minutes', 'FINISHED', 'WORKOUT', 'Gün A – Squat / BP / Row', 3),
       ('018ea000-9204-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-8006-7000-8000-000000000000', '018ea000-6017-7000-8000-000000000000',
        CAST(now() - INTERVAL '38 days' AS DATE), now() - INTERVAL '38 days',
        now() - INTERVAL '38 days' + INTERVAL '92 minutes', 'FINISHED', 'WORKOUT', 'Gün B – Squat / OHP / DL', 3),
       ('018ea000-9205-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-8006-7000-8000-000000000000', '018ea000-6016-7000-8000-000000000000',
        CAST(now() - INTERVAL '10 days' AS DATE), now() - INTERVAL '10 days',
        now() - INTERVAL '10 days' + INTERVAL '98 minutes', 'FINISHED', 'WORKOUT', 'Gün A – Squat / BP / Row', 3),
       ('018ea000-9206-7000-8000-000000000000', '018ea000-0003-7000-8000-000000000003',
        '018ea000-8006-7000-8000-000000000000', '018ea000-6017-7000-8000-000000000000',
        CAST(now() + INTERVAL '2 days' AS DATE), now() + INTERVAL '2 days', NULL, 'PLANNED', 'WORKOUT',
        'Gün B – Squat / OHP / DL', 3);

-- Elif – 8 seans
INSERT INTO workout_session (id, user_profile_id, workout_period_id, workout_day_id, start_date, start_time, end_time,
                             status, type, workout_day_name_snapshot, exercise_count_snapshot)
VALUES ('018ea000-9301-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-8008-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000',
        CAST(now() - INTERVAL '25 days' AS DATE), now() - INTERVAL '25 days',
        now() - INTERVAL '25 days' + INTERVAL '45 minutes', 'FINISHED', 'WORKOUT', 'Kardio + Core', 3),
       ('018ea000-9302-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-8008-7000-8000-000000000000', '018ea000-6020-7000-8000-000000000000',
        CAST(now() - INTERVAL '23 days' AS DATE), now() - INTERVAL '23 days',
        now() - INTERVAL '23 days' + INTERVAL '50 minutes', 'FINISHED', 'WORKOUT', 'Alt Vücut Devre', 3),
       ('018ea000-9303-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-8008-7000-8000-000000000000', '018ea000-6022-7000-8000-000000000000',
        CAST(now() - INTERVAL '21 days' AS DATE), now() - INTERVAL '21 days', NULL, 'MISSED', 'WORKOUT',
        'Üst Vücut Devre', 3),
       ('018ea000-9304-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-8008-7000-8000-000000000000', '018ea000-6019-7000-8000-000000000000',
        CAST(now() - INTERVAL '8 days' AS DATE), now() - INTERVAL '8 days',
        now() - INTERVAL '8 days' + INTERVAL '48 minutes', 'FINISHED', 'WORKOUT', 'Kardio + Core', 3),
       ('018ea000-9305-7000-8000-000000000000', '018ea000-0004-7000-8000-000000000004',
        '018ea000-8008-7000-8000-000000000000', '018ea000-6020-7000-8000-000000000000',
        CAST(now() + INTERVAL '1 day' AS DATE), now() + INTERVAL '1 day', NULL, 'PLANNED', 'WORKOUT', 'Alt Vücut Devre',
        3);

-- Can – 10 seans
INSERT INTO workout_session (id, user_profile_id, workout_period_id, workout_day_id, start_date, start_time, end_time,
                             status, type, workout_day_name_snapshot, exercise_count_snapshot)
VALUES ('018ea000-9401-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000',
        CAST(now() - INTERVAL '70 days' AS DATE), now() - INTERVAL '70 days',
        now() - INTERVAL '70 days' + INTERVAL '60 minutes', 'FINISHED', 'WORKOUT', 'Göğüs', 3),
       ('018ea000-9402-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000',
        CAST(now() - INTERVAL '69 days' AS DATE), now() - INTERVAL '69 days',
        now() - INTERVAL '69 days' + INTERVAL '65 minutes', 'FINISHED', 'WORKOUT', 'Sırt', 3),
       ('018ea000-9403-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6025-7000-8000-000000000000',
        CAST(now() - INTERVAL '68 days' AS DATE), now() - INTERVAL '68 days',
        now() - INTERVAL '68 days' + INTERVAL '55 minutes', 'FINISHED', 'WORKOUT', 'Omuz', 3),
       ('018ea000-9404-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6026-7000-8000-000000000000',
        CAST(now() - INTERVAL '67 days' AS DATE), now() - INTERVAL '67 days',
        now() - INTERVAL '67 days' + INTERVAL '50 minutes', 'FINISHED', 'WORKOUT', 'Kol (Biceps + Triceps)', 2),
       ('018ea000-9405-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000',
        CAST(now() - INTERVAL '66 days' AS DATE), now() - INTERVAL '66 days',
        now() - INTERVAL '66 days' + INTERVAL '75 minutes', 'FINISHED', 'WORKOUT', 'Bacak', 4),
       ('018ea000-9406-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000',
        CAST(now() - INTERVAL '15 days' AS DATE), now() - INTERVAL '15 days',
        now() - INTERVAL '15 days' + INTERVAL '62 minutes', 'FINISHED', 'WORKOUT', 'Göğüs', 3),
       ('018ea000-9407-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6024-7000-8000-000000000000',
        CAST(now() - INTERVAL '14 days' AS DATE), now() - INTERVAL '14 days', NULL, 'MISSED', 'WORKOUT', 'Sırt', 3),
       ('018ea000-9408-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6027-7000-8000-000000000000',
        CAST(now() - INTERVAL '3 days' AS DATE), now() - INTERVAL '3 days',
        now() - INTERVAL '3 days' + INTERVAL '80 minutes', 'FINISHED', 'WORKOUT', 'Bacak', 4),
       ('018ea000-9409-7000-8000-000000000000', '018ea000-0005-7000-8000-000000000005',
        '018ea000-800a-7000-8000-000000000000', '018ea000-6023-7000-8000-000000000000',
        CAST(now() + INTERVAL '1 day' AS DATE), now() + INTERVAL '1 day', NULL, 'PLANNED', 'WORKOUT', 'Göğüs', 3);

-- ============================================================
-- SİSTEM ANTRENMAN PROGRAMLARI (user_profile_id IS NULL)
-- ============================================================
INSERT INTO workout_program (id, user_profile_id, is_active, is_ai_generated, name, description)
VALUES ('018ea000-5000-7000-8000-000000000000', NULL, TRUE, FALSE, 'System Full Body Basics',
        'Sistem tarafından sağlanan, tüm vücudu kapsayan temel başlangıç programı.');

-- ============================================================
-- SİSTEM ANTRENMAN GÜNLERİ (System Full Body Basics)
-- ============================================================
INSERT INTO workout_day (id, user_profile_id, workout_program_id, is_off, order_number, name)
VALUES ('018ea000-6000-7000-8000-000000000001', NULL, '018ea000-5000-7000-8000-000000000000', FALSE, 1.00,
        'Tam Vücut A'),
       ('018ea000-6000-7000-8000-000000000002', NULL, '018ea000-5000-7000-8000-000000000000', TRUE, 2.00, 'Dinlenme'),
       ('018ea000-6000-7000-8000-000000000003', NULL, '018ea000-5000-7000-8000-000000000000', FALSE, 3.00,
        'Tam Vücut B');

-- ============================================================
-- SİSTEM HEDEF SETLERİ (System Full Body Basics)
-- ============================================================
INSERT INTO target_set (id, user_profile_id, exercise_id, workout_day_id, weight_kg, rep_count, duration, unit,
                        rest_duration, order_number)
VALUES
    -- ── Tam Vücut A: Squat 3x8 ─────────────────────────────────
    ('018ea000-7000-7000-8000-000000000001', NULL, '018ea000-3001-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000001', 0.00, 8, NULL, 'METRIC', 120, 1),
    ('018ea000-7000-7000-8000-000000000002', NULL, '018ea000-3001-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000001', 0.00, 8, NULL, 'METRIC', 120, 2),
    ('018ea000-7000-7000-8000-000000000003', NULL, '018ea000-3001-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000001', 0.00, 8, NULL, 'METRIC', 120, 3),

    -- ── Tam Vücut A: Bench Press 3x10 ──────────────────────────
    ('018ea000-7000-7000-8000-000000000004', NULL, '018ea000-3002-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000001', 0.00, 10, NULL, 'METRIC', 90, 4),
    ('018ea000-7000-7000-8000-000000000005', NULL, '018ea000-3002-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000001', 0.00, 10, NULL, 'METRIC', 90, 5),
    ('018ea000-7000-7000-8000-000000000006', NULL, '018ea000-3002-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000001', 0.00, 10, NULL, 'METRIC', 90, 6),

    -- ── Tam Vücut B: Deadlift 3x5 ──────────────────────────────
    ('018ea000-7000-7000-8000-000000000007', NULL, '018ea000-3003-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000003', 0.00, 5, NULL, 'METRIC', 180, 7),
    ('018ea000-7000-7000-8000-000000000008', NULL, '018ea000-3003-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000003', 0.00, 5, NULL, 'METRIC', 180, 8),
    ('018ea000-7000-7000-8000-000000000009', NULL, '018ea000-3003-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000003', 0.00, 5, NULL, 'METRIC', 180, 9),

    -- ── Tam Vücut B: Pull-up 3x8 ───────────────────────────────
    ('018ea000-7000-7000-8000-000000000010', NULL, '018ea000-3004-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000003', 0.00, 8, NULL, 'METRIC', 120, 10),
    ('018ea000-7000-7000-8000-000000000011', NULL, '018ea000-3004-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000003', 0.00, 8, NULL, 'METRIC', 120, 11),
    ('018ea000-7000-7000-8000-000000000012', NULL, '018ea000-3004-7000-8000-000000000000',
     '018ea000-6000-7000-8000-000000000003', 0.00, 8, NULL, 'METRIC', 120, 2.50);