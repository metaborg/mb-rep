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
    defaults = @Value.Immutable(builder = false)
    // @formatter:on
)
public @interface ConstantClass {

}