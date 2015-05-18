package specification.core;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 *  Created by Tomas Hanus on 4/23/2015.
 */
public class DefaultValuesBundle {
    public static String value(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key) {
        return CommonBundle.message(getBundle(), key);
    }

    private static Reference<ResourceBundle> ourBundle;
    @NonNls
    private static final String BUNDLE = "designer.resources.bundleProperties.ElementDefaultValue";

    private DefaultValuesBundle() {
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = com.intellij.reference.SoftReference.dereference(ourBundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }
}