package com.ksoft.dd_processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import com.ksoft.dd.Column;
import com.ksoft.dd.TableName;

public class TableVisitor implements ElementVisitor<Boolean, TableData> {
	final Messager msg;
	final Types types;

	TableVisitor(Messager msg, Types typs) {
		this.msg = msg;
		types = typs;
	}

	@Override
	public Boolean visit(Element e, TableData p) {
		if (e != null) {
			// msg.printMessage(Kind.WARNING, "Visited: " + e + " (" +
			// e.getClass() + ")", r);
		}
		return null;
	}

	@Override
	public Boolean visit(Element e) {
		return visit(e, null);
	}

	@Override
	public Boolean visitPackage(PackageElement e, TableData p) {
		visit(e);
		return null;
	}

	@Override
	public Boolean visitType(TypeElement e, TableData p) {
		visit(e);

		for (TypeMirror face : e.getInterfaces()) {
			Boolean ret = ((TypeElement) types.asElement(face)).accept(this, p);
			if (ret != Boolean.TRUE) {
				msg.printMessage(
						Kind.WARNING,
						"There were no columns in an implemented Interface: this will likely cause an incorrect database generation. Please do a \"clean\" in order to build correctly.",
						e);
			}
		}

		Boolean hadAtLeastOneColumn = false;
		for (Element enlc : e.getEnclosedElements()) {
			Boolean ret = enlc.accept(this, p);
			if (ret == Boolean.TRUE) {
				hadAtLeastOneColumn = Boolean.TRUE;
			}
		}
		return hadAtLeastOneColumn;
	}

	/**
	 * Returns true if the foreign key constraint is specified in a valid way.
	 * 
	 * @param column
	 * @return
	 */
	private boolean validateForeignKey(Column column) {
		boolean keyColumnEmpty = column.foreignKeyColumn().isEmpty();
		boolean keyTableEmpty = column.foreignKeyTable().isEmpty();
		return !(!keyColumnEmpty && keyTableEmpty);
	}

	@Override
	public Boolean visitVariable(VariableElement e, TableData p) {
		Object val = e.getConstantValue();

		TableName name = e.getAnnotation(TableName.class);
		Column col = e.getAnnotation(Column.class);

		if (name != null) {
			p.name = val == null ? null : val.toString();
		} else if (col != null) {

			// msg.printMessage(Kind.WARNING, "Column: " + e, r);
			if (val == null) {
				msg.printMessage(
						Kind.ERROR,
						"A column in a table must have a constant String value of the columns name!",
						e);
			} else if (!validateForeignKey(col)) {
				msg.printMessage(
						Kind.ERROR,
						"Foreign key constraint is invalid. If column is specified, table must be as well.");
			} else if (p.putNewColumn(val.toString(), col)) {
				msg.printMessage(Kind.ERROR,
						"This column name was already used in this table!", e);
			} else {
				// Success!
				return true;
			}
		}
		return false;
	}

	@Override
	public Boolean visitExecutable(ExecutableElement e, TableData p) {
		visit(e);
		return null;
	}

	@Override
	public Boolean visitTypeParameter(TypeParameterElement e, TableData p) {
		visit(e);
		return null;
	}

	@Override
	public Boolean visitUnknown(Element e, TableData p) {
		visit(e);
		return null;
	}

}
