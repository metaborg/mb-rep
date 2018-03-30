package org.spoofax.terms.clean;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.usethesource.capsule.Set;
import org.immutables.value.Value;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
abstract class AbstractCTSet implements ICleanTerm {
    public static final TermKind termKind = TermKind.Set;

    public abstract Set.Immutable<ICleanTerm> set();

    public abstract List<ICleanTerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ICleanTermAttachment> attachments();

    @Override
    public TermKind getTermKind() {
        return termKind;
    }
}
