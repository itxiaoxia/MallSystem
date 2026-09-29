-- MallSystem uses the SQLite database shared with MallAgent.
-- The Spring ApplicationRunner executes the same idempotent seed on startup.

INSERT OR IGNORE INTO products
    (product_code, name, description, price, stock, status, version, created_at, updated_at)
VALUES
    ('COCA-330ML', '可口可乐 330ml', '经典原味碳酸饮料', 3.50, 100, 'ACTIVE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ORANGE-500ML', '橙味汽水 500ml', '清爽橙味汽水', 4.00, 80, 'ACTIVE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('WATER-550ML', '矿泉水 550ml', '日常饮用矿泉水', 2.00, 200, 'ACTIVE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CHIPS-ORIGINAL', '原味薯片', '酥脆原味薯片', 8.50, 60, 'ACTIVE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CHOCO-CLASSIC', '经典巧克力', '醇厚可可巧克力', 12.00, 50, 'ACTIVE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
