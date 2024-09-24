package fr.utc.multeract.server.models;

import fr.utc.multeract.server.converter.HashMapConverter;
import jakarta.persistence.Convert;

@Convert(converter = HashMapConverter.class)
public @interface JsonMapStorage {
}
