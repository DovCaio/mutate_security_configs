# 🧬 mutate_security_configs

Ferramenta mutar configurações de segunrança do spring boot, permitindo analisar e mutar configurações de segurança de projetos Java.

---

## ⚙️ **Como buildar o projeto**

```bash
mvn clean package
```

---

## 🚀 **Como executar**

Após gerar o `.jar`, execute passando o caminho do diretório do projeto alvo:

```bash
java -jar target/mutate_security_configs-1.0-SNAPSHOT.jar /meu/caminho/fake
```

> 📝 **Importante:** Substitua `/meu/caminho/fake` pelo caminho real do projeto que você deseja testar ou analisar.

---

## 🧱 **Preparando o projeto alvo**

Antes de rodar o `mutate_security_configs`, certifique-se de **compilar o projeto alvo**.  
Se estiver usando Maven, execute os comandos abaixo:

```bash
# Compila o código principal
mvn compile

# Compila as classes de teste
mvn test-compile

# Copia as dependências para o diretório target/dependency
mvn dependency:copy-dependencies
```

Esses comandos garantem que todas as classes e dependências necessárias estarão disponíveis para o carregamento dinâmico e execução dos testes.

---

## ✅ **Resumo**

| Etapa | Comando | Descrição |
|-------|----------|-----------|
| 🧩 Build do projeto | `mvn clean package` | Gera o `.jar` principal |
| 🧪 Compilar código e testes | `mvn compile && mvn test-compile` | Prepara as classes |
| 📦 Copiar dependências | `mvn dependency:copy-dependencies` | Coloca todas as libs em `target/dependency` |
| 🚀 Executar | `java -jar target/mutate_security_configs-1.0-SNAPSHOT.jar /seu/projeto` | Inicia a execução dos testes em memória |
