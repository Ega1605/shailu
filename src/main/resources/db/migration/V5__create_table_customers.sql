CREATE TABLE customers (
   id SERIAL PRIMARY KEY,
   code VARCHAR(20) UNIQUE NOT NULL,
   tax_regime_id INTEGER REFERENCES tax_regimes(id),
   first_name VARCHAR(100) NOT NULL,
   last_name VARCHAR(100) NOT NULL,
   reason_social VARCHAR(200), -- For companies/clinics
   rfc VARCHAR(20) NOT NULL, -- Equivalent to RFC
   address TEXT,
   phone VARCHAR(20),
   email VARCHAR(100),
   zip_code VARCHAR(100),
   credit_limit DECIMAL(12,2) DEFAULT 0,
   credit_days INTEGER DEFAULT 0,
   special_discount DECIMAL(5,2) DEFAULT 0, -- Discount percentage
   is_active BOOLEAN DEFAULT true,
   created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   deleted_date TIMESTAMP
);

    INSERT INTO customers (
    code,
    tax_regime_id,
    first_name,
    last_name,
    rfc,
    address,
    zip_code,
    is_active
)
  VALUES (
      'GEN-001',
      (SELECT id FROM tax_regimes WHERE code = 616 LIMIT 1),
      'Público',
      'General',
      'XAXX010101000',
      'VENTA DE MOSTRADOR',
      '00000',
      true
      )
  ON CONFLICT (code) DO NOTHING;