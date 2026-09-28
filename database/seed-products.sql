CREATE DATABASE IF NOT EXISTS mall_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mall_system;

INSERT INTO products
    (product_code, name, description, price, stock, status, version, created_at, updated_at)
VALUES
    ('COCA-330ML', '可口可乐 330ml', '经典原味碳酸饮料', 3.50, 100, 'ACTIVE', 0, NOW(), NOW()),
    ('ORANGE-500ML', '橙味汽水 500ml', '清爽橙味汽水', 4.00, 80, 'ACTIVE', 0, NOW(), NOW()),
    ('WATER-550ML', '矿泉水 550ml', '日常饮用矿泉水', 2.00, 200, 'ACTIVE', 0, NOW(), NOW()),
    ('CHIPS-ORIGINAL', '原味薯片', '酥脆原味薯片', 8.50, 60, 'ACTIVE', 0, NOW(), NOW()),
    ('CHOCO-CLASSIC', '经典巧克力', '醇厚可可巧克力', 12.00, 50, 'ACTIVE', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    product_code = VALUES(product_code);
