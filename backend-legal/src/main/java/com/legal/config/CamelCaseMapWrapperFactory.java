package com.legal.config;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.Map;

public class CamelCaseMapWrapperFactory implements ObjectWrapperFactory {
    @Override
    public boolean hasWrapperFor(Object object) {
        return object instanceof Map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new MapWrapper(metaObject, (Map<String, Object>) object) {
            @Override
            public String findProperty(String name, boolean useCamelCaseMapping) {
                if (!useCamelCaseMapping || name.indexOf('_') < 0) return name;
                StringBuilder result = new StringBuilder();
                boolean upper = false;
                for (char c : name.toCharArray()) {
                    if (c == '_') upper = true;
                    else {
                        result.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                        upper = false;
                    }
                }
                return result.toString();
            }
        };
    }
}
