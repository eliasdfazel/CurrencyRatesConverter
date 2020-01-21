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

/**
 * Beans that support customized output of JSON text shall implement this interface.  
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface JSONAware {
	/**
	 * @return JSON text
	 */
	String toJSONString();
}
