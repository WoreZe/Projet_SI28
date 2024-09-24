package fr.utc.multeract.server.models;

import fr.utc.multeract.server.converter.ListConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Converts;

import java.lang.annotation.Repeatable;

@Convert(converter = ListConverter.class)
public @interface JsonListStorage {
}
