# Comptus

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[![Open Issues](https://img.shields.io/github/issues/innoq/comptus.svg)](https://github.com/innoq/comptus/issues)
[![Build Status](https://github.com/innoq/comptus/actions/workflows/main.yml/badge.svg)](https://github.com/innoq/comptus/actions/workflows/main.yml)

Comptus is a [Thymeleaf](https://www.thymeleaf.org) dialect for creating reuseable & encapsulated view components. 

Like [ViewComponents](https://viewcomponent.org/) or [Angular components](https://angular.io/guide/component-overview) Comptus uses real Java classes to represent a component. Each time a component is used anywhere a new instance of this class ist created and a component template is rendered based upon this instance.

## Getting started

All you have to do is registering Comptus is your Spring Boot Application by doing something like this:

```java
@SpringBootApplication
public class ThymeleafComptusApplication {

    @Bean
    public ComptusDialect comptusDialect() {
         return new ComptusDialect("com.name.of.your.compontents.package");
    }
}
```

## Defining a component

A comptus component consists of two parts, a java class which extends
`com.innoq.comptus.core.Component` and a HTML template.

E.g. a `Button` component would be based upon the following class:

```java
package com.name.of.your.compontents.package;

public class Button extends com.innoq.comptus.core.Component {
    public Button(ComponentContext context) {
        super(context);
    }
}
```

There also needs to be a Thymeleaf template which is rendered each time the component is used.

The Button's template should be placed under `/resources/templates/components/button.html` and could look like this:

```html
<button class="my-button">
    <co:slot />
</button>
```

## Using components

Now you can use `<co:button />` everywhere in your thymeleaf templates like this:

```html
<div>
    <co:button>Click me!</co:button>
</div>
```

This will produce the following HTML:

```html
<div>
    <button class="my-button">
        Click me!
    </button>
</div>
```

## Methods and attributes

Now let's think about providing data to your component from the outside. Maybe we want to style a button
based upon it's "styling" like `<co:button styling="cta">Click me!</co:button>`.

You can access these attributes for your component e.g. inside of your Button's constructor:

```java
private String styling;

public Button(ComponentContext context) {
    super(context);
    styling = stringAttribute("styling").orElse(null);
}
```

Now we'll have to use this information to render different HTML in our component template. 
This could be done by making the instance variable `styling` a `public` field.

But this could also be done by providing methods in your component's class:

```
public String getClassNames() {
    return "my-button %s %s".formatted("cta".equals(styling) ? "primary" : "default");
}
```

and calling this method from your component template:

```html
<button th:class="${this.classNames}">
    <co:slot />
</button>
```

### Complex attributes

The `th:` attribute based approach doesn't work when you want to pass other objects from your view model to your component. So `<co:my-component th:my-data="${mySpecialPOJO}" />` won't work since `th:*` would convert everything `toString()` at the end and all your component would see is a String.

So Comptus also defines the `co:*` attributes on all `<co:*>` elements like:

```
<co:my-component co:my-data="${mySpecialPOJO}" />
```

Now you can access your object inside your component using:

```
MySpecialPOJO myData = attribute("my-data", MySpecialPOJO.class).orElse(null);
```

## Slots

TBD (see src/test/resources/slots.html)

## Context

TBD (see src/test/resources/context.html)


## License

comptus is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
