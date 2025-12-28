<!-- ========================================
     CONFIGURAÇÃO PARA CONSUMIR A BIBLIOTECA
     ======================================== -->

<!-- 1. Adicionar repositório GitHub Packages -->
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/MarceloMacedo02/conexaoauthlib</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>

<!-- 2. Configurar servidor para autenticação -->
<servers>
    <server>
        <id>github</id>
        <username>${env.GITHUB_USERNAME}</username>
        <password>${env.GITHUB_TOKEN}</password>
    </server>
</servers>

<!-- 3. Adicionar dependência da biblioteca -->
<dependencies>
    <dependency>
        <groupId>com.plataforma.conexao</groupId>
        <artifactId>conexao-auth-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

<!-- ========================================
     VARIÁVEIS DE AMBIENTE NECESSÁRIAS
     ======================================== 

Configure as seguintes variáveis de ambiente ou settings.xml:

GITHUB_USERNAME: Seu usuário do GitHub
GITHUB_TOKEN: Personal Access Token com权限 'read:packages'

Exemplo settings.xml:
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>seu-usuario</username>
      <password>ghp_xxxxxxxxxxxxxxxxxxxx</password>
    </server>
  </servers>
</settings>

Para gerar um token:
1. Vá para GitHub > Settings > Developer settings > Personal access tokens
2. Crie um novo token com scopes: 'read:packages'
3. Configure-o em seu ambiente ou settings.xml
-->