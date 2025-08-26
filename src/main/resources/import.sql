-- 👤 Utilisateurs
INSERT INTO user (email, password, status, username, biography, created_at, updated_at, avatar) VALUES ('alice@example.com', 'password123', 'ACTIVE', 'alice', 'Passionnée de voyage', NOW(), NOW(), './Images/avatar.jpg');

INSERT INTO user (email, password, status, username, biography, created_at, updated_at, avatar) VALUES ('bob@example.com', 'password456', 'ACTIVE', 'bob', 'Photographe globe-trotteur', NOW(), NOW(), './Images/avatar.jpg');

INSERT INTO user (email, password, status, username, biography, created_at, updated_at, avatar) VALUES ('charlie@example.com', 'password789', 'ACTIVE', 'charlie', 'Aventurier en sac à dos', NOW(), NOW(), './Images/avatar.jpg');


-- 🔥 Rôles
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_roles (user_id, roles) VALUES (2, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (3, 'ROLE_USER');

-- 📘 Carnets de voyage (sans cover_media_id pour l'instant)
INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude)  VALUES ('Tour du monde en solo', 'Mon aventure autour du monde', 1, true, false, true, 'IN_PROGRESS', NOW(), NOW(), 50.48, 3.33);

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude) VALUES ('Escapade en Asie', 'Découverte des saveurs et cultures d''Asie', 2, true, false, true, 'COMPLETED', NOW(), NOW(), 30, 18);

INSERT INTO travel_diary (title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at, latitude, longitude) VALUES ('Road trip en Europe', 'Voyage en van à travers l''Europe', 3, true, false, true, 'IN_PROGRESS', NOW(), NOW(), 20, 50);

-- 🗺️ Étapes
INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at) VALUES ('Paris', 'Départ officiel', 48.8566, 2.3522, 'Paris', 'France', 'Europe','2020-08-12', '2020-08-12', 'COMPLETED', 1, NOW(), NOW());

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at)VALUES ('Bangkok', 'Street food et temples', 13.7563, 100.5018, 'Bangkok', 'Thaïlande', 'Asia','2020-08-12','2020-08-12','IN_PROGRESS',1,NOW(),NOW());

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at)VALUES ('Berlin', 'Art urbain et histoire', 52.52, 13.405, 'Berlin', 'Allemagne', 'Europe','2020-08-12','2020-08-12','COMPLETED',1,NOW(),NOW());

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at)VALUES ('Kyoto', 'Traditions et temples', 35.0116, 135.7681, 'Kyoto', 'Japon', 'Asia','2020-08-12','2020-08-12','COMPLETED',2,NOW(),NOW());

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at)VALUES ('Hanoï', 'Marchés et street food', 21.0285, 105.8542, 'Hanoï', 'Vietnam', 'Asia','2020-08-12','2020-08-12','IN_PROGRESS',2,NOW(),NOW());

INSERT INTO step (title, description, latitude, longitude, city, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at) VALUES ('Lisbonne', 'Ville lumineuse', 38.7223, -9.1393, 'Lisbonne', 'Portugal', 'Europe','2020-08-12','2020-08-12','COMPLETED',3,NOW(),NOW());

-- 🎥 Médias
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://www.echosciences-grenobler/uploads/article/image/attachment/1005418938/xl_lens-1209823_1920.jpg', 'PHOTO', true, 1, NOW(), NOW(), NULL);

INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://images.pexels.com/photos/32392457/pexels-photo-32392457.jpeg', 'VIDEO', true, 2, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://www.echosciences-grenoble.fr/uploads/article/image/attachment/1005418938/xl_lens-1209823_1920.jpg', 'PHOTO', true, NULL, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://example.com/photo-kyoto.jpg', 'PHOTO', true, 4, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://www.echosciences-grenoble.fr/uploads/article/image/attachment/1005418938/xl_lens-1209823_1920.jpg', 'PHOTO', true, 5, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://example.com/photo-lisbonne.jpg', 'PHOTO', true, 6, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://www.echosciences-grenoble.fr/uploads/article/image/attachment/1005418938/xl_lens-1209823_1920.jpg', 'PHOTO', true, NULL, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id)VALUES ('https://images.pexels.com/photos/32649619/pexels-photo-32649619.jpeg', 'PHOTO', true, NULL, NOW(), NOW(), NULL);
INSERT INTO media (file_url, media_type, is_visible, step_id, created_at, updated_at, travel_diary_id) VALUES ('https://images.pexels.com/photos/32649619/pexels-photo-32649619.jpeg', 'PHOTO', true, 1, NOW(), NOW(), NULL);

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
INSERT INTO article (title, content, user_id, slug, created_at, updated_at) VALUES ('Mes essentiels pour un tour du monde', 'Voici mes indispensables pour voyager léger et longtemps.', 1, 'essentiels-tour-du-monde', NOW(), NOW());
INSERT INTO article (title, content, user_id, slug, created_at, updated_at) VALUES ('Guide street food Bangkok', 'Les meilleurs plats à tester absolument dans les rues de Bangkok.', 2, 'guide-street-food-bangkok', NOW(), NOW());
INSERT INTO article (title, content, user_id, slug, created_at, updated_at) VALUES ('Berlin, capitale de la créativité', 'Pourquoi Berlin est un paradis pour les créateurs.', 3, 'berlin-capitale-creativite', NOW(), NOW());

-- ✅ Mise à jour des cover_media_id dans travel_diary
UPDATE media  SET travel_diary_id  = 1 WHERE id = 3;
UPDATE media  SET travel_diary_id  = 2 WHERE id = 7;
UPDATE media  SET travel_diary_id  = 3 WHERE id = 8;