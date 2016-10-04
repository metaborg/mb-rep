package org.metaborg.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.immutables.value.Value;

@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
@Value.Style(
    // @formatter:off
    typeAbstract = { "*V" },
    typeImmutable = "*",
    get = { "is*", "get*" },
    allParameters = true,
    with = "set*",
    defaults = @Value.Immutable(builder = false, prehash = true)
    // @formatter:on
)
public @interface ConstructorClass {

}