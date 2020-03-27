package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.attachments.ITermAttachment;

import java.io.IOException;

/**
 * A simple text term writer that writes terms and their annotations and attachments
 * in a format that is mostly meant for debugging.
 *
 * @implSpec Override members of this class to influence term writing.
 * Overriding implementation should make sure to call {@link #writeTerm} to write subterms,
 * as this method checks the depth and writes the annotations and attachments, if applicable.
 */
public class SimpleTextTermWriter implements TextTermWriter {

    /** Singleton instance of the term writer. */
    private static final SimpleTextTermWriter INSTANCE = new SimpleTextTermWriter();

    /**
     * Gets the singleton instance of the term writer with the default configuration.
     *
     * @return the singleton instance
     */
    public static SimpleTextTermWriter getInstance() { return INSTANCE; }

    private static final String ELLIPSIS = "…";
    private static final String LIST_SEPARATOR = ",";
    private static final String LIST_TAIL_SEPARATOR = "|";

    private final int maxDepth;
    private final boolean ignoreAnnotations;
    private final boolean ignoreAttachments;
    private final boolean ignoreListTailAttributes;
    private final boolean ignoreAnnotationAttributes;

    /**
     * Creates a new instance of the {@link SimpleTextTermWriter} class
     * that prints only attachments and ignores those on list tails and on attachments,
     * up to unlimited depth.
     */
    public SimpleTextTermWriter() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Creates a new instance of the {@link SimpleTextTermWriter} class
     * that prints only attachments and ignores those on list tails and on attachments.
     *
     * @param maxDepth the maximum depth up to which terms are written;
     *                 or {@link Integer#MAX_VALUE} to not limit the depth
     */
    public SimpleTextTermWriter(int maxDepth) {
        this(maxDepth, false, true);
    }

    /**
     * Creates a new instance of the {@link SimpleTextTermWriter} class.
     *
     * @param maxDepth          the maximum depth up to which terms are written;
     *                          or {@link Integer#MAX_VALUE} to not limit the depth
     * @param ignoreAnnotations whether to ignore writing annotations
     * @param ignoreAttachments whether to ignore writing attachments
     */
    public SimpleTextTermWriter(int maxDepth, boolean ignoreAnnotations, boolean ignoreAttachments) {
        this(maxDepth, ignoreAnnotations, ignoreAttachments, true, true);
    }

    /**
     * Creates a new instance of the {@link SimpleTextTermWriter} class.
     *
     * @param maxDepth                   the maximum depth up to which terms are written;
     *                                   or {@link Integer#MAX_VALUE} to not limit the depth
     * @param ignoreAnnotations          whether to ignore writing annotations
     * @param ignoreAttachments          whether to ignore writing attachments
     * @param ignoreListTailAttributes   whether to ignore annotations and/or attachments on list tails
     * @param ignoreAnnotationAttributes whether to ignore annotations and/or attachments on annotations
     */
    public SimpleTextTermWriter(int maxDepth, boolean ignoreAnnotations, boolean ignoreAttachments, boolean ignoreListTailAttributes, boolean ignoreAnnotationAttributes) {
        this.maxDepth = maxDepth;
        this.ignoreAnnotations = ignoreAnnotations;
        this.ignoreAttachments = ignoreAttachments;
        this.ignoreListTailAttributes = ignoreListTailAttributes;
        this.ignoreAnnotationAttributes = ignoreAnnotationAttributes;
    }

    @Override public void write(IStrategoTerm term, Appendable writer) throws IOException {
        writeTerm(term, writer, this.maxDepth, false);
    }

    /**
     * Writes a term if there is sufficient remaining depth.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeTerm(IStrategoTerm term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        if (depth <= 0) {
            writeElided(writer);
            return;
        }

        switch (term.getTermType()) {
            case IStrategoTerm.APPL:
                writeApplBody((IStrategoAppl)term, writer, depth, isAnnotation);
                break;
            case IStrategoTerm.LIST:
                writeListBody((IStrategoList)term, writer, depth, isAnnotation);
                break;
            case IStrategoTerm.TUPLE:
                writeTupleBody((IStrategoTuple)term, writer, depth, isAnnotation);
                break;
            case IStrategoTerm.INT:
                writeIntBody((IStrategoInt)term, writer, depth, isAnnotation);
                break;
            case IStrategoTerm.REAL:
                writeRealBody((IStrategoReal)term, writer, depth, isAnnotation);
                break;
            case IStrategoTerm.STRING:
                writeStringBody((IStrategoString)term, writer, depth, isAnnotation);
                break;
            case IStrategoTerm.REF:
                writeRefBody((IStrategoRef)term, writer, depth, isAnnotation);
                break;
            case IStrategoTerm.PLACEHOLDER:
                writePlaceholderBody((IStrategoPlaceholder)term, writer, depth, isAnnotation);
                break;
            default:
                throw new RuntimeException("Unknown term type: " + term.getTermType() + " for term of type " + term.getClass().getSimpleName());
        }

        if (termHasAnnotations(term, isAnnotation)) writeAnnotations(term, writer, depth, isAnnotation);
        if (termHasAttachments(term, isAnnotation)) writeAttachments(term, writer, depth, isAnnotation);
    }

    /**
     * Writes a constructor application term.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeApplBody(IStrategoAppl term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        writer.append(term.getConstructor().getName());
        writer.append('(');
        writeSubterms(term, writer, depth - 1, isAnnotation);
        writer.append(')');
    }

    /**
     * Writes a list term.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeListBody(IStrategoList term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        writer.append('[');
        if (!term.isEmpty()) {
            writeTerm(term.head(), writer, depth - 1, isAnnotation);

            IStrategoList tail = term.tail();
            while (!tail.isEmpty()) {
                if (tailHasTermAttributes(tail, isAnnotation)) {
                    // We have to print annotations and/or attachments, so we will instead display the tail
                    // as a new list with its own annotations/attachments.
                    writer.append(LIST_TAIL_SEPARATOR);
                    // We want to write all heads at the same depth, so we don't decrease the depth here.
                    writeTerm(tail, writer, depth, isAnnotation);
                } else {
                    // We don't have to print annotations or attachments, or there are none.
                    writer.append(LIST_SEPARATOR);
                    writeTerm(tail.head(), writer, depth - 1, isAnnotation);
                }
                tail = tail.tail();
            }
        }
        writer.append(']');
    }

    /**
     * Writes a tuple term.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeTupleBody(IStrategoTuple term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        writer.append('(');
        writeSubterms(term, writer, depth - 1, isAnnotation);
        writer.append(')');
    }

    /**
     * Writes an Int term.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeIntBody(IStrategoInt term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        writer.append(Integer.toString(term.intValue()));
    }

    /**
     * Writes a Real term.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeRealBody(IStrategoReal term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        writer.append(Double.toString(term.realValue()));
    }

    /**
     * Writes a String term.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeStringBody(IStrategoString term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        writer.append('"');
        writer.append(StringUtils.escape(term.stringValue()));
        writer.append('"');
    }

    /**
     * Writes a Ref term.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeRefBody(IStrategoRef term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        throw new RuntimeException("REF terms are not supported.");
    }

    /**
     * Writes a placeholder term.
     *
     * @param term         the term to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writePlaceholderBody(IStrategoPlaceholder term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        writer.append('<');
        writeTerm(term.getTemplate(), writer, maxDepth - 1, isAnnotation);
        writer.append('>');
    }

    /**
     * Writes the annotations of a term.
     *
     * @param term         the term whose annotations to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeAnnotations(IStrategoTerm term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        IStrategoList annos = term.getAnnotations();
        if (depth <= 1 || annos.isEmpty()) {
            return;
        }

        writer.append('{');
        writeSubterms(annos, writer, depth - 1, true);
        writer.append('}');
    }

    /**
     * Writes the attachments of a term.
     *
     * @param term         the term whose attachments to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    protected void writeAttachments(IStrategoTerm term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        ITermAttachment attachment = term.getAttachment(null);
        if (depth <= 1 || attachment == null) {
            return;
        }

        writer.append('«');
        writeAttachment(attachment, writer, depth - 1);
        attachment = attachment.getNext();
        while (attachment != null) {
            writer.append(LIST_SEPARATOR);
            writeAttachment(attachment, writer, depth - 1);
            attachment = attachment.getNext();
        }
        writer.append('»');
    }

    /**
     * Writes a term attachment.
     *
     * @param attachment the term attachment to write
     * @param writer     the writer to write to
     * @param depth      the current depth remaining
     * @throws IOException an I/O exception occurred
     */
    protected void writeAttachment(ITermAttachment attachment, Appendable writer, int depth) throws IOException {
        writer.append(attachment.getClass().getSimpleName());
        writer.append('<');
        writer.append(attachment.toString());
        writer.append('>');
    }

    /**
     * Writes a list of subterms.
     *
     * @param term         the term whose subterms to write
     * @param writer       the writer to write to
     * @param depth        the current depth remaining for the subterms
     * @param isAnnotation whether the current term is an annotation term
     * @throws IOException an I/O exception occurred
     */
    private void writeSubterms(IStrategoTerm term, Appendable writer, int depth, boolean isAnnotation) throws IOException {
        Iterator<IStrategoTerm> iterator = term.iterator();
        if(iterator.hasNext()) {
            writeTerm(iterator.next(), writer, depth, isAnnotation);
            while(iterator.hasNext()) {
                writer.append(LIST_SEPARATOR);
                writeTerm(iterator.next(), writer, depth, isAnnotation);
            }
        }
    }

    /**
     * Determines whether the term has annotations or attachments that must be written.
     *
     * @param term         the term to check
     * @param isAnnotation whether the current term is an annotation term
     * @return {@code true} when the term has annotations or attachments that must be written;
     * otherwise, {@code false}
     */
    private boolean tailHasTermAttributes(IStrategoList term, boolean isAnnotation) {
        // @formatter:off
        return !this.ignoreListTailAttributes
                || termHasAnnotations(term, isAnnotation)
                || termHasAttachments(term, isAnnotation);
        // @formatter:on
    }

    /**
     * Determines whether the term has annotations that must be written.
     *
     * @param term         the term to check
     * @param isAnnotation whether the current term is an annotation term
     * @return {@code true} when the term has annotations that must be written;
     * otherwise, {@code false}
     */
    private boolean termHasAnnotations(IStrategoTerm term, boolean isAnnotation) {
        return !(this.ignoreAnnotations || (this.ignoreAnnotationAttributes && isAnnotation) || term.getAnnotations().isEmpty());
    }

    /**
     * Determines whether the term has attachments that must be written.
     *
     * @param term         the term to check
     * @param isAnnotation whether the current term is an annotation term
     * @return {@code true} when the term has attachments that must be written;
     * otherwise, {@code false}
     */
    private boolean termHasAttachments(IStrategoTerm term, boolean isAnnotation) {
        return !(this.ignoreAttachments || (this.ignoreAnnotationAttributes && isAnnotation) || term.getAttachment(null) == null);
    }

    /**
     * Writes an elided term.
     * <p>
     * The default implementation writes an ellipsis.
     *
     * @param writer the writer to write to
     * @throws IOException an I/O exception occurred
     */
    protected void writeElided(Appendable writer) throws IOException {
        writer.append(ELLIPSIS);
    }

}
