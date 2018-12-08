package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
abstract class AbstractTermFloat implements ITerm {
    public static final TermKind termKind = TermKind.Float;

    public abstract float value();

    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ITermAttachment> attachments();

    @Override
    public TermKind getTermKind() {
        return termKind;
    }
}
