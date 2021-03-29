package com.tyron.layouteditor.editor;

import android.content.Context;
import android.content.ContextWrapper;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.Value;

/**
 * ProteusContext
 *
 * @author aditya.sharat
 */

public class EditorContext extends ContextWrapper {

    @NonNull
    private final EditorResources resources;

    @Nullable
    private final EditorLayoutInflater.Callback callback;

    @Nullable
    private final EditorLayoutInflater.ImageLoader loader;

    private EditorLayoutInflater inflater;

    EditorContext(Context base, @NonNull EditorResources resources,
                   @Nullable EditorLayoutInflater.ImageLoader loader,
                   @Nullable EditorLayoutInflater.Callback callback) {
        super(base);
        this.callback = callback;
        this.loader = loader;
        this.resources = resources;
    }

    @Nullable
    public EditorLayoutInflater.Callback getCallback() {
        return callback;
    }

    @NonNull
    public FunctionManager getFunctionManager() {
        return resources.getFunctionManager();
    }

    @NonNull
    public Function getFunction(@NonNull String name) {
        return resources.getFunction(name);
    }

    @Nullable
    public Layout getLayout(@NonNull String name) {
        return resources.getLayout(name);
    }

    @Nullable
    public EditorLayoutInflater.ImageLoader getLoader() {
        return loader;
    }

    @NonNull
    public EditorLayoutInflater getInflater(@NonNull IdGenerator idGenerator) {
        if (null == this.inflater) {
            this.inflater = new SimpleLayoutInflater(this, idGenerator);
        }
        return this.inflater;
    }

    @NonNull
    public EditorLayoutInflater getInflater() {
        return getInflater(new SimpleIdGenerator());
    }

    @Nullable
    public ViewTypeParser getParser(String type) {
        return resources.getParsers().get(type);
    }

    @NonNull
    public EditorResources getEditorResources() {
        return resources;
    }

    @Nullable
    public Map<String, Value> getStyle(String name) {
        return resources.getStyle(name);
    }

    /**
     * Builder
     *
     * @author adityasharat
     */
    public static class Builder {

        @NonNull
        private final Context base;

        @NonNull
        private final FunctionManager functionManager;

        @NonNull
        private final Map<String, ViewTypeParser> parsers;

        @Nullable
        private EditorLayoutInflater.ImageLoader loader;

        @Nullable
        private EditorLayoutInflater.Callback callback;

        @Nullable
        private LayoutManager layoutManager;

        @Nullable
        private StyleManager styleManager;

        Builder(@NonNull Context context, @NonNull Map<String, ViewTypeParser> parsers, @NonNull FunctionManager functionManager) {
            this.base = context;
            this.parsers = parsers;
            this.functionManager = functionManager;
        }

        public Builder setImageLoader(@Nullable EditorLayoutInflater.ImageLoader loader) {
            this.loader = loader;
            return this;
        }

        public Builder setCallback(@Nullable EditorLayoutInflater.Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setLayoutManager(@Nullable LayoutManager layoutManager) {
            this.layoutManager = layoutManager;
            return this;
        }

        public Builder setStyleManager(@Nullable StyleManager styleManager) {
            this.styleManager = styleManager;
            return this;
        }

        public EditorContext build() {
            EditorResources resources = new EditorResources(parsers, layoutManager, functionManager, styleManager);
            return new EditorContext(base, resources, loader, callback);
        }

    }
}