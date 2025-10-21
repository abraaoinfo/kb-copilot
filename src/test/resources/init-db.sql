-- Inicialização do banco de dados para testes com pgvector
-- Este script é executado automaticamente quando o container PostgreSQL é criado

-- Habilita a extensão pgvector
CREATE EXTENSION IF NOT EXISTS vector;

-- Cria o schema se não existir
CREATE SCHEMA IF NOT EXISTS public;

-- Verifica se a extensão foi criada corretamente
SELECT extname FROM pg_extension WHERE extname = 'vector';


