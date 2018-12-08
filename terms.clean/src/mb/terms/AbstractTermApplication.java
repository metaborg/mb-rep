package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
abstract class AbstractTermApplication implements ITerm {
    public static final TermKind termKind = TermKind.Application;

    public abstract String constructor();

    public abstract List<ITerm> children();

    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ITermAttachment> attachments();

    @Override
    public TermKind getTermKind() {
        return termKind;
    }
}
