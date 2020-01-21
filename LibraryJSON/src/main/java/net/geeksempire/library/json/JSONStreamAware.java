/*
 * Copyright © 2020 By A Geek.
 *
 * Created by Elias Fazel on 1/9/20 8:28 AM
 * Last modified 10/15/19 9:29 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.library.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Beans that support customized output of JSON text to a writer shall implement this interface.  
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface JSONStreamAware {
	/**
	 * write JSON string to out.
	 */
	void writeJSONString(Writer out) throws IOException;
}
