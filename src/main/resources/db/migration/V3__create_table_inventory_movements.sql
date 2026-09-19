CREATE TABLE inventory_movements (
     id SERIAL PRIMARY KEY,
     product_id INTEGER REFERENCES products(id),
     movement_type VARCHAR(20) NOT NULL, -- 'IN', 'OUT', 'ADJUSTMENT'
     reason VARCHAR(50) NOT NULL, -- 'PURCHASE', 'SALE', 'RETURN', 'INVENTORY_ADJUSTMENT'
     quantity INTEGER NOT NULL,
     previous_stock INTEGER NOT NULL,
     current_stock INTEGER NOT NULL,
     unit_price DECIMAL(12,2),
     notes TEXT,
     user_id INTEGER REFERENCES users(id),
     created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     deleted_date TIMESTAMP
);
