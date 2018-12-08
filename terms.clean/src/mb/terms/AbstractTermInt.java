package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
public abstract class AbstractTermInt implements ITerm {

    public abstract int value();

    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ITermAttachment> attachments();

    @Override
    public TermKind getTermKind() {
        return TermKind.Int;
    }
}
