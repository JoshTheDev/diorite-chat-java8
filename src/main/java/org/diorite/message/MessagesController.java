package org.diorite.message;

import javax.annotation.Nullable;
import javax.script.ScriptException;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

import org.diorite.message.loader.LocaleMessagesLoader;
import org.diorite.message.loader.MessagesLoader;
import org.diorite.message.loader.MessagesLoaderBuilder;
import org.diorite.message.loader.YamlMessageLoader;

import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;

public class MessagesController
{
    private GroovyScriptEngineImpl groovy;

    private final Map<String, LocaleMessagesLoader> loadersByName      = new ConcurrentHashMap<>();
    private final Map<String, LocaleMessagesLoader> loadersByExtension = new ConcurrentHashMap<>();

    {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        ImportCustomizer importCustomizer = new ImportCustomizer();
        importCustomizer.addStarImports("org.diorite.config", "org.diorite.config.serialization", "org.diorite", "org.diorite.config.exceptions");
        compilerConfiguration.addCompilationCustomizers(importCustomizer);
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(this.getClass().getClassLoader(), compilerConfiguration);
        this.groovy = new GroovyScriptEngineImpl(groovyClassLoader);

        this.registerLocaleMessagesLoader(new YamlMessageLoader(this));
    }

    public void registerLocaleMessagesLoader(LocaleMessagesLoader localeMessagesLoader)
    {
        this.loadersByName.put(localeMessagesLoader.getName(), localeMessagesLoader);
        this.loadersByExtension.put(localeMessagesLoader.getExtension(), localeMessagesLoader);
    }

    @Nullable
    public LocaleMessagesLoader getLocaleMessagesLoaderByName(String name)
    {
        return this.loadersByName.get(name);
    }

    @Nullable
    public LocaleMessagesLoader getLocaleMessagesLoaderByExtension(String extension)
    {
        return this.loadersByExtension.get(extension);
    }

    public Collection<? extends LocaleMessagesLoader> getLocaleMessagesLoaders()
    {
        return Collections.unmodifiableCollection(this.loadersByName.values());
    }

    public MessagesLoaderBuilder loaderBuilder()
    {
        return MessagesLoader.builder(this);
    }

    public MessagesLoader simpleLoader(File folder, Class<?> resourceSource, String basePath)
    {
        return this.loaderBuilder().localeLoaderFromFolder(folder).defaultLoaderFromResourcePath(resourceSource, basePath).build();
    }

    public Closure<?> createInterpolatedClosure(String stringValue) throws ScriptException
    {
        String value = "Closure msg = { -> \"\"\"" + stringValue + "\"\"\" }; return msg;";
        return (Closure<?>) this.groovy.eval(value);
    }
}
