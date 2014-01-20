/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.mongo.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.callback.SourceCallback;

import com.deftlabs.cursor.mongo.TailableCursorDocListener;
import com.mongodb.DBObject;

public class TailableCursorDocListenerImpl implements TailableCursorDocListener {
	private static final Log logger = LogFactory.getLog(TailableCursorDocListenerImpl.class);
    private final SourceCallback callback;

    public TailableCursorDocListenerImpl(final SourceCallback callback) {
    	this.callback = callback;
    }
    
	@Override
	public void nextDoc(DBObject pDoc) {
		try{
			if( logger.isDebugEnabled() )
				logger.debug(pDoc.toString());

			callback.process(pDoc);
		}catch(Exception e){
			logger.error(e);
		}
	}
}
