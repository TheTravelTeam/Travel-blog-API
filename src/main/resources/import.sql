-- 👤 Utilisateurs
INSERT INTO user (email, password, status, username, biography, created_at, updated_at) VALUES ('alice@example.com', 'password123', 'ACTIVE', 'alice', 'Passionnée de voyage', NOW(), NOW()),  ('bob@example.com', 'password456', 'ACTIVE', 'bob', 'Photographe globe-trotteur', NOW(), NOW()),('charlie@example.com', 'password789', 'ACTIVE', 'charlie', 'Aventurier en sac à dos', NOW(), NOW());

-- 🔥 Rôles
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_roles (user_id, roles) VALUES (2, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (3, 'ROLE_USER');

-- 📘 Carnets de voyage
INSERT INTO travel_diary (id, title, description, user_id, can_comment, is_private, is_published, status, created_at, updated_at) VALUES (1, 'Tour du monde en solo', 'Mon aventure autour du monde', 1, true, false, true, 'IN_PROGRESS', NOW(), NOW()), (2, 'Escapade en Asie', 'Découverte des saveurs et cultures Asie', 2, true, false, true, 'COMPLETED', NOW(), NOW()), (3, 'Road trip en Europe', 'Voyage en van à travers Europe', 3, true, false, true, 'CAN_COMMENT', NOW(), NOW());

-- 🗺️ Étapes
INSERT INTO step (id, title, description, latitude, longitude, location, country, continent, start_date, end_date, status, travel_diary_id, created_at, updated_at) VALUES (1, 'Paris', 'Départ officiel', 48.8566, 2.3522, 'Paris', 'France', 'Europe', NOW(), NOW(), 'COMPLETED', 1, NOW(), NOW() ),(2, 'Bangkok', 'Street food, temples et marchés', 13.7563, 100.5018, 'Bangkok', 'Thaïlande', 'Asia', NOW(), NOW(), 'IN_PROGRESS', 1, NOW(), NOW()),(3, 'Berlin', 'Art urbain et histoire', 52.5200, 13.4050, 'Berlin', 'Allemagne', 'Europe', NOW(), NOW(), 'COMPLETED', 1, NOW(), NOW());

-- 🎥 Médias
INSERT INTO media (file_url, media_type, status, step_id, created_at, updated_at) VALUES ('https://example.com/photo-paris.jpg', 'PHOTO', 'VISIBLE', 1, NOW(), NOW()), ('https://example.com/video-bangkok.mp4', 'VIDEO', 'VISIBLE', 2, NOW(), NOW()),('https://example.com/photo-berlin.jpg', 'PHOTO', 'VISIBLE', 3, NOW(), NOW());

-- 💬 Commentaires
INSERT INTO comment (content, step_id, user_id, status, created_at, updated_at) VALUES('Super début de voyage !', 1, 2, 'APPROVED', NOW(), NOW()), ('Bangkok a lair incroyable !', 2, 1, 'APPROVED', NOW(), NOW()), ('Berlin est top pour les artistes !', 3, 1, 'APPROVED', NOW(), NOW());

-- 🏷️ Thèmes
INSERT INTO theme (id, name, created_at, updated_at) VALUES (1, 'Aventure', NOW(), NOW()), (2, 'Culture', NOW(), NOW()), (3, 'Food', NOW(), NOW());

-- 🔗 Jointure Step-Theme
INSERT INTO step_theme (step_id, theme_id) VALUES (1, 1);
INSERT INTO step_theme (step_id, theme_id) VALUES (2, 2);
INSERT INTO step_theme (step_id, theme_id) VALUES (2, 3);
INSERT INTO step_theme (step_id, theme_id) VALUES (3, 1);

-- 📝 Articles
INSERT INTO article (title, content, user_id, slug, created_at, updated_at) VALUES ('Mes essentiels pour un tour du monde', 'Voici mes indispensables pour voyager léger et longtemps.', 1, 'essentiels-tour-du-monde', NOW(), NOW()), ('Guide street food Bangkok', 'Les meilleurs plats à tester absolument dans les rues de Bangkok.', 2, 'guide-street-food-bangkok', NOW(), NOW()), ('Berlin, capitale de la créativité', 'Pourquoi Berlin est un paradis pour les créateurs.', 3, 'berlin-capitale-creativite', NOW(), NOW());
