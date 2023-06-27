package com.innoq.comptus.core;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import java.util.*;

public abstract class Component {

    private final Set<String> handledAttributeNames = new HashSet<>();
    private final ComponentContext context;

    public Component(ComponentContext context) {
        this.context = context;
    }


    public <T> Optional<T> outerVariable(String name, Class<T> clazz) {
        return Optional.ofNullable(context.context.getVariable(name))
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    public void setInnerVariable(String name, Object value) {
        context.structureHandler.setLocalVariable(name, value);
    }

    public <T> Optional<T> attribute(String name, Class<T> clazz) {
        handledAttributeNames.add(name);
        return Optional.ofNullable(context.attributes.get(name))
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    public Optional<String> stringAttribute(String name) {
        handledAttributeNames.add(name);
        return Optional.ofNullable(context.attributes.get(name))
                .map(Object::toString);
    }

    public OptionalInt intAttribute(String name) {
        handledAttributeNames.add(name);
        return Optional.ofNullable(context.attributes.get(name))
                .map(value -> {
                    if (value instanceof Integer i) {
                        return i;
                    } else {
                        return Integer.parseInt(value.toString());
                    }
                })
                .map(OptionalInt::of)
                .orElse(OptionalInt.empty());
    }

    public Map<String, Object> getRestAttributes() {
        final var restAttributes = new HashMap<String, Object>();

        context.attributes.entrySet().stream()
                .filter(entry -> !handledAttributeNames.contains(entry.getKey()))
                .forEach(entry -> restAttributes.put(entry.getKey(), entry.getValue()));

        return restAttributes;
    }

    public boolean hasAttribute(String name) {
        handledAttributeNames.add(name);
        return context.attributes.containsKey(name);
    }

    public boolean hasSlot(String slotName) {
        return this.context.hasSlot(slotName);
    }

    public record ComponentContext(
            Map<String, ?> attributes,
            Set<String> slotNames,
            ITemplateContext context,
            IElementModelStructureHandler structureHandler) {

        public boolean hasSlot(String slotName) {
            return this.slotNames.contains(slotName);
        }
    }
}
