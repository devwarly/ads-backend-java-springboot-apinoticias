# Backend ADS

Este projeto é o backend da aplicação ADS, uma API RESTful desenvolvida com Spring Boot. Ele gerencia a lógica de negócio do site, incluindo autenticação de administradores, notícias, e a grade curricular do curso.

## 🚀 Tecnologias Usadas

  * **Java 21**: Linguagem principal da API.
  * **Spring Boot 3.x**: Framework para desenvolvimento rápido e robusto.
  * **Spring Data JPA**: Para comunicação com o banco de dados.
  * **MySQL**: Banco de dados relacional.
  * **Spring Security & JWT**: Para autenticação e autorização.
  * **Maven**: Gerenciador de dependências.

## ⚙️ Como Rodar o Projeto

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:

  * **JDK 21**
  * **Maven**
  * **MySQL Server** rodando

### 1\. Configuração do Banco de Dados

Crie um banco de dados MySQL e, em seguida, atualize o arquivo `src/main/resources/application.properties` com suas credenciais:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ads_db?createDatabaseIfNotExist=true
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

O Spring Data JPA cuidará de criar as tabelas automaticamente para você.

### 2\. Configuração de E-mail (Opcional)

Se você precisa que a funcionalidade de aprovação de administradores por e-mail funcione, configure uma conta do Gmail no mesmo arquivo:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app_do_google
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Dica:** Por segurança, utilize uma senha de aplicativo do Google em vez da sua senha principal.

### 3\. Iniciar a Aplicação

Abra o terminal na pasta raiz do projeto e execute o comando:

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

-----

## 🗺️ Rotas da API

As rotas são protegidas com Spring Security e organizadas da seguinte forma:

### **`AuthController` - Autenticação e Admins (`/api/auth`)**

| Método | Rota | Descrição | Permissões |
|:---:|:---|:---|:---|
| `POST` | `/login` | Autentica um admin. | Pública |
| `POST` | `/request-access` | Solicita acesso de admin. | Pública |
| `POST` | `/register-admin` | Finaliza o cadastro. | Pública |
| `GET` | `/requests` | Lista solicitações pendentes. | MASTER\_ADMIN |
| `POST` | `/requests/approve/{id}` | Aprova uma solicitação. | MASTER\_ADMIN |
| `DELETE` | `/delete-account` | Exclui a conta logada. | Autenticado |

### **`NoticiaController` - Conteúdo Dinâmico (`/api`)**

| Método | Rota | Descrição | Permissões |
|:---:|:---|:---|:---|
| `GET` | `/noticias` | Retorna todas as notícias. | Pública |
| `POST` | `/noticias` | Cria uma nova notícia. | ADMIN, MASTER\_ADMIN |
| `PUT` | `/noticias/{id}` | Atualiza uma notícia. | ADMIN, MASTER\_ADMIN |
| `DELETE` | `/noticias/{id}` | Exclui uma notícia. | ADMIN, MASTER\_ADMIN |
| `POST` | `/upload` | Faz upload de imagem. | ADMIN, MASTER\_ADMIN |
| `GET` | `/curriculo` | Retorna a grade curricular. | Pública |
| `POST` | `/curriculo` | Adiciona um novo período. | MASTER\_ADMIN |
| `PUT` | `/curriculo/{id}` | Atualiza um período. | MASTER\_ADMIN |

-----
