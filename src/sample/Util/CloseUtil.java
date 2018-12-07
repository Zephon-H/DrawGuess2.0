/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CloseUtil
 * Author:   Zephon
 * Date:     2018/11/24 22:01
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.Util;

import java.io.Closeable;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zephon
 * @create 2018/11/24
 * @since 1.0.0
 */
public class CloseUtil {
    public static void closeAll(Closeable... io){
        for(Closeable temp:io){
                try {
                    if(null!=temp) {
                        temp.close();
                    }
                } catch (Exception e) {
                   // e.printStackTrace();
                }
            }
        }
    }
