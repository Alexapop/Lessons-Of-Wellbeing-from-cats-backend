-- Categories
INSERT INTO categories (name) VALUES ('YOGA');
INSERT INTO categories (name) VALUES ('AYURVEDA');
INSERT INTO categories (name) VALUES ('KOMBUCHA_TEA_LOUNGE');

-- Guides (the people who teach the classes)
INSERT INTO guides (name) VALUES ('Aria');
INSERT INTO guides (name) VALUES ('Master Tea Artisan');
INSERT INTO guides (name) VALUES ('Leo');
INSERT INTO guides (name) VALUES ('Dr. Priya (Ayurvedic Practitioner)');
INSERT INTO guides (name) VALUES ('Elena');
INSERT INTO guides (name) VALUES ('Lounge Host');

-- Hosts (the sanctuary cats)
INSERT INTO hosts (name) VALUES ('Mia');
INSERT INTO hosts (name) VALUES ('Thor');

-- Classes
INSERT INTO classes (title, description, level, location, start_date_time, duration_minutes, category_id, guide_id)
VALUES ('Queen Mia Restorative Cat Nap Yoga & Pranayama', 'Slow-paced restorative yoga supported by bolsters, with deep breathing, hip openers, and quiet contemplation alongside Queen Mia.', 'GENTLE_RESTORATIVE', 'The Velvet Sun Lounge (Main Hall)', '2026-10-09 07:30:00', 75, 1, 1);

INSERT INTO classes (title, description, level, location, start_date_time, duration_minutes, category_id, guide_id)
VALUES ('Kombucha Tea Tasting & Cat Meet & Greet', 'Enjoy homemade kombuchas and aromatic botanical teas while spending a relaxing afternoon with Mia and Thor.', 'ALL_LEVELS', 'The Garden Tea & Kombucha Lounge', '2026-10-09 16:30:00', 90, 3, 2);

INSERT INTO classes (title, description, level, location, start_date_time, duration_minutes, category_id, guide_id)
VALUES ('Thor’s Morning Feline Flow & Cat-Cow Vinyasa', 'A playful yoga sequence inspired by cat movements, with fluid spinal waves, warrior stretches, and energetic balance.', 'ENERGETIC_FLOW', 'Sunbeam Studio', '2026-10-10 07:45:00', 75, 1, 3);

INSERT INTO classes (title, description, level, location, start_date_time, duration_minutes, category_id, guide_id)
VALUES ('Ayurveda & Feline Self-Care Workshop', 'Explore Ayurvedic traditions and gentle daily self-care rituals, including massage, herbal teas, and seasonal routines.', 'ALL_LEVELS', 'Ayurveda Wisdom Room', '2026-10-10 17:00:00', 90, 2, 4);

INSERT INTO classes (title, description, level, location, start_date_time, duration_minutes, category_id, guide_id)
VALUES ('Sunset Yin Yoga & Purr Sound Bath', 'Long-held Yin stretches accompanied by singing bowls and cat purring for a calm transition into the evening.', 'GENTLE_RESTORATIVE', 'The Velvet Sun Lounge', '2026-10-12 18:30:00', 75, 1, 5);

INSERT INTO classes (title, description, level, location, start_date_time, duration_minutes, category_id, guide_id)
VALUES ('Evening Kombucha, Golden Milk & Tea Lounge Session', 'Relax with warm golden milk or seasonal kombucha, read a wellbeing book, and enjoy the company of Mia and Thor.', 'ALL_LEVELS', 'The Garden Tea & Kombucha Lounge', '2026-10-12 19:30:00', 90, 3, 6);

INSERT INTO classes (title, description, level, location, start_date_time, duration_minutes, category_id, guide_id)
VALUES ('Weekend Thor Playful Partner Yoga & Agility', 'Partner and solo yoga poses exploring balance, laughter, and playful movement, with Thor watching the humans stretch.', 'BEGINNERS', 'Sunbeam Studio', '2026-10-13 07:30:00', 90, 1, 3);

INSERT INTO classes (title, description, level, location, start_date_time, duration_minutes, category_id, guide_id)
VALUES ('Ayurvedic Tea Blending & Mindful Sip Masterclass', 'Create a botanical tea blend with guidance from the tea artisan and enjoy a mindful tasting supervised by Queen Mia.', 'ALL_LEVELS', 'Ayurveda Wisdom Room', '2026-10-14 18:00:00', 90, 2, 2);

-- Which cat hosts which class (1 = Mia, 2 = Thor)
INSERT INTO class_host (class_id, host_id) VALUES (1, 1);
INSERT INTO class_host (class_id, host_id) VALUES (2, 1);
INSERT INTO class_host (class_id, host_id) VALUES (2, 2);
INSERT INTO class_host (class_id, host_id) VALUES (3, 2);
INSERT INTO class_host (class_id, host_id) VALUES (4, 1);
INSERT INTO class_host (class_id, host_id) VALUES (5, 1);
INSERT INTO class_host (class_id, host_id) VALUES (5, 2);
INSERT INTO class_host (class_id, host_id) VALUES (6, 1);
INSERT INTO class_host (class_id, host_id) VALUES (6, 2);
INSERT INTO class_host (class_id, host_id) VALUES (7, 2);
INSERT INTO class_host (class_id, host_id) VALUES (8, 1);
