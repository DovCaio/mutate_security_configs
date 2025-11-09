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

## 🔹 Com Maven

O comando a seguir:

```bash
mvn compile && mvn test-compile && mvn dependency:copy-dependencies
```

Executa as seguintes etapas:

1. **Compila o código-fonte principal**
   - Diretório: `src/main/java`
2. **Compila o código de teste**
   - Diretório: `src/test/java`
3. **Copia as dependências do projeto**
   - Diretório de saída: `target/dependency`

---

## 🔸 Com Gradle

O Gradle não possui uma task equivalente a `dependency:copy-dependencies` por padrão.  
É necessário criar uma **task personalizada**.

### 1. Usando Groovy DSL (`build.gradle`)

```groovy
tasks.register('copyDependencies', Copy) {
    from configurations.runtimeClasspath
    from configurations.testRuntimeClasspath
    into "$buildDir/dependencies"
}
```

### 2. Usando Kotlin DSL (`build.gradle.kts`)

```kotlin
tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    from(configurations.testRuntimeClasspath)
    into("$buildDir/dependencies")
}
```

---

## 💻 Executando

Após adicionar a task acima, você pode executar o equivalente ao comando Maven:

```bash
./gradlew compileJava compileTestJava copyDependencies
```


Esses comandos garantem que todas as classes e dependências necessárias estarão disponíveis para o carregamento dinâmico e execução dos testes.

---

## ✅ **Resumo**

| Etapa | Maven | Gradle | Descrição |
|-------|--------|---------|-----------|
| 🧩 Build do projeto (Projeto mutate_security_configs) | `mvn clean package` |  | Gera o `.jar` principal do projeto |
| 🧪 Compilar código e testes (Projeto alvo) | `mvn compile && mvn test-compile` | `./gradlew compileJava compileTestJava` | Prepara as classes principais e de teste |
| 📦 Copiar dependências (Projeto alvo) | `mvn dependency:copy-dependencies` | `./gradlew copyDependencies` *(task personalizada)* | Copia todas as libs para `target/dependency` (Maven) ou `build/dependencies` (Gradle) |
| 🚀 Executar (Projeto mutate_security_configs) | `java -jar target/mutate_security_configs-1.0-SNAPSHOT.jar /seu/projeto` |  | Inicia a execução do `.jar` com os testes em memória |
