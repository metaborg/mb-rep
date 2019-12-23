package mb.terms;

import org.immutables.value.Value;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Value.Immutable
public abstract class AbstractTermString implements ITermString {
    @Override
    public abstract String value();

    @Override
    public abstract List<ITerm> annotations();

    @Value.Auxiliary
    public abstract HashMap<Class<?>, Object> attachments();
}
