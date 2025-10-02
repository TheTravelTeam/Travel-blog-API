-- 👤 Utilisateurs
INSERT INTO user (email, password, status, pseudo, biography, created_at, updated_at, avatar) VALUES ('alice@example.com', '$2a$10$PPZ4qb.mQcIgX6mxEmecKe.TW8kWGVZG2IPBbtlrKg32zOM5Zopve', 'ACTIVE', 'alice', 'Passionnée de voyage', NOW(), NOW(), './Images/avatar.jpg');

INSERT INTO user (email, password, status, pseudo, biography, created_at, updated_at, avatar) VALUES ('bob@example.com', '$2a$10$r3tmkLC6jMvtmOCdIhNutOYxDPAo0eoE17Mi.SrebwLFGK/e7NaYG', 'ACTIVE', 'bob', 'Photographe globe-trotteur', NOW(), NOW(), './Images/avatar.jpg');

INSERT INTO user (email, password, status, pseudo, biography, created_at, updated_at, avatar) VALUES ('charlie@example.com', '$2a$10$fOzCYwHbHdj3z1gznRzvsO/D4glNtva9iYN/gDvL29U7N9vhmDoDa', 'ACTIVE', 'charlie', 'Aventurier en sac à dos', NOW(), NOW(), './Images/avatar.jpg');

INSERT INTO user (email, password, status, pseudo, biography, created_at, updated_at, avatar) VALUES ('diane@example.com', '$2a$10$PPZ4qb.mQcIgX6mxEmecKe.TW8kWGVZG2IPBbtlrKg32zOM5Zopve', 'ACTIVE', 'diane', 'Digital nomade passionnée de café.', NOW(), NOW(), './Images/avatar.jpg');

INSERT INTO user (email, password, status, pseudo, biography, created_at, updated_at, avatar) VALUES ('etienne@example.com', '$2a$10$r3tmkLC6jMvtmOCdIhNutOYxDPAo0eoE17Mi.SrebwLFGK/e7NaYG', 'ACTIVE', 'etienne', 'Explorateur des grands espaces canadiens.', NOW(), NOW(), './Images/avatar.jpg');

INSERT INTO user (email, password, status, pseudo, biography, created_at, updated_at, avatar) VALUES ('fatima@example.com', '$2a$10$fOzCYwHbHdj3z1gznRzvsO/D4glNtva9iYN/gDvL29U7N9vhmDoDa', 'ACTIVE', 'fatima', 'Créatrice de contenu voyage.', NOW(), NOW(), './Images/avatar.jpg');


-- 🔥 Rôles
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_roles (user_id, roles) VALUES (2, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (3, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (4, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (5, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (6, 'ROLE_USER');

-- 📘 Carnets de voyage (sans cover_media_id pour l'instant)
INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date)  VALUES ('Tour du monde en solo', 'Mon aventure autour du monde', 1, true, false, true, 'DISABLED', NOW(), NOW(), 50.48, 3.33, CURDATE(), NULL);

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Escapade en Asie', 'Découverte des saveurs et cultures d''Asie', 2, true, false, true, 'COMPLETED', NOW(), NOW(), 30, 18, CURDATE(), CURDATE());

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Road trip en Europe', 'Voyage en van à travers l''Europe', 3, true, false, true, 'IN_PROGRESS', NOW(), NOW(), 20, 50, CURDATE(), NULL);

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Road trip en Europe2', 'Voyage en van à travers l''Europe', 3, true, false, true, 'IN_PROGRESS', NOW(), NOW(), 30, 50, CURDATE(), NULL);

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Road trip en Europe3', 'Voyage en van à travers l''Europe', 3, true, true, true, 'IN_PROGRESS', NOW(), NOW(), 40, 50, CURDATE(), NULL);

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Cafés d''Europe du Nord', 'Découverte des meilleurs coffee shops scandinaves.', 4, true, false, true, 'IN_PROGRESS', NOW(), NOW(), 59.9139, 10.7522, '2024-04-01', '2024-04-20');

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Immersion dans les Rocheuses', 'Randonnées et bivouacs au Canada.', 5, true, false, true, 'COMPLETED', NOW(), NOW(), 51.0486, -114.0708, '2023-07-10', '2023-08-05');

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Désert et étoiles', 'Expérience nocturne dans le Sahara.', 6, true, false, true, 'IN_PROGRESS', NOW(), NOW(), 31.5091, -4.0117, '2024-02-15', NULL);

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Escapade dans les Cyclades', 'Îles grecques et couchers de soleil inoubliables.', 4, true, false, true, 'PLANNED', NOW(), NOW(), 36.3932, 25.4615, '2024-06-02', '2024-06-18');

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude, start_date, end_date) VALUES ('Saveurs du Maghreb', 'Circuit culinaire en Afrique du Nord.', 6, true, false, true, 'IN_PROGRESS', NOW(), NOW(), 34.0209, -6.8416, '2024-03-10', '2024-03-24');


INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude) VALUES ('Road trip en Europe2', 'Voyage en van à travers l''Europe', 3, true, false, true, 'IN_PROGRESS', NOW(), NOW(), 30, 50);

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude) VALUES ('Road trip en Europe3', 'Voyage en van à travers l''Europe', 3, true, true, true, 'IN_PROGRESS', NOW(), NOW(), 40, 50);


-- 🗺️ Étapes
INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Paris', 'Départ officiel', 48.8566, 2.3522, 'Paris', 'France', 'Europe','2020-08-12', '2020-08-12', 'COMPLETED', 1, NOW(), NOW(), 0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Bangkok', 'Street food et temples', 13.7563, 100.5018, 'Bangkok', 'Thaïlande', 'Asia','2020-08-12','2020-08-12','IN_PROGRESS',1,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Berlin', 'Art urbain et histoire', 52.52, 13.405, 'Berlin', 'Allemagne', 'Europe','2020-08-12','2020-08-12','COMPLETED',1,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Kyoto', 'Traditions et temples', 35.0116, 135.7681, 'Kyoto', 'Japon', 'Asia','2020-08-12','2020-08-12','COMPLETED',2,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Hanoï', 'Marchés et street food', 21.0285, 105.8542, 'Hanoï', 'Vietnam', 'Asia','2020-08-12','2020-08-12','IN_PROGRESS',2,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Lisbonne', 'Ville lumineuse', 38.7223, -9.1393, 'Lisbonne', 'Portugal', 'Europe','2020-08-12','2020-08-12','COMPLETED',3,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Paris', 'Départ officiel', 48.8566, 2.3522, 'Paris', 'France', 'Europe','2020-08-12', '2020-08-12', 'COMPLETED', 5, NOW(), NOW(), 0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Prague', 'Balade dans la vieille ville', 50.0755, 14.4378, 'Prague', 'République tchèque', 'Europe','2020-08-14','2020-08-15','COMPLETED',3,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Amsterdam', 'Exploration des canaux en vélo', 52.3676, 4.9041, 'Amsterdam', 'Pays-Bas', 'Europe','2020-09-01','2020-09-03','COMPLETED',4,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Copenhague', 'Design scandinave et hygge', 55.6761, 12.5683, 'Copenhague', 'Danemark', 'Europe','2020-09-04','2020-09-06','IN_PROGRESS',4,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Bruges', 'Balade en barque sur les canaux', 51.2093, 3.2247, 'Bruges', 'Belgique', 'Europe','2020-09-10','2020-09-11','COMPLETED',5,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Anvers', 'Street art et mode', 51.2194, 4.4025, 'Anvers', 'Belgique', 'Europe','2020-09-12','2020-09-13','IN_PROGRESS',5,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Oslo', 'Dégustation de torréfactions locales', 59.9139, 10.7522, 'Oslo', 'Norvège', 'Europe','2024-04-02','2024-04-05','COMPLETED',6,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Stockholm', 'Ateliers latte art', 59.3293, 18.0686, 'Stockholm', 'Suède', 'Europe','2024-04-06','2024-04-10','IN_PROGRESS',6,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Banff', 'Randonnée au lac Moraine', 51.1784, -115.5708, 'Banff', 'Canada', 'North America','2023-07-12','2023-07-18','COMPLETED',7,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Jasper', 'Observation de la faune', 52.8734, -118.0814, 'Jasper', 'Canada', 'North America','2023-07-19','2023-07-23','COMPLETED',7,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Merzouga', 'Nuit sous les étoiles', 31.0994, -4.0117, 'Merzouga', 'Maroc', 'Africa','2024-02-16','2024-02-18','COMPLETED',8,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Tinfou', 'Balade en dromadaire', 29.7391, -5.5500, 'Tinfou', 'Maroc', 'Africa','2024-02-19','2024-02-21','IN_PROGRESS',8,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Santorin', 'Couchers de soleil à Oia', 36.3932, 25.4615, 'Santorin', 'Grèce', 'Europe','2024-06-03','2024-06-07','PLANNED',9,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Paros', 'Cours de cuisine grecque', 37.0850, 25.1500, 'Paros', 'Grèce', 'Europe','2024-06-08','2024-06-12','PLANNED',9,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Marrakech', 'Marchés et épices', 31.6295, -7.9811, 'Marrakech', 'Maroc', 'Africa','2024-03-11','2024-03-15','COMPLETED',10,NOW(),NOW(),0);

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at, likes_count) VALUES ('Fès', 'Ateliers culinaires traditionnels', 34.0331, -5.0003, 'Fès', 'Maroc', 'Africa','2024-03-16','2024-03-20','IN_PROGRESS',10,NOW(),NOW(),0);


INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at) VALUES ('Paris', 'Départ officiel', 48.8566, 2.3522, 'Paris', 'France', 'Europe','2020-08-12', '2020-08-12', 'COMPLETED', 5, NOW(), NOW());


-- 🎥 Médias
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716053/pexels-photo-32649619_pzo1jk.jpg', 'PHOTO', true, 1, NOW(), NOW(), NULL);

INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716048/xl_lens-1209823_1920_mnq4az.jpg', 'VIDEO', true, 2, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716048/pexels-photo-32392457_mknv7k.jpg', 'PHOTO', true, NULL, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716053/pexels-photo-32649619_pzo1jk.jpg', 'PHOTO', true, 4, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716048/xl_lens-1209823_1920_mnq4az.jpg', 'PHOTO', true, 5, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716048/pexels-photo-32392457_mknv7k.jpg', 'PHOTO', true, 6, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716048/xl_lens-1209823_1920_mnq4az.jpg', 'PHOTO', true, NULL, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id)VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716053/pexels-photo-32649619_pzo1jk.jpg', 'PHOTO', true, NULL, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716048/pexels-photo-32392457_mknv7k.jpg', 'PHOTO', true, 1, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716048/pexels-photo-32392457_mknv7k.jpg', 'PHOTO', true, NULL, NOW(), NOW(), 5);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758716048/pexels-photo-32392457_mknv7k.jpg', 'PHOTO', true, NULL, NOW(), NOW(), 6);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758656489/samples/balloons.jpg', 'PHOTO', true, NULL, NOW(), NOW(), 7);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758656484/samples/landscapes/landscape-panorama.jpg', 'PHOTO', true, NULL, NOW(), NOW(), 8);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://res.cloudinary.com/dfbgrwist/image/upload/v1758656484/samples/landscapes/nature-mountains.jpg', 'PHOTO', true, NULL, NOW(), NOW(), 4);

-- 💬 Commentaires
INSERT INTO comment (content, step_id, user_id, status, created_at, updated_at) VALUES ('Super début de voyage !', 1, 2, 'APPROVED', NOW(), NOW());
INSERT INTO comment (content, step_id, user_id, status, created_at, updated_at)VALUES ('Bangkok a l air incroyable !', 2, 1, 'APPROVED', NOW(), NOW());
INSERT INTO comment (content, step_id, user_id, status, created_at, updated_at) VALUES ('Berlin est top pour les artistes !', 3, 1, 'APPROVED', NOW(), NOW());


-- 🏷️ Thèmes
INSERT INTO theme (name, created_at, updated_at) VALUES ('Aventure', NOW(), NOW());
INSERT INTO theme (name, created_at, updated_at) VALUES ('Culture', NOW(), NOW());
INSERT INTO theme (name, created_at, updated_at) VALUES ('Food', NOW(), NOW());

-- 🔗 Jointure Step-Theme
INSERT INTO step_theme (step_id, theme_id) VALUES (1, 1);
INSERT INTO step_theme (step_id, theme_id) VALUES (2, 2);
INSERT INTO step_theme (step_id, theme_id) VALUES (2, 3);
INSERT INTO step_theme (step_id, theme_id) VALUES (3, 1);

-- 📝 Articles
INSERT INTO article (title, content, cover_url, user_id, slug, created_at, updated_at) VALUES ('Mes essentiels pour un tour du monde', 'Voici mes indispensables pour voyager léger et longtemps.', 'https://res.cloudinary.com/dfbgrwist/image/upload/v1758656484/samples/landscapes/landscape-panorama.jpg', 1, 'essentiels-tour-du-monde', NOW(), NOW());
INSERT INTO article (title, content, cover_url, user_id, slug, created_at, updated_at) VALUES ('Guide street food Bangkok', 'Les meilleurs plats à tester absolument dans les rues de Bangkok.', 'https://res.cloudinary.com/dfbgrwist/image/upload/v1758656483/samples/landscapes/beach-boat.jpg', 2, 'guide-street-food-bangkok', NOW(), NOW());
INSERT INTO article (title, content, cover_url, user_id, slug, created_at, updated_at) VALUES ('Berlin, capitale de la créativité', 'Pourquoi Berlin est un paradis pour les créateurs.', 'https://res.cloudinary.com/dfbgrwist/image/upload/v1758656483/samples/landscapes/architecture-signs.jpg', 3, 'berlin-capitale-creativite', NOW(), NOW());
INSERT INTO article (title, content, cover_url, user_id, slug, created_at, updated_at) VALUES ('Itinéraire caféiné en Scandinavie', 'Un tour des coffee shops incontournables d''Oslo à Stockholm.', 'https://res.cloudinary.com/dfbgrwist/image/upload/v1758656484/samples/landscapes/nature-mountains.jpg', 4, 'itineraire-cafeine-scandinavie', NOW(), NOW());
INSERT INTO article (title, content, cover_url, user_id, slug, created_at, updated_at) VALUES ('Randonner dans les Rocheuses', 'Mes conseils pour profiter des parcs nationaux canadiens.', 'https://res.cloudinary.com/dfbgrwist/image/upload/v1758656483/samples/people/bicycle.jpg', 5, 'randonner-dans-les-rocheuses', NOW(), NOW());
INSERT INTO article (title, content, cover_url, user_id, slug, created_at, updated_at) VALUES ('Bivouac sous les étoiles du Sahara', 'Préparer sa nuit dans le désert : équipement et sécurité.', 'https://res.cloudinary.com/dfbgrwist/image/upload/v1758656482/samples/sheep.jpg', 6, 'bivouac-sous-les-etoiles-du-sahara', NOW(), NOW());
INSERT INTO article (title, content, cover_url, user_id, slug, created_at, updated_at) VALUES ('Guide des Cyclades en 7 jours', 'Que voir et que faire lors d''une semaine dans les Cyclades.', 'https://res.cloudinary.com/dfbgrwist/image/upload/v1758656482/samples/food/pot-mussels.jpg', 4, 'guide-cyclades-7-jours', NOW(), NOW());
INSERT INTO article (title, content, cover_url, user_id, slug, created_at, updated_at) VALUES ('Street food marocain incontournable', 'Itinéraire gourmand de Marrakech à Fès.', 'https://res.cloudinary.com/demo/image/upload/v1700000000/articles/street-food-maroc.jpg', 6, 'street-food-marocain-incontournable', NOW(), NOW());

-- 🖼️ Médias d'articles
INSERT INTO media (file_url, media_type, is_visible, article_id, created_at, updated_at) VALUES ('https://res.cloudinary.com/demo/image/upload/v1700000000/articles/essentiels-cover.jpg', 'PHOTO', TRUE, 1, NOW(), NOW());
INSERT INTO media (file_url, media_type, is_visible, article_id, created_at, updated_at) VALUES ('https://res.cloudinary.com/demo/image/upload/v1700000000/articles/bangkok-cover.jpg', 'PHOTO', TRUE, 2, NOW(), NOW());
INSERT INTO media (file_url, media_type, is_visible, article_id, created_at, updated_at) VALUES ('https://res.cloudinary.com/demo/image/upload/v1700000000/articles/berlin-cover.jpg', 'PHOTO', TRUE, 3, NOW(), NOW());
INSERT INTO media (file_url, media_type, is_visible, article_id, created_at, updated_at) VALUES ('https://res.cloudinary.com/demo/image/upload/v1700000000/articles/scandinavie-coffee-cover.jpg', 'PHOTO', TRUE, 4, NOW(), NOW());
INSERT INTO media (file_url, media_type, is_visible, article_id, created_at, updated_at) VALUES ('https://res.cloudinary.com/demo/image/upload/v1700000000/articles/rocheuses-guide-cover.jpg', 'PHOTO', TRUE, 5, NOW(), NOW());
INSERT INTO media (file_url, media_type, is_visible, article_id, created_at, updated_at) VALUES ('https://res.cloudinary.com/demo/image/upload/v1700000000/articles/sahara-bivouac-cover.jpg', 'PHOTO', TRUE, 6, NOW(), NOW());
INSERT INTO media (file_url, media_type, is_visible, article_id, created_at, updated_at) VALUES ('https://res.cloudinary.com/demo/image/upload/v1700000000/articles/cyclades-guide-cover.jpg', 'PHOTO', TRUE, 7, NOW(), NOW());
INSERT INTO media (file_url, media_type, is_visible, article_id, created_at, updated_at) VALUES ('https://res.cloudinary.com/demo/image/upload/v1700000000/articles/street-food-maroc-cover.jpg', 'PHOTO', TRUE, 8, NOW(), NOW());

-- ✅ Mise à jour des cover_media_id dans travel_diary
UPDATE media  SET travel_diary_id  = 1 WHERE id = 3;
UPDATE media  SET travel_diary_id  = 2 WHERE id = 7;
UPDATE media  SET travel_diary_id  = 3 WHERE id = 8;
