package com.innoq.comptus.components;

import com.innoq.comptus.core.Component;

public class Header extends Component {

    public final int level;

    public Header(ComponentContext context) {
        super(context);
        level = intAttribute("level")
                .orElseGet(() ->
                        outerVariable("parentMagicHeaders", MagicHeaders.class)
                                .map(magicHeaders -> magicHeaders.level)
                                .orElse(1));
    }
}
