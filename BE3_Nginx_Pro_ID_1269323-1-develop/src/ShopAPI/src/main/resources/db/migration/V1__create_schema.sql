CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE address (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(200) NOT NULL
);

CREATE TABLE client (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_name VARCHAR(100) NOT NULL,
    client_surname VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL,
    gender CHAR(1) CHECK (gender IN ('M','F')),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    address_id UUID REFERENCES address(id) ON DELETE SET NULL
);


CREATE TABLE supplier (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    address_id UUID REFERENCES address(id) ON DELETE SET NULL,
    phone_number VARCHAR(20) NOT NULL
);

CREATE TABLE images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    image BYTEA NOT NULL
);

CREATE TABLE product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    available_stock INTEGER NOT NULL DEFAULT 0 CHECK (available_stock >= 0),
    last_update_date DATE DEFAULT CURRENT_DATE,
    supplier_id UUID REFERENCES supplier(id) ON DELETE SET NULL,
    image_id UUID REFERENCES images(id) ON DELETE SET NULL
);

CREATE INDEX idx_client_name ON client(client_name, client_surname);
CREATE INDEX idx_client_birthday ON client(birthday);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_price ON product(price);
CREATE INDEX idx_product_supplier ON product(supplier_id);
CREATE INDEX idx_address_city ON address(city);

CREATE OR REPLACE VIEW product_full_info AS
SELECT
    p.id,
    p.name,
    p.category,
    p.price,
    p.available_stock,
    p.last_update_date,
    s.name AS supplier_name,
    s.phone_number AS supplier_phone,
    a.country,
    a.city,
    a.street AS supplier_address,
    i.image
FROM product p
LEFT JOIN supplier s ON p.supplier_id = s.id
LEFT JOIN address a ON s.address_id = a.id
LEFT JOIN images i ON p.image_id = i.id;

CREATE OR REPLACE VIEW client_full_info AS
SELECT
    c.id,
    c.client_surname,
    c.birthday,
    c.gender,
    c.registration_date,
    a.country,
    a.city,
    a.street AS client_address
FROM client c
LEFT JOIN address a ON c.address_id = a.id;

CREATE OR REPLACE FUNCTION update_last_update_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_update_date = CURRENT_DATE;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_product_date
BEFORE UPDATE ON product
FOR EACH ROW
EXECUTE FUNCTION update_last_update_date();

CREATE OR REPLACE FUNCTION check_stock_availability(product_id UUID, quantity INTEGER)
RETURNS BOOLEAN AS $$
DECLARE
    stock INTEGER;
BEGIN
    SELECT available_stock INTO stock FROM product WHERE id = product_id;
    RETURN stock >= quantity;
END;
$$ LANGUAGE plpgsql;