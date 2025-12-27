# Configuração de Email para Desenvolvimento

Este projeto utiliza **MailSlurper** como servidor SMTP local para testes de email em ambiente de desenvolvimento.

## O que é MailSlurper?

MailSlurper é um servidor SMTP falso (mock) que:
- Aceita conexões SMTP na porta 2500
- Armazena emails em banco SQLite
- Possui interface web para visualizar emails enviados
- Disponível em: https://github.com/ryenus/MailSlurper

## Instalação

### Via NPM
```bash
npm install -g mailslurper
```

### Via Docker
```bash
docker run -p 2500:2500 -p 8085:8085 -p 8080:8080 \
  ryenus/mailslurper:latest
```

## Executar MailSlurper

```bash
mailslurper
```

Após iniciar, estará disponível em:
- **SMTP**: localhost:2500
- **API REST**: http://localhost:8085
- **Interface Web**: http://localhost:8080

## Configuração no application-dev.yml

Já configurado com:
```yaml
email:
  host: localhost
  port: 2500
  username:
  password:
  from: dev-test@conexaoauth.local
  from-name: ConexãoAuth Dev
  auth: false
  starttls: false
```

## Como Testar

### 1. Iniciar o MailSlurper
```bash
mailslurper
```

### 2. Iniciar a aplicação com perfil dev
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Acessar a interface web do MailSlurper
Navegue para: http://localhost:8080

### 4. Testar endpoint de recuperação de senha
```bash
curl -X POST http://localhost:8090/api/v1/auth/recuperar-senha/solicitar \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@exemplo.com"}'
```

### 5. Verificar o email na interface do MailSlurper
Abra http://localhost:8080 e o email com o código de 6 dígitos aparecerá na lista.

### 6. Validar o código usando o email recebido
```bash
curl -X POST http://localhost:8090/api/v1/auth/recuperar-senha/validar \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@exemplo.com","codigo":"123456","novaSenha":"NovaSenha@123"}'
```

## API REST do MailSlurper

### Listar emails
```bash
curl http://localhost:8085/emails
```

### Limpar todos os emails
```bash
curl -X DELETE http://localhost:8085/emails
```

### Obter email por ID
```bash
curl http://localhost:8085/emails/{id}
```

## Solução de Problemas

### Email não chega no MailSlurper
- Verifique se o MailSlurper está rodando (localhost:2500)
- Verifique se a aplicação está usando o perfil `dev`
- Verifique os logs da aplicação para erros de email

### Erro de conexão
- Verifique se a porta 2500 não está em uso
- Verifique o firewall

### Nenhum email aparece na interface
- O MailSlurper pode precisar de restart após muitos emails
- Limpe os emails antigos via API ou interface web
