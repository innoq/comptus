package com.innoq.comptus.core;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.model.*;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.thymeleaf.templatemode.TemplateMode.HTML;

public class ComptusElementModelProcessor extends AbstractElementModelProcessor {

    private final String elementName;
    private final Class<? extends Component> componentClass;

    public ComptusElementModelProcessor(String dialectPrefix, String elementName, Class<? extends Component> componentClass) {
        super(HTML, dialectPrefix, elementName, true, null, false, 1);
        this.elementName = elementName;
        this.componentClass = componentClass;
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel componentInstanceModel, IElementModelStructureHandler structureHandler) {
        final var slotValues = extractSlotValues(context, componentInstanceModel);

        final var componentTemplateModel = createComponentTemplateModel(context, slotValues);
        final var component = createComponent(context, componentInstanceModel, slotValues.keySet(), structureHandler);

        addRestAttributes(context, componentTemplateModel, component);
        structureHandler.setLocalVariable("this", component);

        componentInstanceModel.reset();
        componentInstanceModel.addModel(componentTemplateModel);
    }

    private TemplateModel loadTemplateModel(ITemplateContext context) {
        return context.getConfiguration().getTemplateManager()
                .parseStandalone(context, "components/%s".formatted(this.elementName), null, null, false, true);
    }

    private Map<String, List<ITemplateEvent>> extractSlotValues(ITemplateContext context, IModel componentInstanceModel) {
        final var slotValues = new HashMap<String, List<ITemplateEvent>>();

        String slotName = null; // null == default slot
        var level = 0;

        for (var i = 1; i < componentInstanceModel.size() - 1; i++) {
            var childElement = componentInstanceModel.get(i);
            if (childElement instanceof IOpenElementTag) {
                level++;
            } else if (childElement instanceof ICloseElementTag) {
                level--;
            }

            if (level == 1 && childElement instanceof IProcessableElementTag pet && pet.hasAttribute("co:slot")) {
                slotName = pet.getAttributeValue("co:slot");
                childElement = context.getModelFactory().removeAttribute(pet, "co:slot");
            }

            slotValues.computeIfAbsent(slotName, key -> new ArrayList<>()).add(childElement);

            if (level == 0 && childElement instanceof ICloseElementTag) {
                slotName = null;
            }
        }

        return slotValues;
    }

    private IModel createComponentTemplateModel(ITemplateContext context, Map<String, List<ITemplateEvent>> slotValues) {
        final var templateModel = loadTemplateModel(context);

        final var componentTemplateModel = context.getModelFactory().createModel();

        for (var i = 1; i < templateModel.size() - 1; i++) {
            final var childElement = templateModel.get(i);
            if (childElement instanceof IStandaloneElementTag set && "co:slot".equals(set.getElementCompleteName())) {
                final var slotName = set.getAttributeValue("co:name");
                slotValues.getOrDefault(slotName, emptyList()).forEach(componentTemplateModel::add);
            } else {
                componentTemplateModel.add(childElement);
            }
        }

        return componentTemplateModel;
    }

    private Component createComponent(ITemplateContext context,
                                      IModel componentInstanceModel,
                                      Set<String> slotNames,
                                      IElementModelStructureHandler structureHandler) {
        final var attributes = extractAttributesFrom(context, componentInstanceModel);
        final var componentContext = new Component.ComponentContext(attributes, slotNames, context, structureHandler);
        try {
            return componentClass.getConstructor(Component.ComponentContext.class).newInstance(componentContext);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalComponentDefinition(e);
        }
    }

    private Map<String, Object> extractAttributesFrom(ITemplateContext context, IModel componentInstanceModel) {
        final var attributes = new HashMap<String, Object>();
        Arrays.stream(((IProcessableElementTag) componentInstanceModel.get(0)).getAllAttributes())
                .forEach(attribute -> {
                    final var attributeName = attribute.getAttributeDefinition().getAttributeName();
                    final var attributeValue = attribute.getValue();

                    if ("co".equals(attributeName.getPrefix()) && attributeValue != null) {
                        attributes.put(attributeName.getAttributeName(), getExpressionParser(context).parseExpression(context, attributeValue).execute(context));
                    } else {
                        attributes.put(attributeName.getAttributeName(), attributeValue);
                    }
                });
        return attributes;
    }

    private void addRestAttributes(ITemplateContext context, IModel componentTemplateModel, Component component) {
        final var restAttributes = component.getRestAttributes();
        if (restAttributes.isEmpty()) {
            return;
        }

        final var modelFactory = context.getModelFactory();

        var element = (IProcessableElementTag) componentTemplateModel.get(0);
        for (var entry : restAttributes.entrySet()) {
            element = modelFactory.setAttribute(element, entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
        }

        componentTemplateModel.replace(0, element);
    }

    private static IStandardExpressionParser getExpressionParser(ITemplateContext context) {
        return StandardExpressions.getExpressionParser(context.getConfiguration());
    }

    public static class IllegalComponentDefinition extends RuntimeException {

        private IllegalComponentDefinition(Throwable cause) {
            super(cause);
        }
    }
}
