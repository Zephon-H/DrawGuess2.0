/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: PlayMusic
 * Author:   Zephon
 * Date:     2018/12/21 8:22
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sample.Util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zephon
 * @create 2018/12/21
 * @since 1.0.0
 */
public class PlayMusic {
    private static PlayMusic instance;
    private PlayMusic(){
    }
    public static PlayMusic getInstance(){
        if(instance==null)instance = new PlayMusic();
        return instance;
    }
    MediaPlayer mp;
    public void setAddress(){
        String s2 = PlayMusic.class.getResource("../music/bg.mp3").toString();
        Media media2 = new Media(s2);
        mp = new MediaPlayer(media2);
    }
    public void play(){
        setAddress();
        mp.play();
    }
    public void pause(){
        mp.pause();
    }


}