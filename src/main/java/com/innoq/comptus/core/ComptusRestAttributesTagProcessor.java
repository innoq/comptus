package com.innoq.comptus.core;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import static org.thymeleaf.templatemode.TemplateMode.HTML;

public class ComptusRestAttributesTagProcessor extends AbstractAttributeTagProcessor {

    public static final int PRECEDENCE = 200;
    public static final String ATTR_NAME = "rest";

    ComptusRestAttributesTagProcessor(final String dialectPrefix) {
        super(HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        if (context.getVariable("this") instanceof Component component) {
            component.getRestAttributes()
                .forEach((key, value) -> structureHandler.setAttribute(key, value == null ? "" : value.toString()));
        }
    }
}
