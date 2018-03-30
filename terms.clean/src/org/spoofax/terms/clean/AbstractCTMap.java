package org.spoofax.terms.clean;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.usethesource.capsule.Map;
import org.immutables.value.Value;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
abstract class AbstractCTMap implements ICleanTerm {
    public static final TermKind termKind = TermKind.Map;

    public abstract Map.Immutable<ICleanTerm, ICleanTerm> map();

    public abstract List<ICleanTerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ICleanTermAttachment> attachments();

    @Override
    public TermKind getTermKind() {
        return termKind;
    }
}
