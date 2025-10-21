# Spring AI RAG - Sistema de Consulta Inteligente

Sistema de RAG (Retrieval-Augmented Generation) completo usando Spring Boot 3.5.6, Spring AI, PostgreSQL com pgvector, Spring Security e Thymeleaf.

## 🏗️ Arquitetura

- **Backend**: Spring Boot 3.5.6 com Java 25
- **IA**: Spring AI + OpenAI GPT-3.5-turbo
- **Banco**: PostgreSQL com extensão pgvector
- **Segurança**: Spring Security com form-login
- **Interface**: Thymeleaf com Bootstrap 5
- **Rate Limiting**: Bucket4j
- **Logs**: SLF4J com MDC para requestId
- **Monitoramento**: Actuator + Prometheus
- **Testes**: Testcontainers com PostgreSQL pgvector

## 🚀 Instalação e Execução

### Pré-requisitos

1. **Java 25** instalado
2. **Maven 3.6+** instalado
3. **Docker** e **Docker Compose** instalados
4. **Chave API OpenAI** válida

### 1. Subir o Banco de Dados

```bash
docker compose up -d postgres
```

Aguarde o banco ficar saudável (healthcheck automático).

### 2. Configurar API Key

```bash
export OPENAI_API_KEY="sua_chave_api_openai_aqui"
```

**⚠️ OBRIGATÓRIO**: Sem a API key, a aplicação **FALHARÁ** ao iniciar com mensagem clara.

### 3. Executar a Aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 📋 Funcionalidades

### 🔍 Consulta RAG
- **Endpoint**: `/ask` (POST/GET)
- **Processo**: similaritySearch + ChatClient
- **TopK**: 8 documentos similares
- **Fallback**: "Não encontrei base suficiente"
- **Rate Limiting**: 10 requests/minuto (configurável)

### 📄 Ingestão de Documentos
- **Texto**: `/admin/ingest` (POST)
- **PDF**: `/admin/ingest-pdf` (POST)
- **Extração**: Apache Tika para PDFs
- **Validação**: Títulos únicos, conteúdo não-vazio

### 🔐 Segurança
- **Autenticação**: Form-login
- **Usuários**: admin/admin123, user/user123
- **Endpoints públicos**: `/`, `/ask`
- **Endpoints protegidos**: `/admin/**`

### 📊 Monitoramento
- **Health**: `/actuator/health`
- **Prometheus**: `/actuator/prometheus`
- **Logs**: Com MDC requestId
- **Métricas**: Documentos, perguntas, sucessos/falhas

## 🎯 Endpoints da API

### Públicos
```bash
# Página inicial
GET /

# Consulta RAG
POST /ask
GET /ask?question=Sua pergunta

# Login
GET /login
```

### Administrativos (Autenticação Obrigatória)
```bash
# Painel admin
GET /admin

# Ingestão de texto
POST /admin/ingest

# Ingestão de PDF
POST /admin/ingest-pdf

# Status do sistema
GET /admin/status
```

## 🧪 Testes

### Executar Todos os Testes
```bash
mvn test
```

### Executar Testes de Integração
```bash
mvn test -Dtest=RagIntegrationTest
```

### Executar Testes de Controller
```bash
mvn test -Dtest=AskControllerTest
```

Os testes usam **Testcontainers** com PostgreSQL pgvector real, validando:
- ✅ Ingestão real de documentos
- ✅ Busca por similaridade (topK=8)
- ✅ Consultas RAG sem strings mockadas
- ✅ Configuração do vector store

## 🐳 Execução com Docker

### Subir Apenas o Banco
```bash
docker compose up -d postgres
```

### Parar Serviços
```bash
docker compose down
```

### Limpar Volumes (CUIDADO: apaga dados)
```bash
docker compose down -v
```

## 🔧 Configurações

### application.yml
- **Virtual Threads**: Habilitado (`spring.threads.virtual.enabled=true`)
- **Banco**: PostgreSQL localhost:5432/aiapp
- **OpenAI**: Modelo gpt-3.5-turbo, temperatura 0.7
- **pgvector**: HNSW index, distância cosseno, 1536 dimensões
- **Rate Limiting**: 10 requests/minuto por padrão
- **Logs**: DEBUG para debugging, MDC com requestId

### Perfis
- **default**: Desenvolvimento local
- **test**: Testes com Testcontainers
- **docker**: Execução em container

## 🛠️ Desenvolvimento

### Estrutura do Projeto
```
src/
├── main/java/com/example/kb/
│   ├── config/          # Configurações (Security, DB, Rate Limit, MDC)
│   ├── controller/      # REST endpoints (Home, Ask, Admin, Login)
│   ├── domain/          # Entidades JPA (Documento, QuestionLog)
│   ├── service/         # Lógica de negócio (RAG, Ingestão, Extração)
│   └── SpringAiRagApplication.java
├── main/resources/
│   ├── templates/       # Thymeleaf (home, result, admin, login)
│   └── application.yml
└── test/
    ├── java/com/example/kb/
    │   ├── controller/  # Testes de controller
    │   └── integration/ # Testes de integração
    └── resources/
        └── application-test.yml
```

### Tecnologias Utilizadas
- **Spring Boot 3.5.6**: Framework principal
- **Spring AI 1.0.0-M4**: Integração com OpenAI
- **Spring Security**: Autenticação e autorização
- **PostgreSQL + pgvector**: Banco vetorial
- **Thymeleaf**: Templates web
- **Apache Tika 2.9.1**: Extração de texto de PDFs
- **Bucket4j 8.10.1**: Rate limiting
- **Testcontainers 1.20.4**: Testes de integração
- **Lombok**: Redução de boilerplate
- **Micrometer + Prometheus**: Métricas

## 🔐 Pontos que Exigem Segredo/Infraestrutura

### 1. API Key OpenAI
- **Localização**: Variável de ambiente `OPENAI_API_KEY`
- **Necessidade**: Chave válida da OpenAI
- **Impacto**: Sem ela, aplicação falha na inicialização
- **Custo**: Uso cobrado por token da OpenAI

### 2. Banco de Dados PostgreSQL
- **Localização**: localhost:5432 (desenvolvimento)
- **Necessidade**: PostgreSQL com extensão pgvector
- **Impacto**: Sem ele, não há armazenamento vetorial
- **Alternativa**: Docker Compose (incluído)

### 3. Conectividade de Rede
- **Localização**: Acesso à internet
- **Necessidade**: Para chamadas à API OpenAI
- **Impacto**: Sem internet, consultas RAG falham
- **Fallback**: Mensagem de erro clara

## 📝 Logs e Debugging

### Níveis de Log
- **DEBUG**: `com.example.kb`, `org.springframework.ai`
- **INFO**: Aplicação, ingestão, consultas
- **ERROR**: Falhas de conectividade, validação

### Logs Importantes
- ✅ "OPENAI_API_KEY validated successfully"
- ✅ "Texto ingerido com sucesso"
- ✅ "Encontrados X documentos similares"
- ✅ "Pergunta processada com sucesso em Xms"
- ❌ "OPENAI_API_KEY environment variable is required"

### MDC (Mapped Diagnostic Context)
Todos os logs incluem `requestId` para rastreamento:
```
2024-01-01 12:00:00 [http-nio-8080-exec-1] INFO [req-123-abc] com.example.kb.service.RagService - Processando pergunta RAG: Qual é a missão?
```

## 🚨 Troubleshooting

### Problema: "OPENAI_API_KEY environment variable is required"
**Solução**: Configure a variável de ambiente:
```bash
export OPENAI_API_KEY="sua_chave_aqui"
```

### Problema: "Connection refused" no banco
**Solução**: Suba o PostgreSQL:
```bash
docker compose up -d postgres
```

### Problema: "Não encontrei base suficiente"
**Solução**: Ingira documentos primeiro via `/admin`

### Problema: "Rate limit exceeded"
**Solução**: Aguarde ou ajuste a configuração em `application.yml`

### Problema: Testes falhando
**Solução**: Verifique se o Docker está rodando para Testcontainers

## 📊 Métricas Disponíveis

### Actuator Endpoints
- `/actuator/health`: Status da aplicação
- `/actuator/prometheus`: Métricas para Prometheus
- `/actuator/info`: Informações da aplicação

### Métricas Personalizadas
- `document_count`: Número de documentos
- `question_count`: Número de perguntas
- `success_rate`: Taxa de sucesso das consultas
- `response_time`: Tempo de resposta médio

## 🔄 Rate Limiting

### Configuração Padrão
- **Capacidade**: 10 requests
- **Refill Rate**: 2 requests por minuto
- **Período**: 60 segundos

### Configuração Customizada
Edite `application.yml`:
```yaml
rate-limit:
  ask:
    capacity: 20
    refill-rate: 5
    refill-period: 60
```

## 📄 Licença

Este projeto é fornecido como exemplo educacional. Verifique as licenças das dependências utilizadas.

---

**⚠️ LEMBRE-SE**: Este projeto **NÃO ACEITA** mocks, stubs ou dados fictícios no código de produção. Todas as funcionalidades são implementadas com recursos reais e conectividade real.

**🎯 CRITÉRIOS DE ACEITE**:
- ❌ Rejeita PRs com "TODO", "mock", "stub", "fake" em `src/main`
- ✅ Fail-fast para problemas de configuração
- ✅ Logs claros e informativos
- ✅ Testes com recursos reais (Testcontainers)
- ✅ Rate limiting funcional
- ✅ Segurança implementada
- ✅ Interface web responsiva