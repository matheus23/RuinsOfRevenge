package org.matheusdev.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created with IntelliJ IDEA.
 * Author: matheusdev
 * Date: 3/14/13
 * Time: 8:26 PM
 */
public enum FileLocation {
    CLASSPATH,
    INTERNAL,
    LOCAL,
    EXTERNAL,
    ABSOLUTE;

    public FileHandle getFile(String path) {
        switch (this) {
            case CLASSPATH: return Gdx.files.classpath(path);
            case INTERNAL: return Gdx.files.internal(path);
            case LOCAL: return Gdx.files.local(path);
            case EXTERNAL: return Gdx.files.external(path);
            case ABSOLUTE: return Gdx.files.absolute(path);
            default: return null;
        }
    }
}