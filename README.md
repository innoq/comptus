# Comptus

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Open Issues](https://img.shields.io/github/issues/innoq/comptus.svg)](https://github.com/innoq/comptus/issues)
[![Build Status](https://github.com/innoq/comptus/actions/workflows/main.yml/badge.svg)](https://github.com/innoq/comptus/actions/workflows/main.yml)

[Thymeleaf](https://www.thymeleaf.org) dialect for building Server-side rendered components.


## Quick Start

### Usage

A comptus component consists of two parts, a java class which extends
`com.innoq.comptus.core.Component` and a HTML template.

E.g. a `Badge` component would consist of the following class and template:


```java
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
```

```html
<div th:class="${this.classNames}">
    <co:slot />
</div>
```

The component can then be referenced from within a Thymeleaf template

```html
<co:badge>Test</co:badge>
<co:badge type="danger">This is dangerous</co:badge>
```

which results into the following HTML:

```html
<div class="badge bg-secondary">
    Test
</div>
<div class="badge bg-danger">
    This is dangerous
</div>
```


## License

comptus is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
