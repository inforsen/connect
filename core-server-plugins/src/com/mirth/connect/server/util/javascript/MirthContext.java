/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package com.mirth.connect.server.util.javascript;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

public class MirthContext extends Context {
    private final static int INSTRUCTION_THRESHOLD = 1;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private Logger logger = LogManager.getLogger(this.getClass());
    private ScriptableObject sealedSharedScope;

    public MirthContext(IMirthContextFactory contextFactory) {
        this(contextFactory, Context.VERSION_DEFAULT);
    }

    public MirthContext(IMirthContextFactory contextFactory, int languageVersion) {
        super((ContextFactory) contextFactory);
        setInstructionObserverThreshold(INSTRUCTION_THRESHOLD);
        sealedSharedScope = contextFactory.getSealedSharedScope();
        setLanguageVersion(languageVersion);
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }

    protected ScriptableObject getSealedSharedScope() {
        return sealedSharedScope;
    }

    @Override
    protected void observeInstructionCount(int count) {
        if (!running.get()) {
            logger.debug("Halting JavaScript execution");
            throw new Error();
        }
    }
}
