/*
 * Copyright (c) 1996, 2014, Oracle and/or its affiliates. All rights reserved.
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
package sun.awt.windows;

import java.awt.*;
import java.awt.peer.*;

import sun.awt.*;
import sun.awt.im.*;

final class WDialogPeer extends WWindowPeer implements DialogPeer {
    // Toolkit & peer internals

    // Platform default background for dialogs.  Gets set on target if
    // target has none explicitly specified.
    static final Color defaultBackground =  SystemColor.control;

    WDialogPeer(Dialog target) {
        super(target);

        InputMethodManager imm = InputMethodManager.getInstance();
        String menuString = imm.getTriggerMenuString();
        if (menuString != null)
        {
            pSetIMMOption(menuString);
        }
    }

    native void createAwtDialog(WComponentPeer parent);
    @Override
    void create(WComponentPeer parent) {
        preCreate(parent);
        createAwtDialog(parent);
    }

    native void showModal();
    native void endModal();

    @Override
    void initialize() {
        Dialog target = (Dialog)this.target;
        // Need to set target's background to default _before_ a call
        // to super.initialize.
        if (!target.isBackgroundSet()) {
            target.setBackground(defaultBackground);
        }

        super.initialize();

        if (target.getTitle() != null) {
            setTitle(target.getTitle());
        }
        setResizable(target.isResizable());
    }

    @Override
    protected void realShow() {
        Dialog dlg = (Dialog)target;
        if (dlg.getModalityType() != Dialog.ModalityType.MODELESS) {
            showModal();
        } else {
            super.realShow();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    void hide() {
        Dialog dlg = (Dialog)target;
        if (dlg.getModalityType() != Dialog.ModalityType.MODELESS) {
            endModal();
        } else {
            super.hide();
        }
    }

    @Override
    public void blockWindows(java.util.List<Window> toBlock) {
        for (Window w : toBlock) {
            WWindowPeer wp = AWTAccessor.getComponentAccessor().getPeer(w);
            if (wp != null) {
                wp.setModalBlocked((Dialog)target, true);
            }
        }
    }

    @Override
    public Dimension getMinimumSize() {
        if (((Dialog)target).isUndecorated()) {
            return super.getMinimumSize();
        } else {
            return new Dimension(getSysMinWidth(), getSysMinHeight());
        }
    }

    @Override
    boolean isTargetUndecorated() {
        return ((Dialog)target).isUndecorated();
    }

    @Override
    public void reshape(int x, int y, int width, int height) {
        if (((Dialog)target).isUndecorated()) {
            super.reshape(x, y, width, height);
        } else {
            reshapeFrame(x, y, width, height);
        }
    }

    native void pSetIMMOption(String option);
    void notifyIMMOptionChange(){
      InputMethodManager.getInstance().notifyChangeRequest((Component)target);
    }
}
