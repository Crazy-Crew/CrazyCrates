package com.ryderbelserion.config.types;

import com.ryderbelserion.api.api.properties.PropertyBuilder;
import com.ryderbelserion.api.api.properties.interfaces.IPropertyHolder;
import com.ryderbelserion.api.api.properties.objects.interfaces.Property;
import java.util.List;

public class ConfigKeys implements IPropertyHolder {

    public static Property<String> example_property = PropertyBuilder.newProperty("This is a default value.", "root", "test-message");

    public static Property<Integer> example_integer_property = PropertyBuilder.newProperty(1, "root", "example-integer");

    public static Property<Boolean> example_boolean_property = PropertyBuilder.newProperty(true, "root", "use-new-editor");

    public static Property<List<String>> example_list_property = PropertyBuilder.newProperty(List.of("blank message"), "root", "messages");

}