package org.spoofax.terms.typesmart;

import mb.terms.ITerm;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.OriginAttachment;
import org.spoofax.terms.typesmart.types.SortType;
import org.spoofax.terms.typesmart.types.TList;
import org.spoofax.terms.typesmart.types.TSort;

/**
 * @author Sebastian Erdweg
 */
public class TypesmartSortAttachment {
    public TypesmartSortAttachment(SortType[] sorts) {
        this.sorts = sorts;
    }

    SortType[] sorts;

    public SortType[] getSorts() {
        return sorts;
    }

    public static TypesmartSortAttachment get(ITerm term) {
        return term.getAttachment(TypesmartSortAttachment.class);
    }

    public static SortType[] getSorts(ITerm term) {
        TypesmartSortAttachment attach = get(term);
        if(attach != null) {
            return attach.getSorts();
        } else {
            ImploderAttachment imploderAttach = term.getAttachment(ImploderAttachment.class);
            if(imploderAttach != null) {
                if(imploderAttach.isSequenceAttachment()) {
                    return new SortType[] { new TList(new TSort(imploderAttach.getElementSort())) };
                } else {
                    return new SortType[] { new TSort(imploderAttach.getSort()) };
                }
            } else {
                OriginAttachment origin = term.getAttachment(OriginAttachment.class);
                if(origin != null) {
                    // TODO: Switch OriginAttachment::getOrigin to ITerm
//                    return getSorts(origin.getOrigin());
                    return null;
                } else {
                    return null;
                }
            }
        }
    }
}
