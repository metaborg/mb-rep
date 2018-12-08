package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.usethesource.capsule.Map;
import org.immutables.value.Value;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
abstract class AbstractTermMap implements ITerm {
    public static final TermKind termKind = TermKind.Map;

    public abstract Map.Immutable<ITerm, ITerm> map();

    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ITermAttachment> attachments();

    @Override
    public TermKind getTermKind() {
        return termKind;
    }
}
