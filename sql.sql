CREATE TABLE productos (
  id_producto SERIAL PRIMARY KEY,
  nombre_producto VARCHAR(255) NOT NULL,
  descripcion TEXT,
  categoria VARCHAR(120),
  marca VARCHAR(120),
  valor_unitario NUMERIC(12,2) DEFAULT 0,
  precio_venta NUMERIC(12,2) DEFAULT 0,
  stock_actual INTEGER DEFAULT 0,
  codigo_barras VARCHAR(100)
);

CREATE TABLE ventas (
  id_venta SERIAL PRIMARY KEY,
  cliente_nombre VARCHAR(255) NOT NULL,
  cliente_identificacion VARCHAR(50),
  fecha TIMESTAMP NOT NULL DEFAULT now(),
  subtotal NUMERIC(14,2),
  iva NUMERIC(14,2),
  total NUMERIC(14,2)
);

CREATE TABLE detalles_venta (
  id_detalle SERIAL PRIMARY KEY,
  id_venta INTEGER REFERENCES ventas(id_venta) ON DELETE CASCADE,
  id_producto INTEGER REFERENCES productos(id_producto),
  cantidad INTEGER,
  precio_unitario NUMERIC(12,2),
  subtotal NUMERIC(14,2)
);

