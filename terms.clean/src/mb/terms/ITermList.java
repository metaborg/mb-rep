package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public interface ITermList extends Iterable<ITerm>, ITermListForStratego {
    default Iterator<ITerm> iterator() {
        return new Iter(this.children(), offset());
    }
}
