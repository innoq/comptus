package com.innoq.comptus.components;

import com.innoq.comptus.core.Component;

public class MagicHeaders extends Component {

    private final MagicHeaders parent;
    public final int level;

    public MagicHeaders(ComponentContext context) {
        super(context);
        parent = outerVariable("parentMagicHeaders", MagicHeaders.class).orElse(null);
        level = parent == null ? 1 : (parent.level + 1);
        setInnerVariable("parentMagicHeaders", this);
    }
}
