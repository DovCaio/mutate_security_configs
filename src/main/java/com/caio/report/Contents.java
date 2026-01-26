package com.caio.report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.caio.engine.ParamsForTestMutationApresentation;
import com.caio.engine.RunTest;
import com.caio.engine.RunTest.TestResult;

public class Contents {

    public static String css() {
        return """
                body {
    font-family: Arial, sans-serif;
    margin: 20px;
    background-color: #f4f4f4;
}

header {
    text-align: center;
    margin-bottom: 20px;
}

section.summary ul {
    list-style: none;
    padding: 0;
}

section.summary li {
    margin: 4px 0;
}

table {
    width: 100%;
    border-collapse: collapse;
}

table, th, td {
    border: 1px solid #ccc;
}

th, td {
    padding: 8px;
    text-align: center;
}

.captured {
    background-color: #d4f8d4; /* verde claro */
}

.survived {
    background-color: #f8d4d4; /* vermelho claro */
}
                    """;
    }

    public static String html(List<RunTest.TestResult> testResults) {

        int totalMutants = testResults.size();
        long capturedCount = testResults.stream().filter(RunTest.TestResult::whasCaptured).count();
        long survivedCount = totalMutants - capturedCount;
        double captureRate = totalMutants > 0 ? (capturedCount * 100.0 / totalMutants) : 0.0;


        String date = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        StringBuilder html = new StringBuilder("""
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
            <meta charset="UTF-8">
            <title>Relatório de Mutação</title>
            <link rel="stylesheet" href="../assets/css/style.css">
        </head>
        <body>
            <header>
                <h1>Relatório de Mutação</h1>
                <p>Gerado em: %s</p>
            </header>

            <section class="summary">
                <h2>Resumo Geral</h2>
                <ul>
                    <li><strong>Total de Mutantes:</strong> %d</li>
                    <li><strong>Capturados:</strong> %d</li>
                    <li><strong>Sobreviveram:</strong> %d</li>
                    <li><strong>Taxa de Captura:</strong> %.2f%%</li>
                </ul>
            </section>

            <section>
                <h2>Detalhes por Mutante</h2>
                <table>
                    <tr>
                        <th>#</th>
                        <th>Package Name</th>
                        <th>Class Name</th>
                        <th>Método</th>
                        <th>Valor original</th>
                        <th>Mutação</th>
                        <th>Total</th>
                        <th>Sucesso</th>
                        <th>Falhas</th>
                        <th>Erros</th>
                        <th>Capturado</th>
                    </tr>
        """.formatted(date, totalMutants, capturedCount, survivedCount, captureRate));

    int index = 1;
    for (RunTest.TestResult result : testResults) {
        boolean captured = result.whasCaptured();
        String rowClass = captured ? "captured" : "survived";
        String capturedText = captured ? "Sim" : "Não";

        ParamsForTestMutationApresentation params = result.getParamsForTestMutationApresentation();

        html.append(String.format("""
            <tr class="%s">
                <td>%d</td>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
                <td>%d</td>
                <td>%d</td>
                <td>%d</td>
                <td>%s</td>
                <td>%s</td>
            </tr>
        """, rowClass, index++, params.packageName, params.className, params.method,
                params.originalValue, params.mutatedValue,result.getTotalTest(), result.getSuccedded(),
                result.getFailed(), result.getFailures(), capturedText));
    }

    html.append("""
                </table>
            </section>
        </body>
        </html>
    """);


        return html.toString();
    }

}
