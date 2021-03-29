package com.tyron.layouteditor.parser;

import android.view.View;
import android.view.ViewGroup;

import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.editor.widget.BaseWidget;
import com.tyron.layouteditor.models.Attribute;
import com.tyron.layouteditor.models.Widget;
import com.tyron.layouteditor.values.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ViewLayoutExporter {

    public static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    public static String inflate(BaseWidget widget) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder db = builderFactory.newDocumentBuilder();

        Document document = db.newDocument();

        Element rootElement = document.createElement(widget.getAsView().getClass().getSuperclass().getName());
        for (Attribute attr : widget.getAttributes()) {
            //skip empty attributes
            if (attr.value.isNull()) {
                continue;
            }
			
			if(attr.key.equals(Attributes.LinearLayout.Orientation)){
				attr.value = new Primitive(attr.value.getAsInt() == 0 ? "vertical" : "horizontal");
			}
			
			switch (Attributes.getType(attr.key)){
				case Attributes.TYPE_LAYOUT_STRING:
				    attr.value = new Primitive("@id/" + attr.value);
				break;
				
				case Attributes.TYPE_STRING:
				    if(attr.key.equals(Attributes.View.Id)){
						attr.value = new Primitive("@+id/" + attr.value);
					}
				break;
			}
            rootElement.setAttributeNS(ANDROID_NS, attr.key, attr.value.toString());
        }
        document.appendChild(rootElement);

        serializeView((ViewGroup) widget.getAsView(), rootElement);

        DOMSource source = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-attributes", "true");
        transformer.transform(source, result);


        return writer.toString();
    }

    public static void serializeView(ViewGroup viewGroup, Element element) {

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            Element childElement = element.getOwnerDocument().createElement(child.getClass().getSuperclass().getName());

            for (Attribute attr : ((BaseWidget) child).getAttributes()) {
                if (attr == null || attr.value.isNull()) {
                    continue;
                }
				
				if(attr.key.equals(Attributes.LinearLayout.Orientation)){
				    attr.value = new Primitive(attr.value.getAsInt() == 0 ? "vertical" : "horizontal");
			    }
			
			    switch (Attributes.getType(attr.key)){
				    case Attributes.TYPE_LAYOUT_STRING:
				        attr.value = new Primitive("@id/" + attr.value);
				        break;
				
				    case Attributes.TYPE_STRING:
				        if(attr.key.equals(Attributes.View.Id)){
						    attr.value = new Primitive("@+id/" + attr.value);
					     }
				     break;
				}
			
                childElement.setAttributeNS(ANDROID_NS,  attr.key, attr.value.toString());
            }
            if (child instanceof ViewGroup) {
                serializeView((ViewGroup) child, childElement);
            }
            element.appendChild(childElement);
        }
    }

    public static Element createElement(Node node) {
        return null;
    }

    /**
     * @param name name of the editor item
     * @return return the android equivalent of the widget
     */
    public static String getAndroidWidgetName(String name) {
        if (name.equals(Widget.LINEAR_LAYOUT)) {
            return "android.widget.LinearLayout";
        } else if (name.equals(Widget.RELATIVE_LAYOUT)) {
            return "android.widget.RelativeLayout";
        }
        return name;
    }
}
