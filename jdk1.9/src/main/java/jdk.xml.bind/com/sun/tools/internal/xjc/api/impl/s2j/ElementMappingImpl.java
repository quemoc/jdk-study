/*
 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.tools.internal.xjc.api.impl.s2j;

import java.util.List;

import com.sun.tools.internal.xjc.api.Property;
import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import com.sun.tools.internal.xjc.model.CAdapter;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CElementInfo;
import com.sun.tools.internal.xjc.model.CElementPropertyInfo;
import com.sun.tools.internal.xjc.model.CTypeInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.model.TypeUseFactory;

/**
 * @author Kohsuke Kawaguchi
 */
final class ElementMappingImpl extends AbstractMappingImpl<CElementInfo> {

    private final TypeAndAnnotation taa;

    protected ElementMappingImpl(JAXBModelImpl parent, CElementInfo elementInfo) {
        super(parent,elementInfo);

        TypeUse t = clazz.getContentType();
        if(clazz.getProperty().isCollection())
            t = TypeUseFactory.makeCollection(t);
        CAdapter a = clazz.getProperty().getAdapter();
        if(a!=null)
            t = TypeUseFactory.adapt(t,a);
        taa = new TypeAndAnnotationImpl(parent.outline,t);
    }

    public TypeAndAnnotation getType() {
        return taa;
    }

    public final List<Property> calcDrilldown() {
        CElementPropertyInfo p = clazz.getProperty();

        if(p.getAdapter()!=null)
            return null;    // if adapted, avoid drill down

        if(p.isCollection())
        // things like <xs:element name="foo" type="xs:NMTOKENS" /> is not eligible.
            return null;

        CTypeInfo typeClass = p.ref().get(0);

        if(!(typeClass instanceof CClassInfo))
            // things like <xs:element name="foo" type="xs:string" /> is not eligible.
            return null;

        CClassInfo ci = (CClassInfo)typeClass;

        // if the type is abstract we can't use it.
        if(ci.isAbstract())
            return null;

        // the 'all' compositor doesn't qualify
        if(!ci.isOrdered())
            return null;

        return buildDrilldown(ci);
    }
}
