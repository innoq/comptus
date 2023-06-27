package com.innoq.comptus.components;

import com.innoq.comptus.core.Component;

public class Button extends Component {

    private String styling;
    private String additionalClasses;

    public Button(ComponentContext context) {
        super(context);
        styling = stringAttribute("styling").orElse(null);
        additionalClasses = stringAttribute("additional-classes").orElse("");
    }

    public String getClassNames() {
        return "btn %s %s".formatted(("cta".equals(styling) ? "btn-primary" : "btn-secondary"), additionalClasses);
    }
}
