package com.ryderbelserion.config.types;

import com.ryderbelserion.api.api.properties.PropertyBuilder;
import com.ryderbelserion.api.api.properties.interfaces.IPropertyHolder;
import com.ryderbelserion.api.api.properties.objects.interfaces.Property;

public class TestKeys implements IPropertyHolder {

    public static Property<String> example_property = PropertyBuilder.newProperty("bleh.", "root", "bleh");

}