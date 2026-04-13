INSERT INTO orders (user_id, status, total_price, deleted, created_at, updated_at) VALUES
(1, 'AWAITING_PAYMENT', 1249.98, false, NOW(), NOW()),
(2, 'PAID', 1999.99, false, NOW(), NOW()),
(1, 'PAID', 749.98, false, NOW(), NOW()),
(3, 'CANCELED', 349.99, false, NOW(), NOW()),
(4, 'PAID', 529.98, false, NOW(), NOW());