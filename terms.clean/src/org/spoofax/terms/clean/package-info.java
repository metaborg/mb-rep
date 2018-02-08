@Value.Style(
        // @formatter:off
        typeImmutable = "*", // No prefix or suffix for generated immutable type
        defaults = @Value.Immutable(builder = false, prehash = true),
        allParameters = true,
        visibility = Value.Style.ImplementationVisibility.PUBLIC
        // @formatter:on
)
package org.spoofax.terms.clean;

import org.immutables.value.Value;