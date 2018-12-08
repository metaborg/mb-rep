package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.usethesource.capsule.Set;
import org.immutables.value.Value;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
abstract class AbstractTermSet implements ITerm {
    public static final TermKind termKind = TermKind.Set;

    public abstract Set.Immutable<ITerm> set();

    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ITermAttachment> attachments();

    @Override
    public TermKind getTermKind() {
        return termKind;
    }
}
