Backend da Aplicação ADS
Este é o backend da aplicação ADS, construído com Spring Boot. Ele fornece uma API RESTful para o frontend, gerenciando as principais funcionalidades do sistema: autenticação, gerenciamento de administradores, notícias e grade curricular.

Visão Geral das Funcionalidades
Sistema de Autenticação e Autorização: Utiliza JSON Web Tokens (JWT) para autenticar usuários com diferentes níveis de acesso (ADMIN, MASTER_ADMIN).

Gerenciamento de Admins: Permite solicitar acesso, aprovar/rejeitar solicitações e visualizar a lista de todos os administradores cadastrados.

Gerenciamento de Conteúdo:

Notícias: Endpoints para criar, ler, atualizar e excluir notícias.

Grade Curricular: Endpoints para gerenciar períodos e disciplinas.

Upload de Imagens: Suporte para upload de imagens, que são salvas no servidor e vinculadas a notícias.

E-mails: Envio de notificações automáticas por e-mail para aprovação de contas.

Tecnologias Utilizadas
Linguagem: Java 21

Framework: Spring Boot 3.x

Persistência: Spring Data JPA

Banco de Dados: MySQL

Segurança: Spring Security (com JWT)

E-mail: JavaMailSender

Build Tool: Maven

Como Rodar a Aplicação
Siga estes passos para configurar e executar o projeto em seu ambiente local:

1. Configurar o Banco de Dados
Crie um banco de dados MySQL e configure as credenciais no arquivo src/main/resources/application.properties:

Properties

spring.datasource.url=jdbc:mysql://localhost:3306/ads_db?createDatabaseIfNotExist=true
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
A propriedade spring.jpa.hibernate.ddl-auto=update fará com que as tabelas sejam criadas e atualizadas automaticamente ao iniciar a aplicação.

2. Configurar o Envio de E-mail
O sistema de aprovação de contas envia e-mails. Você deve configurar um e-mail do Gmail e uma senha de aplicativo no application.properties:

Properties

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app_do_google
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
3. Executar o Backend
Abra um terminal na raiz do projeto.

Execute o comando Maven para iniciar a aplicação:

Bash

./mvnw spring-boot:run
A API estará disponível em http://localhost:8080.

Endpoints da API
AuthController (/api/auth)
Método	Endpoint	Descrição	Permissões
POST	/login	Autentica um admin e retorna um token JWT.	Nenhuma
POST	/request-access	Envia uma solicitação de acesso para o Master Admin.	Nenhuma
POST	/register-admin	Conclui o cadastro usando a chave de acesso.	Nenhuma
GET	/requests	Lista solicitações de acesso pendentes.	MASTER_ADMIN
GET	/admins	Lista todos os administradores cadastrados.	MASTER_ADMIN
POST	/requests/approve/{id}	Aprova uma solicitação e envia a chave por e-mail.	MASTER_ADMIN
DELETE	/requests/reject/{id}	Rejeita uma solicitação de acesso.	MASTER_ADMIN
PUT	/change-password	Altera a senha do usuário autenticado.	Autenticado
DELETE	/delete-account	Exclui a conta do usuário autenticado.	Autenticado

Exportar para Sheets
NoticiaController (/api)
Método	Endpoint	Descrição	Permissões
GET	/noticias	Lista todas as notícias.	Nenhuma
GET	/noticias/{id}	Retorna uma notícia por ID.	Nenhuma
POST	/noticias	Cria uma nova notícia.	ADMIN, MASTER_ADMIN
PUT	/noticias/{id}	Atualiza uma notícia existente.	ADMIN, MASTER_ADMIN
DELETE	/noticias/{id}	Exclui uma notícia.	ADMIN, MASTER_ADMIN
POST	/upload	Faz upload de uma imagem e retorna a URL.	ADMIN, MASTER_ADMIN
GET	/curriculo	Lista todos os períodos da grade curricular.	Nenhuma
POST	/curriculo	Adiciona um novo período à grade.	MASTER_ADMIN
PUT	/curriculo/{id}	Atualiza um período da grade.	MASTER_ADMIN
DELETE	/curriculo/{id}	Exclui um período da grade.	MASTER_ADMIN
