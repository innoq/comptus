package com.innoq.comptus;

import com.innoq.comptus.core.ComptusDialect;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.thymeleaf.templatemode.TemplateMode.HTML;

class ComptusTests {

    ITemplateEngine engine = createEngine();

    @ParameterizedTest
    @ValueSource(strings = {"basics", "attributes", "slots", "context", "objects"})
    void test(String template) throws Exception {
        var context = new Context();
        context.setVariable("obj", new ContextObject("Michael"));
        context.setVariable("list", List.of(new AbstractMap.SimpleEntry<>("foo", "bar"), new AbstractMap.SimpleEntry<>("boo", "baz")));

        var result = engine.process(template, context);

        assertEquals(normalize(expectationFor(template)), normalize(result));
    }

    private static ITemplateEngine createEngine() {
        final var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");

        final var engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.addDialect(new ComptusDialect("com.innoq.comptus.components"));

        return engine;
    }

    private static String expectationFor(String name) throws IOException {
        final String file = "/expectations/%s.html".formatted(name);
        try (final var in = ComptusTests.class.getResourceAsStream(file)) {
            final var bytes = in.readAllBytes();
            return new String(bytes, UTF_8);
        }
    }

    private static String normalize(String string) {
        return Arrays.stream(string.split("\n"))
                .map(String::strip)
                .filter(line -> !line.isBlank())
                .collect(Collectors.joining("\n"));
    }

    record ContextObject(String name) {
        public boolean hasLongName() {
            return name.length() > 3;
        }
    }
}
