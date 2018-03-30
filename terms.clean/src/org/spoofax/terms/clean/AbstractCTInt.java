package org.spoofax.terms.clean;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
abstract class AbstractCTInt implements ICleanTerm {
    public static final TermKind termKind = TermKind.Int;

    public abstract int value();

    public abstract List<ICleanTerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ICleanTermAttachment> attachments();

    @Override
    public TermKind getTermKind() {
        return termKind;
    }
}
