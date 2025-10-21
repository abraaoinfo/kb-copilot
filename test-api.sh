#!/bin/bash

# Script de teste para demonstrar o funcionamento da API RAG
# Requer que a aplicação esteja rodando em localhost:8080

BASE_URL="http://localhost:8080"

echo "🚀 Testando Spring AI RAG API"
echo "================================"

# Verifica se a aplicação está rodando
echo "1. Verificando status da aplicação..."
curl -s "$BASE_URL/actuator/health" | jq '.' 2>/dev/null || echo "❌ Aplicação não está rodando ou jq não está instalado"
echo ""

# Testa página inicial
echo "2. Testando página inicial..."
curl -s "$BASE_URL/" | grep -q "Spring AI RAG" && echo "✅ Página inicial OK" || echo "❌ Página inicial com problema"
echo ""

# Testa consulta sem documentos (deve retornar erro)
echo "3. Testando consulta sem documentos..."
curl -X POST "$BASE_URL/ask" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=Qual é a missão da empresa?" \
  -s | grep -q "Não encontrei base suficiente" && echo "✅ Consulta sem documentos OK" || echo "❌ Consulta sem documentos com problema"
echo ""

# Testa endpoint de status
echo "4. Testando endpoint de status..."
curl -s "$BASE_URL/admin/status" | jq '.' 2>/dev/null || echo "❌ Endpoint de status com problema"
echo ""

echo "✅ Teste básico concluído!"
echo ""
echo "Para testar funcionalidades completas:"
echo "1. Acesse http://localhost:8080"
echo "2. Faça login com admin/admin123"
echo "3. Ingira alguns documentos"
echo "4. Faça perguntas sobre os documentos"
echo ""
echo "Para monitoramento:"
echo "- Health: http://localhost:8080/actuator/health"
echo "- Prometheus: http://localhost:8080/actuator/prometheus"