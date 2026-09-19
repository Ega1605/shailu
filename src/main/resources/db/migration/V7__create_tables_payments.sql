CREATE TABLE payments (
      id SERIAL PRIMARY KEY,
      sale_id INTEGER REFERENCES sales(id),
      payment_method VARCHAR(20) NOT NULL, -- 'CASH', 'CARD', 'TRANSFER', 'CHECK'
      amount DECIMAL(12,2) NOT NULL,
      paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      user_id INTEGER REFERENCES users(id),
      notes TEXT
);

CREATE TABLE accounts_receivable (
     id SERIAL PRIMARY KEY,
     sale_id INTEGER REFERENCES sales(id),
     customer_id INTEGER REFERENCES customers(id),
     total_amount DECIMAL(12,2) NOT NULL,
     paid_amount DECIMAL(12,2) DEFAULT 0,
     remaining_balance DECIMAL(12,2) NOT NULL,
     due_date DATE NOT NULL,
     paid_at TIMESTAMP,
     status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'PAID', 'OVERDUE'
     days_overdue INTEGER DEFAULT 0,
     created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     deleted_date TIMESTAMP
);

CREATE TABLE accounts_receivable_payments (
      id SERIAL PRIMARY KEY,
      account_receivable_id INTEGER  REFERENCES accounts_receivable(id),
      amount DECIMAL(12,2) NOT NULL,
      payment_type VARCHAR(20), -- CASH, TRANSFER, CARD, etc.
      notes TEXT,
      created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

