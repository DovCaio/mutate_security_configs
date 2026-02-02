package com.caio.analize;

import com.caio.exceptions.NoOneAnnotationMutableFinded;
import com.caio.models.AnnotationMutationPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CodeAnalyzerTest {

    @TempDir
    Path tempDir;

    private Path createJavaFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file;
    }


    @Test
    void shouldDetectControllerWithPreAuthorize() throws Exception {
        String code = """
            package com.test;

            import org.springframework.web.bind.annotation.RestController;
            import org.springframework.security.access.prepost.PreAuthorize;

            @RestController
            public class UserController {

                @PreAuthorize("hasRole('ADMIN')")
                public void test() {}
            }
            """;

        Path file = createJavaFile("UserController.java", code);

        CodeAnalyzer analyzer = new CodeAnalyzer();
        analyzer.analyze(List.of(file));

        assertEquals(1, analyzer.getControllers().size());
        assertEquals(1, analyzer.getMutationsPoints().size());
    }


    @Test
    void shouldRemoveControllerWithoutSecurityAnnotations() throws Exception {
        String code = """
            @RestController
            public class NoSecurityController {
                public void test() {}
            }
            """;

        Path file = createJavaFile("NoSecurityController.java", code);

        CodeAnalyzer analyzer = new CodeAnalyzer();

        assertThrows(NoOneAnnotationMutableFinded.class,
                () -> analyzer.analyze(List.of(file)));
    }


    @Test
    void shouldDetectClassLevelAnnotation() throws Exception {
        String code = """
            package com.test;

            @RestController
            @PreAuthorize("hasRole('USER')")
            public class ClassSecuredController {

                public void test() {}
            }
            """;

        Path file = createJavaFile("ClassSecuredController.java", code);

        CodeAnalyzer analyzer = new CodeAnalyzer();
        analyzer.analyze(List.of(file));

        AnnotationMutationPoint mp = analyzer.getMutationsPoints().get(0);

        assertEquals("", mp.getMethodName());
        assertEquals(AnnotationMutationPoint.TargetType.CLASS, mp.getTargetType());
    }


    @Test
    void shouldDetectPostAuthorize() throws Exception {
        String code = """
            @Controller
            public class PostController {

                @PostAuthorize("hasAuthority('READ')")
                public void run() {}
            }
            """;

        Path file = createJavaFile("PostController.java", code);

        CodeAnalyzer analyzer = new CodeAnalyzer();
        analyzer.analyze(List.of(file));

        assertEquals(1, analyzer.getMutationsPoints().size());
    }


    @Test
    void shouldDetectMultipleAnnotations() throws Exception {
        String code = """
            @RestController
            public class MultiController {

                @PreAuthorize("hasRole('ADMIN')")
                public void a(){}

                @PreAuthorize("hasRole('USER')")
                public void b(){}
            }
            """;

        Path file = createJavaFile("MultiController.java", code);

        CodeAnalyzer analyzer = new CodeAnalyzer();
        analyzer.analyze(List.of(file));

        assertEquals(2, analyzer.getMutationsPoints().size());
    }


    @Test
    void shouldExtractRolesCorrectly() throws Exception {
        String code = """
            @RestController
            public class RoleController {

                @PreAuthorize("hasAnyRole('ADMIN','USER','GUEST')")
                public void run(){}
            }
            """;

        Path file = createJavaFile("RoleController.java", code);

        CodeAnalyzer analyzer = new CodeAnalyzer();
        analyzer.analyze(List.of(file));

        List<String> roles = analyzer.getRoles();

        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("USER"));
        assertTrue(roles.contains("GUEST"));
    }


    @Test
    void shouldExtractAuthoritiesCorrectly() throws Exception {
        String code = """
            @RestController
            public class AuthController {

                @PreAuthorize("hasAnyAuthority('READ','WRITE','DELETE')")
                public void run(){}
            }
            """;

        Path file = createJavaFile("AuthController.java", code);

        CodeAnalyzer analyzer = new CodeAnalyzer();
        analyzer.analyze(List.of(file));

        List<String> auth = analyzer.getAuthorities();

        assertEquals(3, auth.size());
        assertTrue(auth.contains("READ"));
    }


    @Test
    void shouldExtractPackageNameViaReflection() throws Exception {
        CodeAnalyzer analyzer = new CodeAnalyzer();

        Method m = CodeAnalyzer.class.getDeclaredMethod("extractPackageName", String.class);
        m.setAccessible(true);

        String result = (String) m.invoke(analyzer,
                "package com.example.test;\n public class A {}");

        assertEquals("com.example.test", result);
    }


    @Test
    void shouldExtractMethodNameViaReflection() throws Exception {
        String code = """
            public class X {

                @PreAuthorize("hasRole('ADMIN')")
                public List<String> runTest() { return null; }
            }
            """;

        CodeAnalyzer analyzer = new CodeAnalyzer();

        Method m = CodeAnalyzer.class
                .getDeclaredMethod("extractMethod", String.class, Integer.class);

        m.setAccessible(true);

        String method = (String) m.invoke(analyzer, code, 3);

        assertEquals("runTest", method);
    }


    @Test
    void shouldThrowWhenAnalyzeNull() {
        CodeAnalyzer analyzer = new CodeAnalyzer();

        assertThrows(IllegalArgumentException.class,
                () -> analyzer.analyze(null));
    }


    @Test
    void shouldAnalyzeMultipleFiles() throws Exception {

        Path a = createJavaFile("A.java", """
            @RestController
            public class A {
                @PreAuthorize("hasRole('ADMIN')")
                public void x(){}
            }
            """);

        Path b = createJavaFile("B.java", """
            @RestController
            public class B {
                @PreAuthorize("hasRole('USER')")
                public void y(){}
            }
            """);

        CodeAnalyzer analyzer = new CodeAnalyzer();
        analyzer.analyze(List.of(a, b));

        assertEquals(2, analyzer.getMutationsPoints().size());
    }

}
