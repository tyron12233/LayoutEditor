package xyz.truenight.dynamic.adapter.param;

import java.util.List;

import xyz.truenight.utils.Utils;

/**
 * Created by true
 * date: 03/03/2017
 * time: 03:45
 * <p>
 * Copyright © Mikhail Frolov
 */

public final class TypedParamAdapters {
    private TypedParamAdapters() {
    }

    public static final TypedParamAdapter VIEW_GROUP_ADAPTER = new ViewGroupParamAdapter();
    public static final TypedParamAdapter MARGIN_ADAPTER = new MarginParamAdapter();
    public static final TypedParamAdapter FRAME_LAYOUT_ADAPTER = new FrameLayoutParamAdapter();
    public static final TypedParamAdapter LINEAR_LAYOUT_ADAPTER = new LinearLayoutParamAdapter();

    public static List<TypedParamAdapter> DEFAULT = Utils.add(
            TypedParamAdapters.VIEW_GROUP_ADAPTER,
            TypedParamAdapters.MARGIN_ADAPTER,
            TypedParamAdapters.FRAME_LAYOUT_ADAPTER,
            TypedParamAdapters.LINEAR_LAYOUT_ADAPTER
    );
}
