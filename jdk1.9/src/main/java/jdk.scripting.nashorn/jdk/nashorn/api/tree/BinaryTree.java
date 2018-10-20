/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package jdk.nashorn.api.tree;

/**
 * A tree node for a binary expression.
 * Use {@link #getKind getKind} to determine the kind of operator.
 *
 * For example:
 * <pre>
 *   <em>leftOperand</em> <em>operator</em> <em>rightOperand</em>
 * </pre>
 *
 * @since 9
 */
public interface BinaryTree extends ExpressionTree {
    /**
     * Returns left hand side (LHS) of this binary expression.
     *
     * @return left hand side (LHS) of this binary expression
     */
    ExpressionTree getLeftOperand();

    /**
     * Returns right hand side (RHS) of this binary expression.
     *
     * @return right hand side (RHS) of this binary expression
     */
    ExpressionTree getRightOperand();
}
