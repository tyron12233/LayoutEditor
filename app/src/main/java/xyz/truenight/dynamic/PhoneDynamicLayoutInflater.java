/**
 * Copyright (C) 2017 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.dynamic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.tyron.layouteditor.WidgetFactory;
import com.tyron.layouteditor.models.Widget;

public class PhoneDynamicLayoutInflater extends DynamicLayoutInflater {
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit."
    };

    /**
     * Instead of instantiating directly, you should retrieve an instance
     * through {@link DynamicLayoutInflater#from(Context)}
     *
     * @param context The Context in which in which to find resources and other
     *                application-specific things.
     */


    protected PhoneDynamicLayoutInflater(Context context) {
        super(context);
    }

    protected PhoneDynamicLayoutInflater(DynamicLayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    /**
     * Override onCreateView to instantiate names that correspond to the
     * widgets known to the Widget factory. If we don't find a match,
     * call through to our super class.
     */
    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {

                View view = null;

                switch(name){
                    case  "RelativeLayout":
                        view = widgetFactory.createWidget(getContext(), new Widget(Widget.RELATIVE_LAYOUT));
                        break;
                    case "LinearLayout":
                        view = widgetFactory.createWidget(getContext(), new Widget(Widget.LINEAR_LAYOUT));
                        break;
                    case "TextView":
                        view = widgetFactory.createWidget(getContext(), new Widget(Widget.TEXTVIEW));
                        break;
                }
                if(view == null) {
                    view = createView(name, prefix, attrs);
                }
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }

    public DynamicLayoutInflater cloneInContext(Context newContext) {
        return new PhoneDynamicLayoutInflater(this, newContext);
    }
}