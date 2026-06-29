INSERT INTO product_category (
    category_code,
    category_name,
    display_order,
    is_active,
    description,
    created_at,
    updated_at
) VALUES
    ('FOOD', '食品', 10, TRUE, '食品カテゴリ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('BOOK', '書籍', 20, TRUE, '書籍カテゴリ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DAILY', '日用品', 30, FALSE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
