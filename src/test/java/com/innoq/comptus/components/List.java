package com.innoq.comptus.components;

import com.innoq.comptus.core.Component;

import java.util.Collections;

public class List extends Component {

    public final java.util.List<?> children;

    public List(ComponentContext context) {
        super(context);
        children = attribute("children", java.util.List.class).orElse(Collections.emptyList());
    }
}
