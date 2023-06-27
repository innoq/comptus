package com.innoq.comptus.components;

import com.innoq.comptus.core.Component;

public class Card extends Component {

    public final String href;

    public Card(ComponentContext context) {
        super(context);
        href = stringAttribute("href").orElse(null);
    }
}
