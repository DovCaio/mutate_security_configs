# 🧬 AutoSecMut 

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
java -jar target/mutate_security_configs-1.0-SNAPSHOT.jar /meu/caminho/repositorio
```

> 📝 **Importante:** Substitua `/meu/caminho/repositorio` pelo caminho real do projeto que você deseja testar ou analisar.

## 🔍 Modo Verbose

Você pode usar a flag `-v` para habilitar saída detalhada (verbose) durante a execução da ferramenta, permitindo acompanhar cada etapa do processamento:

```bash
java -jar target/mutate_security_configs-1.0-SNAPSHOT.jar /meu/caminho/repositorio -v
```

Depois da execução um arquivo report vai ser gerado dentro do arquivo do repositorio, as execuções são nomeadas pelo, dia-mes-ano-horas-minutos-segundos

## 📄 Geração de Relatórios

Após a execução, a ferramenta gera automaticamente um arquivo de relatório dentro do diretório do repositório analisado.

Cada relatório recebe um nome baseado em timestamp para evitar sobrescrita de execuções anteriores:
```bash
dia-mes-ano-horas-minutos-segundos
```
