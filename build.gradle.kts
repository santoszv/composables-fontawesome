import jakarta.json.Json
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly

buildscript {
    dependencies {
        classpath("org.glassfish:jakarta.json:2.0.1")
    }
}

plugins {
    kotlin("js") version "1.7.10"
    id("org.jetbrains.compose") version "1.2.0"
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.runtime)
    implementation(compose.web.core)
    implementation(compose.web.svg)
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("org.jetbrains.compose.web.ExperimentalComposeWebSvgApi")
    }
    js(IR) {
        browser()
    }
}

val generateComposables by tasks.registering(DefaultTask::class) {
    doLast {
        file("src/main/kotlin/fontawesome").mkdirs()
        file("src/main/kotlin/fontawesome/fontawesome.kt").bufferedWriter().use { writer ->
            writer.appendLine("/*")
            file("LICENSE.txt").readLines().forEach { writer.appendLine(it) }
            writer.appendLine("*/")
            writer.appendLine()
            writer.appendLine("package fontawesome")
            writer.appendLine()
            writer.appendLine("import androidx.compose.runtime.Composable")
            writer.appendLine("import org.jetbrains.compose.web.css.em")
            writer.appendLine("import org.jetbrains.compose.web.css.height")
            writer.appendLine("import org.jetbrains.compose.web.css.width")
            writer.appendLine("import org.jetbrains.compose.web.dom.AttrBuilderContext")
            writer.appendLine("import org.jetbrains.compose.web.svg.Path")
            writer.appendLine("import org.jetbrains.compose.web.svg.Svg")
            writer.appendLine("import org.w3c.dom.svg.SVGElement")
            file("icons.json").inputStream().use { input ->
                val iconsMetadata = Json.createParser(input).let {
                    it.next()
                    it.`object`
                }
                for ((iconName, iconMetadata) in iconsMetadata) {
                    iconMetadata as jakarta.json.JsonObject
                    val svgMetadata = iconMetadata.getJsonObject("svg")
                    for ((styleName, svgData) in svgMetadata) {
                        svgData as jakarta.json.JsonObject
                        val identifier = run {
                            val t1 = styleName.replace(Regex("\\W"), "_").split('_').joinToString("") { it.capitalizeAsciiOnly() }
                            val t2 = iconName.replace(Regex("\\W"), "_").split('_').joinToString("") { it.capitalizeAsciiOnly() }
                            "fa$t1$t2"
                        }
                        val viewBox = svgData.getJsonArray("viewBox")
                        val path = svgData.getString("path")
                        writer.appendLine()
                        writer.appendLine("@Composable")
                        writer.appendLine("fun ${identifier}(attrs: AttrBuilderContext<SVGElement>? = null) {")
                        writer.appendLine("    Svg(viewBox = \"${viewBox.getInt(0)} ${viewBox.getInt(1)} ${viewBox.getInt(2)} ${viewBox.getInt(3)}\", attrs = {")
                        writer.appendLine("        style {")
                        writer.appendLine("            width(1.em)")
                        writer.appendLine("            height(1.em)")
                        writer.appendLine("            property(\"vertical-align\", \"-.125em\")")
                        writer.appendLine("        }")
                        writer.appendLine("        attr(\"aria-hidden\", \"true\")")
                        writer.appendLine("        attr(\"focusable\", \"false\")")
                        writer.appendLine("        attrs?.invoke(this)")
                        writer.appendLine("    }) {")
                        writer.appendLine("        Path(")
                        writer.appendLine("            attrs = {")
                        writer.appendLine("                attr(\"fill\", \"currentColor\")")
                        writer.appendLine("            },")
                        writer.appendLine("            d = \"$path\"")
                        writer.appendLine("        )")
                        writer.appendLine("    }")
                        writer.appendLine("}")
                    }
                }
            }
        }
    }
}

val jsJar by tasks.getting(Jar::class)

val jsSourcesJar by tasks.getting(org.gradle.jvm.tasks.Jar::class)

val jsJavadoc by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(file("$projectDir/files/README"))
}

publishing {
    publications.register<MavenPublication>("js") {
        artifact(jsJar)
        artifact(jsSourcesJar)
        artifact(jsJavadoc)
        groupId = "mx.com.inftel.oss"
        artifactId = "fontawesome-composables"
        version = "6.2.0-r2"
        pom {
            name.set("Composables FontAwesome Icons")
            description.set("Composables FontAwesome Icons")
            url.set("https://github.com/santoszv/composables-fontawesome")
            inceptionYear.set("2022")
            licenses {
                license {
                    name.set("Font Awesome Free License")
                    url.set("https://fontawesome.com/license/free")
                }
                license {
                    name.set("CC BY 4.0 License")
                    url.set("https://creativecommons.org/licenses/by/4.0/")
                }
            }
            developers {
                developer {
                    id.set("santoszv")
                    name.set("Santos Zatarain/Vera")
                    email.set("santoszv@inftel.com.mx")
                    url.set("https://www.inftel.com.mx")
                }
            }
            scm {
                connection.set("scm:git:https://github.com/santoszv/composables-fontawesome")
                developerConnection.set("scm:git:https://github.com/santoszv/composables-fontawesome")
                url.set("https://github.com/santoszv/composables-fontawesome")
            }
        }
        signing.sign(this)
    }

    repositories {
        maven {
            setUrl(file("$projectDir/build/repo"))
        }
    }
}

signing {
    useGpgCmd()
}

// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        nodeVersion = "16.0.0"
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.9.0"
    }
}