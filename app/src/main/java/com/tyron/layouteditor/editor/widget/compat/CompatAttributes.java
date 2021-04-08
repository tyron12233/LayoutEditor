package com.tyron.layouteditor.editor.widget.compat;

import com.tyron.layouteditor.editor.widget.Attributes;
import com.tyron.layouteditor.models.Attribute;

import java.util.HashMap;

public class CompatAttributes {

    private static HashMap<String, Integer> types = new HashMap<>();

    static {
        types.put(ConstraintLayout.LeftToLeftOf, Attributes.TYPE_LAYOUT_STRING);
        types.put(ConstraintLayout.LeftToRightOf, Attributes.TYPE_LAYOUT_STRING);
        types.put(ConstraintLayout.RightToLeftOf, Attributes.TYPE_LAYOUT_STRING);
        types.put(ConstraintLayout.RightToRightOf, Attributes.TYPE_LAYOUT_STRING);
        types.put(ConstraintLayout.BottomToBottomOf, Attributes.TYPE_LAYOUT_STRING);
        types.put(ConstraintLayout.BottomToTopOf, Attributes.TYPE_LAYOUT_STRING);
        types.put(ConstraintLayout.TopToBottomOf, Attributes.TYPE_LAYOUT_STRING);
        types.put(ConstraintLayout.TopToTopOf, Attributes.TYPE_LAYOUT_STRING);

        types.put(CardView.BackgroundColor, Attributes.TYPE_COLOR);
        types.put(MaterialCardView.RippleColor, Attributes.TYPE_COLOR);
        types.put(MaterialCardView.ForegroundColor, Attributes.TYPE_COLOR);

        types.put(CardView.ContentPadding, Attributes.TYPE_DIMENSION);
        types.put(CardView.ContentPaddingLeft, Attributes.TYPE_DIMENSION);
        types.put(CardView.ContentPaddingTop, Attributes.TYPE_DIMENSION);
        types.put(CardView.ContentPaddingRight, Attributes.TYPE_DIMENSION);
        types.put(CardView.ContentPaddingBottom, Attributes.TYPE_DIMENSION);
        types.put(CardView.Elevation, Attributes.TYPE_DIMENSION);
        types.put(CardView.MaxElevation, Attributes.TYPE_DIMENSION);
        types.put(CardView.CornerRadius, Attributes.TYPE_DIMENSION);
        types.put(CoordinatorLayout.PeekHeight, Attributes.TYPE_DIMENSION);
        types.put(MaterialCardView.CheckedIconMargin, Attributes.TYPE_DIMENSION);
        types.put(MaterialCardView.CheckedIconSize, Attributes.TYPE_DIMENSION);

        types.put(MaterialCardView.Checkable, Attributes.TYPE_BOOLEAN);
        types.put(CardView.PreventCornerOverlap, Attributes.TYPE_BOOLEAN);
        types.put(CardView.UseCompatPadding, Attributes.TYPE_BOOLEAN);
        types.put(CoordinatorLayout.Hideable, Attributes.TYPE_BOOLEAN);

        types.put(MaterialCardView.CheckedIcon, Attributes.TYPE_DRAWABLE);
    }
    public static class View {
        public static final String Background = "app:background";
    }
    public static class ConstraintLayout{
        public static final String LeftToLeftOf = "app:layout_constraintLeft_toLeftOf";
        public static final String LeftToRightOf = "app:layout_constraintLeft_toRightOf";
        public static final String RightToLeftOf = "app:layout_constraintRight_toLeftOf";
        public static final String RightToRightOf = "app:layout_constraintRight_toRightOf";

        public static final String BottomToBottomOf = "app:layout_constraintBottom_toBottomOf";
        public static final String BottomToTopOf = "app:layout_constraintBottom_toTopOf";
        public static final String TopToBottomOf = "app:layout_constraintTop_toBottomOf";
        public static final String TopToTopOf = "app:layout_constraintTop_toTopOf";
    }

    public static class CardView {
        public static final String BackgroundColor = "app:cardBackgroundColor";
        public static final String CornerRadius = "app:cardCornerRadius";
        public static final String Elevation = "app:cardElevation";
        public static final String MaxElevation = "app:cardMaxElevation";
        public static final String PreventCornerOverlap = "app:cardPreventCornerOverlap";
        public static final String UseCompatPadding = "app:cardUseCompatPadding";
        public static final String ContentPadding = "app:contentPadding";
        public static final String ContentPaddingLeft = "app:contentPaddingLeft";
        public static final String ContentPaddingRight = "app:contentPaddingRight";
        public static final String ContentPaddingBottom = "app:contentPaddingBottom";
        public static final String ContentPaddingTop = "app:contentPaddingTop";
    }

    public static class CoordinatorLayout {
        public static final String Behavior = "app:layout_behavior";
        public static final String Hideable = "app:behavior_hideable";
        public static final String PeekHeight = "app:peekHeight";
    }

    public static class MaterialCardView {
        public static final String Checkable = "android:checkable";
        public static final String RippleColor = "app:rippleColor";
        public static final String ForegroundColor = "app:cardForegroundColor";
        public static final String CheckedIcon = "app:checkedIcon";
        public static final String CheckedIconTint = "app:checkedIconTint";
        public static final String CheckedIconSize = "app:checkedIconSize";
        public static final String CheckedIconMargin = "app:checkedIconMargin";
    }

    public static Integer getType(String name){
        return types.get(name);
    }
}
