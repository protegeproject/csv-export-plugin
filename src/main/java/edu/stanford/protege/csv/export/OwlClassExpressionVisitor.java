package edu.stanford.protege.csv.export;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Rafael Gonçalves <br>
 * Center for Biomedical Informatics Research <br>
 * Stanford University
 */
public class OwlClassExpressionVisitor implements OWLClassExpressionVisitor {
    private OWLProperty property;
    private OWLObject filler;

    /**
     * No-arguments constructor
     */
    public OwlClassExpressionVisitor() { }

    public Optional<OWLEntity> getProperty() {
        return Optional.ofNullable(property);
    }

    public Optional<OWLObject> getFiller() {
        return Optional.ofNullable(filler);
    }

    @Override
    public void visit(@Nonnull OWLClass owlClass) {
        property = null;
        filler = null;
    }

    @Override
    public void visit(@Nonnull OWLObjectIntersectionOf owlObjectIntersectionOf) {
        property = null;
        filler = null;
    }

    @Override
    public void visit(@Nonnull OWLObjectUnionOf owlObjectUnionOf) {
        property = null;
        filler = null;
    }

    @Override
    public void visit(@Nonnull OWLObjectComplementOf owlObjectComplementOf) {
        property = null;
        filler = null;
    }

    @Override
    public void visit(@Nonnull OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
        property = owlObjectSomeValuesFrom.getProperty().getNamedProperty();
        filler = owlObjectSomeValuesFrom.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLObjectAllValuesFrom owlObjectAllValuesFrom) {
        property = owlObjectAllValuesFrom.getProperty().getNamedProperty();
        filler = owlObjectAllValuesFrom.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLObjectHasValue owlObjectHasValue) {
        property = owlObjectHasValue.getProperty().getNamedProperty();
        filler = owlObjectHasValue.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLObjectMinCardinality owlObjectMinCardinality) {
        property = owlObjectMinCardinality.getProperty().getNamedProperty();
        filler = owlObjectMinCardinality.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLObjectExactCardinality owlObjectExactCardinality) {
        property = owlObjectExactCardinality.getProperty().getNamedProperty();
        filler = owlObjectExactCardinality.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLObjectMaxCardinality owlObjectMaxCardinality) {
        property = owlObjectMaxCardinality.getProperty().getNamedProperty();
        filler = owlObjectMaxCardinality.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLObjectHasSelf owlObjectHasSelf) {
        property = owlObjectHasSelf.getProperty().getNamedProperty();
        // TODO
    }

    @Override
    public void visit(@Nonnull OWLObjectOneOf owlObjectOneOf) {
        property = null;
        filler = null;
    }

    @Override
    public void visit(@Nonnull OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
        property = owlDataSomeValuesFrom.getProperty().asOWLDataProperty();
        filler = owlDataSomeValuesFrom.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLDataAllValuesFrom owlDataAllValuesFrom) {
        property = owlDataAllValuesFrom.getProperty().asOWLDataProperty();
        filler = owlDataAllValuesFrom.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLDataHasValue owlDataHasValue) {
        property = owlDataHasValue.getProperty().asOWLDataProperty();
        filler = owlDataHasValue.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLDataMinCardinality owlDataMinCardinality) {
        property = owlDataMinCardinality.getProperty().asOWLDataProperty();
        filler = owlDataMinCardinality.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLDataExactCardinality owlDataExactCardinality) {
        property = owlDataExactCardinality.getProperty().asOWLDataProperty();
        filler = owlDataExactCardinality.getFiller();
    }

    @Override
    public void visit(@Nonnull OWLDataMaxCardinality owlDataMaxCardinality) {
        property = owlDataMaxCardinality.getProperty().asOWLDataProperty();
        filler = owlDataMaxCardinality.getFiller();
    }
}
