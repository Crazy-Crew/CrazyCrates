package com.ryderbelserion.config.types;

import com.ryderbelserion.api.api.properties.PropertyBuilder;
import com.ryderbelserion.api.api.properties.interfaces.IPropertyHolder;
import com.ryderbelserion.api.api.properties.objects.interfaces.Property;

public class SecondaryKeys implements IPropertyHolder {

    public static Property<String> send_message = PropertyBuilder.newProperty("send_message", "root", "message-state");

}