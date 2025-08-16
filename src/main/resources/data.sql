-- init.sql
-- Dummy dataset for Bento Recommender

-- Clear existing data
TRUNCATE TABLE bento_ingredients CASCADE;
TRUNCATE TABLE bento_tags CASCADE;
TRUNCATE TABLE bentos CASCADE;
TRUNCATE TABLE ingredients CASCADE;
TRUNCATE TABLE tags CASCADE;
TRUNCATE TABLE users CASCADE;

-- Ingredients
INSERT INTO ingredients (id, name) VALUES
(1, 'rice'),
(2, 'salmon'),
(3, 'egg'),
(4, 'chicken'),
(5, 'beef'),
(6, 'tofu'),
(7, 'broccoli'),
(8, 'tomato'),
(9, 'seaweed'),
(10, 'pork'),
(11, 'potato'),
(12, 'miso'),
(13, 'cucumber'),
(14, 'carrot'),
(15, 'onion');

-- Tags
INSERT INTO tags (id, name) VALUES
(1, 'vegan'),
(2, 'vegetarian'),
(3, 'spicy'),
(4, 'seafood'),
(5, 'low_calorie'),
(6, 'high_protein'),
(7, 'traditional'),
(8, 'fusion');

-- Bentos
INSERT INTO bentos (id, name, description, calorie) VALUES
(1, 'Salmon Bento', 'Grilled salmon with rice and vegetables', 550),
(2, 'Chicken Teriyaki Bento', 'Chicken teriyaki with steamed rice', 600),
(3, 'Beef Sukiyaki Bento', 'Sweet soy beef with veggies', 700),
(4, 'Vegan Tofu Bento', 'Tofu and veggies over rice', 400),
(5, 'Spicy Pork Bento', 'Pork stir-fry with chili sauce', 650),
(6, 'Seaweed Rice Bento', 'Rice wrapped in nori with pickles', 350),
(7, 'Miso Chicken Bento', 'Chicken with miso sauce', 500),
(8, 'Broccoli Beef Bento', 'Beef with broccoli stir-fry', 650),
(9, 'Egg Omelet Bento', 'Japanese-style omelet with rice', 450),
(10, 'Cucumber Roll Bento', 'Vegetarian cucumber sushi rolls', 300),
(11, 'Carrot Tofu Bento', 'Tofu stir-fry with carrots', 380),
(12, 'Spicy Salmon Bento', 'Salmon with spicy mayo sauce', 580),
(13, 'Potato Croquette Bento', 'Fried potato croquettes with rice', 500),
(14, 'Seafood Mix Bento', 'Mixed seafood over rice', 720),
(15, 'Low Cal Veggie Bento', 'Steamed veggies with light sauce', 320),
(16, 'Fusion Teriyaki Beef', 'Beef teriyaki with fusion spices', 680),
(17, 'Onion Chicken Bento', 'Chicken stir-fry with onions', 560),
(18, 'Sweet Tofu Bento', 'Tofu in sweet soy glaze', 390),
(19, 'Spicy Veggie Bento', 'Vegetables in chili sauce', 410),
(20, 'Traditional Sushi Bento', 'Assorted sushi pieces', 500);

-- Bento-Ingredients mapping
INSERT INTO bento_ingredients (bento_id, ingredient_id) VALUES
(1, 1), (1, 2), (1, 7), (1, 8),
(2, 1), (2, 4), (2, 7),
(3, 1), (3, 5), (3, 14), (3, 15),
(4, 1), (4, 6), (4, 7), (4, 8),
(5, 1), (5, 10), (5, 14),
(6, 1), (6, 9), (6, 13),
(7, 1), (7, 4), (7, 12),
(8, 1), (8, 5), (8, 7),
(9, 1), (9, 3), (9, 14),
(10, 13), (10, 9), (10, 1),
(11, 1), (11, 6), (11, 14),
(12, 1), (12, 2), (12, 3),
(13, 1), (13, 11), (13, 8),
(14, 1), (14, 4), (14, 2), (14, 10),
(15, 7), (15, 8), (15, 13),
(16, 1), (16, 5), (16, 14),
(17, 1), (17, 4), (17, 15),
(18, 1), (18, 6), (18, 12),
(19, 1), (19, 7), (19, 14),
(20, 1), (20, 2), (20, 3), (20, 9);

-- Bento-Tags mapping
INSERT INTO bento_tags (bento_id, tag_id) VALUES
(1, 4), (1, 6), (1, 7),
(2, 6), (2, 7),
(3, 6), (3, 7),
(4, 1), (4, 2), (4, 5),
(5, 3), (5, 6),
(6, 2), (6, 5),
(7, 6), (7, 7),
(8, 6), (8, 8),
(9, 2), (9, 5),
(10, 1), (10, 2), (10, 5),
(11, 1), (11, 5),
(12, 3), (12, 4), (12, 6),
(13, 2), (13, 5),
(14, 4), (14, 6),
(15, 1), (15, 5),
(16, 6), (16, 8),
(17, 6), (17, 7),
(18, 1), (18, 2),
(19, 1), (19, 3),
(20, 4), (20, 7);

-- Users
INSERT INTO users (id, name) VALUES
(1, 'Alice'),
(2, 'Bob'),
(3, 'Charlie');
