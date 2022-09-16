# FontAwesome Composables

FontAwesome SVG Icons as composables for Compose for Web.

More info:

- https://fontawesome.com/
- https://www.jetbrains.com/lp/compose-mpp/

## Usage

Add to `build.gradle.kts`:

```
plugins {
    kotlin("js") version <version>
    id("org.jetbrains.compose") version <version>
}

repositories {
    ...
    mavenCentral()
    ...
}

dependencies {
    ...
    implementation(compose.runtime)
    implementation(compose.web.core)
    implementation(compose.web.svg)
    implementation("mx.com.inftel.oss:fontawesome-composables:<version>")
    ...
}
```

More info:

- https://fontawesome.com/docs/web/add-icons/svg-bare

### Version compatibility

| FontAwesome Composables | Compose for Web | KotlinJS |
|-------------------------|-----------------|----------|
| 6.1.2-r1                | 1.1.1           | 1.6.10   |
| 6.2.0                   | 1.1.1           | 1.6.10   |
## License

Font Awesome Free License

Font Awesome Free is free, open source, and GPL friendly. You can use it for
commercial projects, open source projects, or really almost whatever you want.
Full Font Awesome Free license: https://fontawesome.com/license/free.

Icons: CC BY 4.0 License (https://creativecommons.org/licenses/by/4.0/)

The Font Awesome Free download is licensed under a Creative Commons
Attribution 4.0 International License and applies to all icons packaged
as SVG and JS file types.
