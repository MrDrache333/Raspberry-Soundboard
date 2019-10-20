package editor;

import static editor.Debugger.printDebug;

/**
 * Project: Soundboard
 * Package: editor
 * Created by keno on 20.05.17.
 */
public class Profile {

    private sound Sounds[] = new sound[27];
    private int INDEX;
    private static int anz_Profiles = 0;

    /**
     * Instantiates a new Profile.
     */
    public Profile(){
        INDEX = anz_Profiles;
        anz_Profiles++;
    }

    /**
     * Get sound sound.
     *
     * @param INDEX the index
     * @return the sound
     */
    sound getSound(int INDEX){
        return Sounds[INDEX];
    }

    /**
     * Set sound.
     *
     * @param Sound the sound
     * @param INDEX the index
     */
    void setSound(sound Sound, int INDEX){
        Sounds[INDEX] = Sound;
    }

    /**
     * Get sounds sound [ ].
     *
     * @return the sound [ ]
     */
    sound[] getSounds() {
        return Sounds;
    }

    /**
     * Sets sounds.
     *
     * @param sounds the sounds
     */
    public void setSounds(sound[] sounds) {
        Sounds = sounds;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getINDEX() {
        return INDEX;
    }

    /**
     * Load.
     */
    void load(){
        
        int loadcount = 0;
        for (int i = 0; i < Controller.Buttons.length && i < Sounds.length;i++){
            try{
                if (this.Sounds[i].getSoundfile().exists()){
                    Controller.Buttons[i].setSound(this.Sounds[i]);
                    loadcount++;
                }else
                    Controller.Buttons[i].setSound(null);
            }catch(Exception e){
                Controller.Buttons[i].setSound(null);
            }
        }
        printDebug("INFO","Profile " + INDEX + " Loaded! Applied " + loadcount + " Sounds to Buttons");

        
    }
}
