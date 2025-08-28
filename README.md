<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Documentação do Backend ADS</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            background-color: #f4f7f9;
            color: #333;
        }
        .container {
            max-width: 900px;
            margin: auto;
            background: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        h1, h2 {
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
            margin-top: 20px;
        }
        h3 {
            color: #34495e;
            margin-top: 25px;
        }
        p, ul, ol {
            margin-bottom: 15px;
        }
        ul, ol {
            padding-left: 20px;
        }
        code {
            background-color: #e9ecef;
            padding: 2px 5px;
            border-radius: 4px;
            font-family: 'Courier New', Courier, monospace;
        }
        pre {
            background-color: #2d2d2d;
            color: #f8f8f2;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
            position: relative;
        }
        .copy-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            background-color: #3498db;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
        }
        .copy-btn:hover {
            background-color: #2980b9;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        th {
            background-color: #3498db;
            color: white;
            font-weight: bold;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        .permission {
            background-color: #e8f5e9;
            padding: 3px 8px;
            border-radius: 4px;
            font-weight: bold;
            color: #2e7d32;
        }
        .permission-public {
            background-color: #e3f2fd;
            color: #1565c0;
        }
        .permission-admin {
            background-color: #ffe0b2;
            color: #e65100;
        }
        .permission-master {
            background-color: #fce4ec;
            color: #ad1457;
        }
        .permission-auth {
            background-color: #ede7f6;
            color: #4527a0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Documentação do Backend ADS</h1>
        <p>Este projeto é o backend da aplicação ADS, uma API RESTful construída com <strong>Spring Boot</strong>. Ele gerencia a lógica de negócio do site, incluindo autenticação, administração, notícias e a grade curricular.</p>

        <hr>

        <h2>Tecnologias Usadas</h2>
        <ul>
            <li><strong>Java 21:</strong> A linguagem principal da API.</li>
            <li><strong>Spring Boot 3.x:</strong> Nosso framework para desenvolvimento rápido e robusto.</li>
            <li><strong>Spring Data JPA:</strong> Para uma comunicação eficiente e direta com o banco de dados.</li>
            <li><strong>MySQL:</strong> O banco de dados relacional onde todas as informações são armazenadas.</li>
            <li><strong>Spring Security & JWT:</strong> Para um sistema de autenticação e autorização seguro.</li>
            <li><strong>JavaMailSender:</strong> Biblioteca utilizada para o envio de e-mails de notificação.</li>
            <li><strong>Maven:</strong> Nosso gerenciador de dependências e ferramenta de build.</li>
        </ul>

        <hr>

        <h2>Como Colocar o Projeto para Rodar</h2>
        <h3>Pré-requisitos</h3>
        <ul>
            <li>Ter o JDK 21 instalado.</li>
            <li>Ter o Maven instalado.</li>
            <li>Ter o MySQL Server rodando.</li>
        </ul>

        <h3>1. Configuração do Banco de Dados</h3>
        <p>Primeiro, crie um banco de dados no MySQL. Depois, abra o arquivo <code>src/main/resources/application.properties</code> e atualize as configurações com suas credenciais:</p>
        <pre><code id="db-code">spring.datasource.url=jdbc:mysql://localhost:3306/ads_db?createDatabaseIfNotExist=true
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect</code></pre>
        <p>A linha <code>spring.jpa.hibernate.ddl-auto=update</code> faz com que o Spring crie todas as tabelas necessárias automaticamente.</p>

        <h3>2. Configuração do E-mail</h3>
        <p>O sistema envia e-mails de aprovação. Para que isso funcione, configure sua conta do Gmail e uma senha de aplicativo do Google no mesmo arquivo <code>application.properties</code>:</p>
        <pre><code id="email-code">spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app_do_google
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true</code></pre>
        <p><strong>Dica:</strong> Por segurança, use uma senha de aplicativo do Google em vez da sua senha principal.</p>

        <h3>3. Subindo o Servidor</h3>
        <p>Abra o terminal na pasta raiz do projeto e use o Maven para iniciar a aplicação:</p>
        <pre><code id="run-code">./mvnw spring-boot:run</code></pre>
        <p>Pronto! A API estará disponível para acesso em <code>http://localhost:8080</code>.</p>

        <hr>

        <h2>Rotas da API</h2>
        <p>A API é organizada em rotas principais. As permissões são validadas pelo Spring Security e aplicadas a cada endpoint.</p>

        <h3><code>AuthController</code> - Autenticação e Admins (<code>/api/auth</code>)</h3>
        <table>
            <thead>
                <tr>
                    <th>Método</th>
                    <th>Rota</th>
                    <th>Descrição</th>
                    <th>Permissões</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><code>POST</code></td>
                    <td><code>/login</code></td>
                    <td>Autentica um admin e retorna o JWT.</td>
                    <td><span class="permission permission-public">Pública</span></td>
                </tr>
                <tr>
                    <td><code>POST</code></td>
                    <td><code>/request-access</code></td>
                    <td>Envia uma solicitação de acesso para o MASTER_ADMIN.</td>
                    <td><span class="permission permission-public">Pública</span></td>
                </tr>
                <tr>
                    <td><code>POST</code></td>
                    <td><code>/register-admin</code></td>
                    <td>Finaliza o cadastro usando uma chave de acesso.</td>
                    <td><span class="permission permission-public">Pública</span></td>
                </tr>
                <tr>
                    <td><code>GET</code></td>
                    <td><code>/requests</code></td>
                    <td>Lista todas as solicitações de acesso pendentes.</td>
                    <td><span class="permission permission-master">MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>GET</code></td>
                    <td><code>/admins</code></td>
                    <td>Lista todos os administradores cadastrados.</td>
                    <td><span class="permission permission-master">MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>POST</code></td>
                    <td><code>/requests/approve/{id}</code></td>
                    <td>Aprova uma solicitação e envia a chave por e-mail.</td>
                    <td><span class="permission permission-master">MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>DELETE</code></td>
                    <td><code>/requests/reject/{id}</code></td>
                    <td>Rejeita uma solicitação de acesso.</td>
                    <td><span class="permission permission-master">MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>PUT</code></td>
                    <td><code>/change-password</code></td>
                    <td>Permite a alteração de senha.</td>
                    <td><span class="permission permission-auth">Autenticado</span></td>
                </tr>
                <tr>
                    <td><code>DELETE</code></td>
                    <td><code>/delete-account</code></td>
                    <td>Exclui a conta do usuário logado.</td>
                    <td><span class="permission permission-auth">Autenticado</span></td>
                </tr>
            </tbody>
        </table>

        <h3><code>NoticiaController</code> - Conteúdo Dinâmico (<code>/api</code>)</h3>
        <table>
            <thead>
                <tr>
                    <th>Método</th>
                    <th>Rota</th>
                    <th>Descrição</th>
                    <th>Permissões</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><code>GET</code></td>
                    <td><code>/noticias</code></td>
                    <td>Retorna todas as notícias em ordem de data.</td>
                    <td><span class="permission permission-public">Pública</span></td>
                </tr>
                <tr>
                    <td><code>GET</code></td>
                    <td><code>/noticias/{id}</code></td>
                    <td>Retorna uma notícia específica pelo ID.</td>
                    <td><span class="permission permission-public">Pública</span></td>
                </tr>
                <tr>
                    <td><code>POST</code></td>
                    <td><code>/noticias</code></td>
                    <td>Cria uma nova notícia.</td>
                    <td><span class="permission permission-admin">ADMIN, MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>PUT</code></td>
                    <td><code>/noticias/{id}</code></td>
                    <td>Atualiza uma notícia existente.</td>
                    <td><span class="permission permission-admin">ADMIN, MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>DELETE</code></td>
                    <td><code>/noticias/{id}</code></td>
                    <td>Exclui uma notícia.</td>
                    <td><span class="permission permission-admin">ADMIN, MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>POST</code></td>
                    <td><code>/upload</code></td>
                    <td>Faz upload de uma imagem e retorna a URL.</td>
                    <td><span class="permission permission-admin">ADMIN, MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>GET</code></td>
                    <td><code>/curriculo</code></td>
                    <td>Retorna todos os períodos da grade curricular.</td>
                    <td><span class="permission permission-public">Pública</span></td>
                </tr>
                <tr>
                    <td><code>POST</code></td>
                    <td><code>/curriculo</code></td>
                    <td>Adiciona um novo período e suas disciplinas.</td>
                    <td><span class="permission permission-master">MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>PUT</code></td>
                    <td><code>/curriculo/{id}</code></td>
                    <td>Atualiza um período da grade curricular.</td>
                    <td><span class="permission permission-master">MASTER_ADMIN</span></td>
                </tr>
                <tr>
                    <td><code>DELETE</code></td>
                    <td><code>/curriculo/{id}</code></td>
                    <td>Exclui um período da grade.</td>
                    <td><span class="permission permission-master">MASTER_ADMIN</span></td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>
