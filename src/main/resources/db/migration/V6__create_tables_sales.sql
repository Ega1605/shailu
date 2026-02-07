CREATE TABLE sales (
       id SERIAL PRIMARY KEY,
       customer_id INTEGER REFERENCES customers(id),
       seller_id INTEGER REFERENCES users(id),
       general_discount DECIMAL(12,2) DEFAULT 0,
       tax_amount DECIMAL(12,2) DEFAULT 0,
       total DECIMAL(12,2) NOT NULL DEFAULT 0,
       payment_type VARCHAR(20) NOT NULL, -- 'CASH', 'CREDIT', 'DEBIT', 'TRANSFER'
       status VARCHAR(20) DEFAULT 'COMPLETED', -- 'PENDING', 'COMPLETED', 'CANCELLED'
       notes TEXT,
       created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       deleted_date TIMESTAMP
);

CREATE TABLE sale_details (
      id SERIAL PRIMARY KEY,
      sale_id INTEGER REFERENCES sales(id) ON DELETE CASCADE,
      product_id INTEGER REFERENCES products(id),
      quantity INTEGER NOT NULL,
      unit_price DECIMAL(12,2) NOT NULL,
      item_discount DECIMAL(12,2) DEFAULT 0,
      item_subtotal DECIMAL(12,2) NOT NULL,
      created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      deleted_date TIMESTAMP
);