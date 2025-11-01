-- SQL скрипт для проверки структуры базы данных zoo_shop
-- Выполните этот скрипт в psql или другом клиенте PostgreSQL:
-- psql -U postgres -d zoo_shop -f check_db_structure.sql

-- Получение списка всех таблиц
SELECT 
    table_name 
FROM 
    information_schema.tables 
WHERE 
    table_schema = 'public' 
    AND table_type = 'BASE TABLE'
ORDER BY 
    table_name;

-- Получение структуры таблицы products (если существует)
SELECT 
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM 
    information_schema.columns
WHERE 
    table_schema = 'public' 
    AND table_name = 'products'
ORDER BY 
    ordinal_position;

-- Получение первичных ключей для таблицы products
SELECT
    kcu.column_name,
    tc.constraint_name
FROM 
    information_schema.table_constraints AS tc
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
WHERE 
    tc.table_schema = 'public'
    AND tc.table_name = 'products'
    AND tc.constraint_type = 'PRIMARY KEY';

