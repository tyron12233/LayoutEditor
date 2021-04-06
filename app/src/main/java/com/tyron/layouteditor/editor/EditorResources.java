package com.tyron.layouteditor.editor;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tyron.layouteditor.values.Layout;
import com.tyron.layouteditor.values.Value;

/**
 * ProteusResources
 *
 * @author adityasharat
 */

public class EditorResources {

    @NonNull
    private final Map<String, ViewTypeParser> parsers;

    @Nullable
    private final LayoutManager layoutManager;

    @NonNull
    private final FunctionManager functionManager;

    @Nullable
    private final StyleManager styleManager;

    private final DrawableManager drawableManager;

    EditorResources(@NonNull Map<String, ViewTypeParser> parsers, @Nullable LayoutManager layoutManager,
                     @NonNull FunctionManager functionManager, @Nullable StyleManager styleManager,
                    @NonNull DrawableManager drawableManager) {
        this.parsers = parsers;
        this.layoutManager = layoutManager;
        this.functionManager = functionManager;
        this.styleManager = styleManager;
        this.drawableManager = drawableManager;
    }

    @NonNull
    public FunctionManager getFunctionManager() {
        return this.functionManager;
    }
	
	@NonNull
	public LayoutManager getLayoutManager() {
		return this.layoutManager;
	}

	@NonNull
    public DrawableManager getDrawableManager(){
        return this.drawableManager;
    }

    @NonNull
    public String getDrawable(String name){
        return drawableManager.get(name);
    }

    @NonNull
    public Function getFunction(@NonNull String name) {
        return functionManager.get(name);
    }

    @Nullable
    public Layout getLayout(@NonNull String name) {
        return null != layoutManager ? layoutManager.get(name) : null;
    }

    @NonNull
    public Map<String, ViewTypeParser> getParsers() {
        return parsers;
    }

    @Nullable
    public Map<String, Value> getStyle(String name) {
        return null != styleManager ? styleManager.get(name) : null;
    }

    public void putDrawable(String name, String path){
        this.drawableManager.put(name, path);
    }
}