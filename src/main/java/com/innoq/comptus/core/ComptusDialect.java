package com.innoq.comptus.core;

import org.reflections.Reflections;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.Set;
import java.util.stream.Collectors;

import static org.thymeleaf.standard.StandardDialect.PROCESSOR_PRECEDENCE;

public class ComptusDialect extends AbstractProcessorDialect {

    private static final String DIALECT_NAME = "Comptus Dialect";
    private static final String DIALECT_PREFIX = "co";

    private final String componentPackage;

    public ComptusDialect(String componentPackage) {
        super(DIALECT_NAME, DIALECT_PREFIX, PROCESSOR_PRECEDENCE);
        this.componentPackage = componentPackage;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return findComponentClasses().stream()
                .map(componentClass -> {
                    final var tagName = tagNameFor(componentClass);
                    return new ComptusElementModelProcessor(DIALECT_PREFIX, tagName, componentClass);
                })
                .collect(Collectors.toSet());
    }

    private Set<Class<? extends Component>> findComponentClasses() {
        return new Reflections(componentPackage).getSubTypesOf(Component.class);
    }

    private static String tagNameFor(Class<? extends Component> clazz) {
        final var className = clazz.getSimpleName();
        return className.replaceAll("(?!^)(?=[A-Z][a-z])", "-").toLowerCase();
    }
}
