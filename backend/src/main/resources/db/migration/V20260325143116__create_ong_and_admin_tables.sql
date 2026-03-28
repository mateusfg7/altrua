CREATE TABLE IF NOT EXISTS "ongs" (
    "id" UUID PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "slug" VARCHAR(255) NOT NULL,
    "cnpj" VARCHAR(14),
    "description" TEXT,
    "email" VARCHAR(255) NOT NULL,
    "phone" VARCHAR(20),
    "category" VARCHAR(100) NOT NULL,
    "status" VARCHAR(30) NOT NULL,
    "logo_url" VARCHAR(500),
    "banner_url" VARCHAR(500),
    "donation_info" TEXT,
    "latitude" DECIMAL(10, 8),
    "longitude" DECIMAL(11, 8),
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted_at" TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS "ong_administrators" (
    "user_id" UUID NOT NULL,
    "ong_id" UUID NOT NULL,
    "assigned_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "is_creator" BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY ("user_id", "ong_id"),
    
    FOREIGN KEY ("user_id") REFERENCES "users"("id"),
    FOREIGN KEY ("ong_id") REFERENCES "ongs"("id") ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS "uk_active_ong_slug" ON "ongs"("slug") 
WHERE "deleted_at" IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS "uk_active_ong_cnpj" ON "ongs"("cnpj") 
WHERE ("cnpj" IS NOT NULL AND "deleted_at" IS NULL);

CREATE INDEX IF NOT EXISTS "idx_ong_admin_ong_id" ON "ong_administrators"("ong_id");