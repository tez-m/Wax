package com.tezm.wax.injection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class InjectionMap
{
    private Map<String, List<InjectionField>> classMap = new HashMap<>();

    public void appendClass(String className)
    {
        if (!classMap.containsKey(className))
        {
            classMap.put(className, new ArrayList<>());
        }
    }

    public void appendField(String className, String fieldName, String fieldType, String factoryName)
    {
        appendClass(className);
        classMap.get(className).add(new InjectionField(fieldName, fieldType, factoryName));
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJson()
    {
        JSONObject root = new JSONObject();
        JSONArray injectionMap = new JSONArray();

        for (Map.Entry<String, List<InjectionField>> entry : classMap.entrySet())
        {
            JSONArray fields = new JSONArray();

            for (InjectionField injectionField : entry.getValue())
            {
                JSONObject field = new JSONObject();
                field.put("name", injectionField.name);
                field.put("type", injectionField.type);
                field.put("factory", injectionField.factory);
                fields.add(field);
            }

            JSONObject injection = new JSONObject();
            injection.put("class", entry.getKey());
            injection.put("fields", fields);
            injectionMap.add(injection);
        }

        root.put("injection_map", injectionMap);
        return root;
    }
}
