Documentação do Projeto Backend - appReceitas
1. Descrição Geral do Projeto
O projeto appReceitas consiste em uma aplicação backend desenvolvida em Java com o framework Spring Boot,
com integração ao Firebase para autenticação e armazenamento de dados e arquivos. O sistema tem como objetivo
fornecer suporte a um aplicativo de culinária, permitindo a gestão de usuários (clientes) e receitas.
O backend disponibiliza uma API RESTful que permite operações de criação, leitura, atualização e exclusão de dados,
 com autenticação baseada em tokens do Firebase. A base de dados utilizada é o Firestore (NoSQL)
e os arquivos são armazenados no Firebase Storage.
3. Funcionalidades do Sistema
2.1 Módulo Cliente
• Visualização de Perfil
  o Endpoint: GET /clientes/perfil - Permite que o cliente visualize suas informações pessoais cadastradas.
• Atualização de Perfil
  o Endpoint: PUT /clientes/perfil - Permite editar nome e e-mail do cliente.
• Exclusão de Perfil
  o Endpoint: DELETE /clientes/perfil - Exclui a conta do cliente do Firestore e do Firebase Authentication.
• Listagem de Receitas Favoritas
  o Endpoint: GET /clientes/favoritos - Lista as receitas favoritas do cliente.
• Favoritar Receita
  o Endpoint: POST /clientes/favoritar?receitaId={id} - Adiciona uma receita à lista de favoritos.
• Desfavoritar Receita
  o Endpoint: POST /clientes/desfavoritar?receitaId={id} - Remove uma receita dos favoritos.
• Redefinição de Senha
  o Endpoint: POST /clientes/esqueci-senha?email={email} - Gera um link de redefinição de senha.
• Upload de Foto de Perfil
  o Endpoint: POST /clientes/upload-foto - Envia imagem de perfil ao Firebase Storage.
2.2 Módulo Receita
• Criação de Receita
  o Endpoint: POST /receitas - Cria uma nova receita.
• Listagem Geral de Receitas
  o Endpoint: GET /receitas - Lista todas as receitas cadastradas.
• Consulta de Receita por ID
  o Endpoint: GET /receitas/{id} - Retorna os detalhes de uma receita específica.
• Atualização de Receita
  o Endpoint: PUT /receitas/{id} - Permite editar os dados da receita.
• Exclusão de Receita
  o Endpoint: DELETE /receitas/{id} - Remove a receita do sistema.
• Listagem de Receitas Públicas
  o Endpoint: GET /receitas/publicas - Retorna as receitas públicas.
4. Arquitetura e Tecnologias Utilizadas
•	Linguagem: Java 17
•	Framework: Spring Boot 3.5
•	Gerenciador de dependências: Maven
•	Banco de Dados: Firebase Firestore (NoSQL)
•	Armazenamento de Arquivos: Firebase Storage
•	Autenticação: Firebase Authentication
•	Arquitetura: MVC (Model-View-Controller)
•	Outras bibliotecas: Apache Commons IO, Spring Web, Spring Security
5. Instruções de Instalação e Execução
1. Clonar o repositório:
   git clone https://github.com/seu-usuario/appReceitas.git
2. Importar o projeto em uma IDE (IntelliJ, Eclipse ou VS Code)
3. Configurar credenciais do Firebase:
   o Criar projeto no Firebase Console
   o Habilitar Firestore, Authentication e Storage
   o Inserir serviceAccountKey.json na pasta resources
4. Executar o projeto:
   ./mvnw spring-boot:run
5. Acessar a aplicação: http://localhost:8080
5. Considerações Finais
Este backend oferece uma base sólida para aplicações de culinária com foco em experiência do usuário, segurança,
integração em tempo real e escalabilidade via serviços da Google Cloud Platform.
A arquitetura em camadas permite manutenção facilitada e expansão futura com módulos adicionais.
Documentação do Front-end do Aplicativo "Diário de Sabores"
1. Descrição do Projeto e Funcionalidades
O "Diário de Sabores" é um aplicativo móvel desenvolvido para o registro, organização e visualização de receitas culinárias.
O projeto foi concebido com o objetivo de proporcionar uma experiência de usuário intuitiva, moderna e funcional.
A aplicação permite que os usuários armazenem receitas pessoais (públicas ou privadas), busquem receitas de outros usuários,
e gerenciem suas criações culinárias com facilidade.
Funcionalidades principais:
•	Cadastro e login de usuários com autenticação segura.
•	Visualização de receitas públicas em uma tela inicial personalizada ("Para você").
•	Busca por receitas por nome ou ingrediente.
•	Cadastro de novas receitas com possibilidade de torná-las privadas.
•	Edição e exclusão de receitas criadas pelo usuário.
•	Favoritação de receitas para acesso rápido.
•	Interface intuitiva com navegação inferior entre as seções: Home, Buscar, Adicionar Receita, Favoritos e Perfil.
3. Instruções para Configurar e Executar o Projeto
Requisitos:
•	Android Studio (recomendado: versão mais recente)
•	SDK Android 30 ou superior
•	Gradle configurado (automático ao abrir o projeto)
Passos para execução:
1. Clonar o repositório do GitHub.
2. Abrir o Android Studio e selecionar a pasta do projeto.
3. Aguardar a sincronização do Gradle e instalar dependências.
4. Configurar um emulador com API 30+ ou conectar um dispositivo físico.
5. Executar o projeto utilizando a Activity principal: LoginActivity.kt.
Observação: é necessário acesso à internet para autenticação e sincronização com o backend (ou Firebase, se utilizado).
3. Arquitetura e Tecnologias Utilizadas
Padrão de Arquitetura: MVVM (Model-View-ViewModel)
Justificativa:
O padrão MVVM foi adotado por sua capacidade de separar de forma clara a interface do usuário da lógica de negócio e das manipulações de dados.
Ele proporciona uma estrutura organizada, facilita a manutenção e permite uma maior cobertura de testes unitários.
Camadas:
* Model: Representação das entidades do sistema, como Recipe.kt.
* View: Activities e Fragments que exibem a interface ao usuário.
* ViewModel: Responsável pela lógica e comunicação com os dados, utilizando LiveData.
Tecnologias Utilizadas:
•	Linguagem: Kotlin
•	IDE: Android Studio
•	Componentes: RecyclerView, CardView, BottomNavigationView, ConstraintLayout, EditText, ImageView, Button
•	Persistência de Dados: Room (SQLite Jetpack)
•	Gerenciamento de dependências: Gradle
•	Controle de versão: Git + GitHub
•	Testes: JUnit e Mockito para cobertura de testes unitários acima de 85%
