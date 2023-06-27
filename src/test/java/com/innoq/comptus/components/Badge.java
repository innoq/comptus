package com.innoq.comptus.components;

import com.innoq.comptus.core.Component;

public class Badge extends Component {

    private final String type;

    public Badge(ComponentContext context) {
        super(context);
        type = stringAttribute("type").orElse("default");
    }

    public String getClassNames() {
        return "badge %s".formatted("danger".equals(type) ? "bg-danger" : "bg-secondary");
    }
}
