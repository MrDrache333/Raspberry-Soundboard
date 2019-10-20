package editor;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Project: Soundboard
 * Package: editor
 * Created by keno on 20.05.17.
 */
public class Button {

    private boolean pressed;
    private ImageView Image;
    private sound Sound;
    private boolean isProfileButton;
    private int ProfileIndex;

    /**
     * Instantiates a new Button.
     *
     * @param imageView the image view
     */
    public Button(ImageView imageView){
        pressed = false;
        Image = imageView;
        Sound = new sound(null,null);
    }

    /**
     * Instantiates a new Button.
     *
     * @param imageView       the image view
     * @param isprofilebutton the isprofilebutton
     * @param profileindex    the profileindex
     */
    public Button(ImageView imageView, boolean isprofilebutton, int profileindex){
        pressed = false;
        Image = imageView;
        isProfileButton = isprofilebutton;
        ProfileIndex = profileindex;
    }

    /**
     * Play.
     */
    void play(){
        if (Sound.getSoundfile().exists()){

            Sound.play();

        }else{
            //TODO Sound abspielen wenn keiner vorhanden
        }
    }

    /**
     * Stop.
     */
    void stop(){
        this.Sound.stop();
    }

    /**
     * Isplaying boolean.
     *
     * @return the boolean
     */
    boolean isplaying(){
        return this.Sound.isPlaying();
    }

    /**
     * Is pressed boolean.
     *
     * @return the boolean
     */
    public boolean isPressed() {
        return this.pressed;
    }

    /**
     * Sets pressed.
     *
     * @param pressed the pressed
     * @throws MalformedURLException the malformed url exception
     */
    void setPressed(boolean pressed) throws MalformedURLException {
        this.pressed = pressed;

        if (pressed){
            if (!isProfileButton)
                this.Image.setImage(new Image(Controller.BUTTONPRESSEDIMAGE.toURI().toURL().toString()));
            else
                this.Image.setImage(new Image(Controller.BUTTONPRESSEDIMAGE_PROFILE.toURI().toURL().toString()));
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (!isProfileButton)
                            Image.setImage(new Image(Controller.BUTTONIMAGE.toURI().toURL().toString()));
                        else
                            Image.setImage(new Image(Controller.BUTTONIMAGE_PROFILE.toURI().toURL().toString()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    this.cancel();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task,500);
            pressed = false;
        }else{
            if (!isProfileButton)
                this.Image.setImage(new Image(Controller.BUTTONIMAGE.toURI().toURL().toString()));
            else
                this.Image.setImage(new Image(Controller.BUTTONIMAGE_PROFILE.toURI().toURL().toString()));
        }

    }

    /**
     * Gets image view.
     *
     * @return the image view
     */
    ImageView getImageView() {
        return Image;
    }

    /**
     * Sets image.
     *
     * @param image the image
     * @throws MalformedURLException the malformed url exception
     */
    public void setImage(File image) throws MalformedURLException {
        Image.setImage(new Image(image.toURI().toURL().toString()));
    }

    /**
     * Gets sound.
     *
     * @return the sound
     */
    public sound getSound() {
        return Sound;
    }

    /**
     * Sets sound.
     *
     * @param sound the sound
     */
    public void setSound(sound sound) {
        Sound = sound;
    }

    /**
     * Is profile button boolean.
     *
     * @return the boolean
     */
    boolean isProfileButton() {
        return isProfileButton;
    }

    /**
     * Sets profile button.
     *
     * @param profileButton the profile button
     */
    void setProfileButton(boolean profileButton) {
        isProfileButton = profileButton;
    }

    /**
     * Gets profile index.
     *
     * @return the profile index
     */
    int getProfileIndex() {
        return ProfileIndex;
    }

    /**
     * Sets profile index.
     *
     * @param profileButtonIndex the profile button index
     */
    void setProfileIndex(int profileButtonIndex) {
        ProfileIndex = profileButtonIndex;
    }
}
